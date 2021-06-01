package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.thread.JobCompleteHelper;
import com.xxl.job.admin.core.thread.JobRegistryHelper;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {


    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        return JobCompleteHelper.getInstance().callback(callbackParamList);
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        return JobRegistryHelper.getInstance().registry(registryParam);
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return JobRegistryHelper.getInstance().registryRemove(registryParam);
    }

    //---------------not used-----------------
    @Override
    public ReturnT<String> addJob(XxlJobInfo jobInfo) {
        return null;
    }

    @Override
    public ReturnT<String> addJobByKey(XxlJobInfoParam jobInfo) {
        return null;
    }

    @Override
    public ReturnT<String> updateJob(XxlJobInfo jobInfo) {
        return null;
    }

    @Override
    public ReturnT<String> updateJobByKey(XxlJobInfoParam jobInfo) {
        return null;
    }

    @Override
    public ReturnT<String> removeJob(int id) {
        return null;
    }

    @Override
    public ReturnT<String> removeJobByKey(XxlJobKey key) {
        return null;
    }

    @Override
    public ReturnT<String> pauseJob(int id) {
        return null;
    }

    @Override
    public ReturnT<String> pauseJobByKey(XxlJobKey key) {
        return null;
    }

    @Override
    public ReturnT<String> startJob(int id) {
        return null;
    }

    @Override
    public ReturnT<String> startJobByKey(XxlJobKey key) {
        return null;
    }

    @Override
    public ReturnT<String> addAndStartJob(XxlJobInfo jobInfo) {
        return null;
    }

    @Override
    public ReturnT<String> addAndStartJobByKey(XxlJobInfoParam jobInfo) {
        return null;
    }

}
