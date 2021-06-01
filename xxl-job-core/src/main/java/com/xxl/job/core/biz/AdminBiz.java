package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.*;

import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {


    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryParam
     * @return
     */
    public ReturnT<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    public ReturnT<String> registryRemove(RegistryParam registryParam);


    // ---------------------- biz (custome) ----------------------
    // group、job ... manage

    public ReturnT<String> addJob(XxlJobInfo jobInfo);

    public ReturnT<String> addJobByKey(XxlJobInfoParam jobInfo);

    public ReturnT<String> updateJob(XxlJobInfo jobInfo);

    public ReturnT<String> updateJobByKey(XxlJobInfoParam jobInfo);

    public ReturnT<String> removeJob(int id);

    public ReturnT<String> removeJobByKey(XxlJobKey key);

    public ReturnT<String> pauseJob(int id);

    public ReturnT<String> pauseJobByKey(XxlJobKey key);

    public ReturnT<String> startJob(int id);

    public ReturnT<String> startJobByKey(XxlJobKey key);

    public ReturnT<String> addAndStartJob(XxlJobInfo jobInfo);

    public ReturnT<String> addAndStartJobByKey(XxlJobInfoParam jobInfo);

}
