package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.core.biz.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.MisfireStrategyEnum;
import com.xxl.job.admin.core.scheduler.ScheduleTypeEnum;
import com.xxl.job.admin.core.thread.JobScheduleHelper;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.XxlJobInfoParam;
import com.xxl.job.core.biz.model.XxlJobKey;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	private XxlJobService xxlJobService;
	
	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // 阻塞处理策略-字典
		model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());	    				// 调度类型
		model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());	    			// 调度过期策略

		// 执行器列表
		List<XxlJobGroup> jobGroupList_all =  xxlJobGroupDao.findAll();

		// filter group
		List<XxlJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupList_all);
		if (jobGroupList==null || jobGroupList.size()==0) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		return "jobinfo/jobinfo.index";
	}

	public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all){
		List<XxlJobGroup> jobGroupList = new ArrayList<>();
		if (jobGroupList_all!=null && jobGroupList_all.size()>0) {
			XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
			if (loginUser.getRole() == 1) {
				jobGroupList = jobGroupList_all;
			} else {
				List<String> groupIdStrs = new ArrayList<>();
				if (loginUser.getPermission()!=null && loginUser.getPermission().trim().length()>0) {
					groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
				}
				for (XxlJobGroup groupItem:jobGroupList_all) {
					if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
						jobGroupList.add(groupItem);
					}
				}
			}
		}
		return jobGroupList;
	}
	public static void validPermission(HttpServletRequest request, int jobGroup) {
		XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
		if (!loginUser.validPermission(jobGroup)) {
			throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username="+ loginUser.getUsername() +"]");
		}
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
		
		return xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
	}
	
	@RequestMapping("/add")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		return xxlJobService.add(jobInfo);
	}

	@RequestMapping("/key/add")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> addKey(XxlJobInfoParam jobInfo) {
		Connection conn = null;
		Boolean connAutoCommit = null;
		PreparedStatement preparedStatement = null;

		try {
			conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
			connAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			preparedStatement = conn.prepareStatement("SELECT * FROM xxl_job_group AS t WHERE t.app_name = ? for update");
			preparedStatement.setString(1, jobInfo.getJobGroupName());
			preparedStatement.execute();

			jobInfo.setJobGroup(getGroupId(jobInfo.getJobGroupName()));
			ReturnT<String> exist = jobInfoExist(jobInfo.getJobGroupName(), jobInfo.getExecutorHandler(), jobInfo.getExecutorParam());
			if (exist.getCode() != ReturnT.SUCCESS_CODE) {
				return exist;
			}
			return xxlJobService.add(jobInfo);
		} catch (SQLException e){
			return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.commit();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				try {
					conn.setAutoCommit(connAutoCommit);
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}

			if (null != preparedStatement) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	@RequestMapping("/update")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> update(XxlJobInfo jobInfo) {
		return xxlJobService.update(jobInfo);
	}

	@RequestMapping("/key/update")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> updateKey(XxlJobInfoParam jobInfo) {
		Connection conn = null;
		Boolean connAutoCommit = null;
		PreparedStatement preparedStatement = null;

		try {
			conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
			connAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			preparedStatement = conn.prepareStatement("SELECT * FROM xxl_job_group AS t WHERE t.app_name = ? for update");
			preparedStatement.setString(1, jobInfo.getJobGroupName());
			preparedStatement.execute();

			jobInfo.setJobGroup(getGroupId(jobInfo.getJobGroupName()));
			ReturnT<XxlJobInfo> info = getJobInfo(new XxlJobKey(jobInfo.getJobGroupName(), jobInfo.getExecutorHandler(), jobInfo.getExecutorParam()));
			if (info.getCode() != ReturnT.SUCCESS_CODE){
				return new ReturnT<>(info.getCode(), info.getMsg());
			}
			jobInfo.setId(info.getContent().getId());
			return xxlJobService.update(jobInfo);
		} catch (SQLException e){
			return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.commit();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				try {
					conn.setAutoCommit(connAutoCommit);
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}

			if (null != preparedStatement) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	@RequestMapping("/remove")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> remove(int id) {
		return xxlJobService.remove(id);
	}

	@RequestMapping("/key/remove")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> removeKey(XxlJobKey key) {
		ReturnT<XxlJobInfo> info = getJobInfo(key);
		if (info.getCode() != ReturnT.SUCCESS_CODE){
			return new ReturnT<>(info.getCode(), info.getMsg());
		}
		return xxlJobService.remove(info.getContent().getId());
	}
	
	@RequestMapping("/stop")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> pause(int id) {
		return xxlJobService.stop(id);
	}

	@RequestMapping("/key/stop")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> pauseKey(XxlJobKey key) {
		ReturnT<XxlJobInfo> info = getJobInfo(key);
		if (info.getCode() != ReturnT.SUCCESS_CODE){
			return new ReturnT<>(info.getCode(), info.getMsg());
		}
		return xxlJobService.stop(info.getContent().getId());
	}
	
	@RequestMapping("/start")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> start(int id) {
		return xxlJobService.start(id);
	}

	@RequestMapping("/key/start")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> startKey(XxlJobKey key) {
		ReturnT<XxlJobInfo> info = getJobInfo(key);
		if (info.getCode() != ReturnT.SUCCESS_CODE){
			return new ReturnT<>(info.getCode(), info.getMsg());
		}
		return xxlJobService.start(info.getContent().getId());
	}

	@RequestMapping("/addAndStart")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> addAndStart(XxlJobInfo jobInfo) {
		ReturnT<String> addResult = xxlJobService.add(jobInfo);
		if (addResult.getCode() == ReturnT.FAIL_CODE){
			return addResult;
		}
		int id = Integer.valueOf(addResult.getContent());
		ReturnT<String> startResult = xxlJobService.start(id);
		if (startResult.getCode() == ReturnT.FAIL_CODE){
			return startResult;
		}
		return addResult;
	}

	@RequestMapping("/key/addAndStart")
	@PermissionLimit(limit=false)
	@ResponseBody
	public ReturnT<String> addAndStartKey(XxlJobInfoParam jobInfo) {
		Connection conn = null;
		Boolean connAutoCommit = null;
		PreparedStatement preparedStatement = null;

		try {
			conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
			connAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);

			preparedStatement = conn.prepareStatement("SELECT * FROM xxl_job_group AS t WHERE t.app_name = ? for update");
			preparedStatement.setString(1, jobInfo.getJobGroupName());
			preparedStatement.execute();

			jobInfo.setJobGroup(getGroupId(jobInfo.getJobGroupName()));
			ReturnT<String> exist = jobInfoExist(jobInfo.getJobGroupName(), jobInfo.getExecutorHandler(), jobInfo.getExecutorParam());
			if (exist.getCode() != ReturnT.SUCCESS_CODE){
				return exist;
			}
			ReturnT<String> addResult = xxlJobService.add(jobInfo);
			if (addResult.getCode() == ReturnT.FAIL_CODE){
				return addResult;
			}
			int id = Integer.valueOf(addResult.getContent());
			ReturnT<String> startResult = xxlJobService.start(id);
			if (startResult.getCode() == ReturnT.FAIL_CODE){
				return startResult;
			}
			return addResult;
		} catch (SQLException e){
			return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.commit();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				try {
					conn.setAutoCommit(connAutoCommit);
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}

			if (null != preparedStatement) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	//@PermissionLimit(limit = false)
	public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
		// force cover job param
		if (executorParam == null) {
			executorParam = "";
		}

		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
		return ReturnT.SUCCESS;
	}

	@RequestMapping("/nextTriggerTime")
	@ResponseBody
	public ReturnT<List<String>> nextTriggerTime(String scheduleType, String scheduleConf) {

		XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
		paramXxlJobInfo.setScheduleType(scheduleType);
		paramXxlJobInfo.setScheduleConf(scheduleConf);

		List<String> result = new ArrayList<>();
		try {
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {
				lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
				if (lastTime != null) {
					result.add(DateUtil.formatDateTime(lastTime));
				} else {
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<List<String>>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) + e.getMessage());
		}
		return new ReturnT<List<String>>(result);

	}

	private ReturnT<XxlJobInfo> getJobInfo(XxlJobKey key){
		if (key == null){
			return new ReturnT<XxlJobInfo>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"param") );
		}
		if (key.getJobGroup() == null || key.getJobGroup().length() == 0){
			return new ReturnT<XxlJobInfo>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"jobGroup") );
		}
		if (key.getExecutorHandler() == null || key.getExecutorHandler().length() == 0){
			return new ReturnT<XxlJobInfo>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"executorHandler") );
		}
		if (key.getExecutorParam() == null){
			return new ReturnT<XxlJobInfo>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"executorParam") );
		}

		List<XxlJobInfo> infos = xxlJobInfoDao.loadByKey(key.getJobGroup(), key.getExecutorHandler(), key.getExecutorParam());
		if (infos.size() == 0){
			return new ReturnT<XxlJobInfo>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_job")+I18nUtil.getString("system_not_found")) );
		}
		if (infos.size() > 1){
			return new ReturnT<XxlJobInfo>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_job")+I18nUtil.getString("system_not_unique")) );
		}
		return new ReturnT<XxlJobInfo>(infos.get(0));
	}

	private ReturnT<String> jobInfoExist(String jobGroup, String executorHandler, String executorParam){
		if (jobGroup == null || jobGroup.length() == 0){
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"jobGroup") );
		}
		if (executorHandler == null || executorHandler.length() == 0){
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"executorHandler") );
		}
		if (executorParam == null){
			return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("system_please_input")+"executorParam") );
		}

		List<XxlJobInfo> infos = xxlJobInfoDao.loadByKey(jobGroup, executorHandler, executorParam);
		if (infos.size() == 0){
			return ReturnT.SUCCESS;
		}
		return new ReturnT<String>(ReturnT.FAIL_CODE, (I18nUtil.getString("jobinfo_job")+I18nUtil.getString("system_already_exists")) );
	}

	private int getGroupId(String appname){
		XxlJobGroup group = xxlJobGroupDao.loadByAppname(appname);
		return group==null?0:group.getId();
	}
	
}
