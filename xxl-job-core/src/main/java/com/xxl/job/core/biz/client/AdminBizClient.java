package com.xxl.job.core.biz.client;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.util.XxlJobRemotingUtil;

import java.util.List;

/**
 * admin api test
 *
 * @author xuxueli 2017-07-28 22:14:52
 */
public class AdminBizClient implements AdminBiz {

    public AdminBizClient() {
    }
    public AdminBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    }

    private String addressUrl ;
    private String accessToken;
    private int timeout = 3;


    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        return XxlJobRemotingUtil.postBody(addressUrl+"api/callback", accessToken, timeout, callbackParamList, String.class);
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registry", accessToken, timeout, registryParam, String.class);
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registryRemove", accessToken, timeout, registryParam, String.class);
    }

    @Override
    public ReturnT<String> addJob(XxlJobInfo jobInfo) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/add", accessToken, timeout, getJobInfoFormData(jobInfo), String.class);
    }

    @Override
    public ReturnT<String> addJobByKey(XxlJobInfoParam jobInfo) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/key/add", accessToken, timeout, getJobInfoParamFormData(jobInfo), String.class);
    }

    @Override
    public ReturnT<String> updateJob(XxlJobInfo jobInfo) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/update", accessToken, timeout, getJobInfoFormData(jobInfo), String.class);
    }

    @Override
    public ReturnT<String> updateJobByKey(XxlJobInfoParam jobInfo) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/key/update", accessToken, timeout, getJobInfoParamFormData(jobInfo), String.class);
    }

    @Override
    public ReturnT<String> removeJob(int id) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/remove", accessToken, timeout, getIdFormData(id), String.class);
    }

    @Override
    public ReturnT<String> removeJobByKey(XxlJobKey key) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/key/remove", accessToken, timeout, getJobKeyFormData(key), String.class);
    }

    @Override
    public ReturnT<String> pauseJob(int id) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/stop", accessToken, timeout, getIdFormData(id), String.class);
    }

    @Override
    public ReturnT<String> pauseJobByKey(XxlJobKey key) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/key/stop", accessToken, timeout, getJobKeyFormData(key), String.class);
    }

    @Override
    public ReturnT<String> startJob(int id) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/start", accessToken, timeout, getIdFormData(id), String.class);
    }

    @Override
    public ReturnT<String> startJobByKey(XxlJobKey key) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/key/start", accessToken, timeout, getJobKeyFormData(key), String.class);
    }

    @Override
    public ReturnT<String> addAndStartJob(XxlJobInfo jobInfo) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/addAndStart", accessToken, timeout, getJobInfoFormData(jobInfo), String.class);
    }

    @Override
    public ReturnT<String> addAndStartJobByKey(XxlJobInfoParam jobInfo) {
        return XxlJobRemotingUtil.postForm(addressUrl+"jobinfo/key/addAndStart", accessToken, timeout, getJobInfoParamFormData(jobInfo), String.class);
    }

    private String getIdFormData(int id){
        return ("id="+id).replaceAll("null", "");
    }

    private String getJobInfoFormData(XxlJobInfo jobInfo){
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(jobInfo.getId());
        sb.append("&jobGroup=").append(jobInfo.getJobGroup());
        sb.append("&jobDesc=").append(jobInfo.getJobDesc());
        //sb.append("&addTime=").append(jobInfo.getAddTime());
        //sb.append("&updateTime=").append(jobInfo.getUpdateTime());
        sb.append("&author=").append(jobInfo.getAuthor());
        sb.append("&alarmEmail=").append(jobInfo.getAlarmEmail());
        sb.append("&scheduleType=").append(jobInfo.getScheduleType());
        sb.append("&scheduleConf=").append(jobInfo.getScheduleConf());
        sb.append("&misfireStrategy=").append(jobInfo.getMisfireStrategy());
        sb.append("&executorRouteStrategy=").append(jobInfo.getExecutorRouteStrategy());
        sb.append("&executorHandler=").append(jobInfo.getExecutorHandler());
        sb.append("&executorParam=").append(jobInfo.getExecutorParam());
        sb.append("&executorBlockStrategy=").append(jobInfo.getExecutorBlockStrategy());
        sb.append("&executorTimeout=").append(jobInfo.getExecutorTimeout());
        sb.append("&executorFailRetryCount=").append(jobInfo.getExecutorFailRetryCount());
        sb.append("&glueType=").append(jobInfo.getGlueType());
        sb.append("&glueSource=").append(jobInfo.getGlueSource());
        sb.append("&glueRemark=").append(jobInfo.getGlueRemark());
        //sb.append("&glueUpdatetime=").append(jobInfo.getGlueUpdatetime());
        sb.append("&childJobId=").append(jobInfo.getChildJobId());
        sb.append("&triggerStatus=").append(jobInfo.getTriggerStatus());
        sb.append("&triggerLastTime=").append(jobInfo.getTriggerLastTime());
        sb.append("&triggerNextTime=").append(jobInfo.getTriggerNextTime());
        return sb.toString().replaceAll("null", "");
    }

    private String getJobInfoParamFormData(XxlJobInfoParam jobInfo){
        StringBuilder sb = new StringBuilder(getJobInfoFormData(jobInfo));
        sb.append("&jobGroupName=");
        sb.append(jobInfo.getJobGroupName());
        return sb.toString().replaceAll("null", "");
    }

    private String getJobKeyFormData(XxlJobKey key){
        StringBuilder sb = new StringBuilder();
        sb.append("jobGroup=").append(key.getJobGroup());
        sb.append("&executorHandler=").append(key.getExecutorHandler());
        sb.append("&executorParam=").append(key.getExecutorParam());
        return sb.toString().replaceAll("null", "");
    }

}
