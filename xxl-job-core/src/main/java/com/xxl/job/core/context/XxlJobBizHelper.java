package com.xxl.job.core.context;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.XxlJobInfo;
import com.xxl.job.core.biz.model.XxlJobInfoParam;
import com.xxl.job.core.biz.model.XxlJobKey;
import com.xxl.job.core.exception.XxlJobException;
import com.xxl.job.core.executor.XxlJobExecutor;

import java.util.List;

/**
 * @author: YangWeiDong
 * @date: 2021/05/30 14:03
 */
public class XxlJobBizHelper {

    public static int addJob(XxlJobInfo jobInfo){
        if (jobInfo.getExecutorParam() == null){
            jobInfo.setExecutorParam("");
        }
        ReturnT<String> returnT = getAdminBiz().addJob(jobInfo);
        handleReturn(returnT);
        return Integer.valueOf(returnT.getContent());
    }

    public static int addJobByKey(XxlJobInfoParam jobInfo){
        if (jobInfo.getExecutorParam() == null){
            jobInfo.setExecutorParam("");
        }
        ReturnT<String> returnT = getAdminBiz().addJobByKey(jobInfo);
        handleReturn(returnT);
        return Integer.valueOf(returnT.getContent());
    }

    public static void updateJob(XxlJobInfo jobInfo){
        if (jobInfo.getExecutorParam() == null){
            jobInfo.setExecutorParam("");
        }
        ReturnT<String> returnT = getAdminBiz().updateJob(jobInfo);
        handleReturn(returnT);
    }

    public static void updateJobByKey(XxlJobInfoParam jobInfo){
        if (jobInfo.getExecutorParam() == null){
            jobInfo.setExecutorParam("");
        }
        ReturnT<String> returnT = getAdminBiz().updateJobByKey(jobInfo);
        handleReturn(returnT);
    }

    public static void removeJob(int id){
        ReturnT<String> returnT = getAdminBiz().removeJob(id);
        handleReturn(returnT);
    }

    public static void removeJobByKey(XxlJobKey key){
        ReturnT<String> returnT = getAdminBiz().removeJobByKey(key);
        handleReturn(returnT);
    }

    public static void pauseJob(int id){
        ReturnT<String> returnT = getAdminBiz().pauseJob(id);
        handleReturn(returnT);
    }

    public static void pauseJobByKey(XxlJobKey key){
        ReturnT<String> returnT = getAdminBiz().pauseJobByKey(key);
        handleReturn(returnT);
    }

    public static void startJob(int id){
        ReturnT<String> returnT = getAdminBiz().startJob(id);
        handleReturn(returnT);
    }

    public static void startJobByKey(XxlJobKey key){
        ReturnT<String> returnT = getAdminBiz().startJobByKey(key);
        handleReturn(returnT);
    }

    public static int addAndStartJob(XxlJobInfo jobInfo){
        if (jobInfo.getExecutorParam() == null){
            jobInfo.setExecutorParam("");
        }
        ReturnT<String> returnT = getAdminBiz().addAndStartJob(jobInfo);
        handleReturn(returnT);
        return Integer.valueOf(returnT.getContent());
    }

    public static int addAndStartJobByKey(XxlJobInfoParam jobInfo){
        if (jobInfo.getExecutorParam() == null){
            jobInfo.setExecutorParam("");
        }
        ReturnT<String> returnT = getAdminBiz().addAndStartJobByKey(jobInfo);
        handleReturn(returnT);
        return Integer.valueOf(returnT.getContent());
    }

    private static AdminBiz getAdminBiz(){
        List<AdminBiz> clients = XxlJobExecutor.getAdminBizList();
        if (clients == null || clients.size() < 1){
            throw new XxlJobException("adminBiz not found");
        }
        return clients.get(0);
    }

    private static void handleReturn(ReturnT<String> result){
        if (result.getCode() != ReturnT.SUCCESS_CODE){
            throw new XxlJobException(result.getMsg());
        }
    }
}
