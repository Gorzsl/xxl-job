package com.xxl.job.core.biz.model;

/**
 * @author: YangWeiDong
 * @date: 2021/05/30 13:45
 */
public class XxlJobKey {
    private String jobGroup;
    private String executorHandler;
    private String executorParam;

    public XxlJobKey() {
    }

    public XxlJobKey(String jobGroup, String executorHandler, String executorParam) {
        this.jobGroup = jobGroup;
        this.executorHandler = executorHandler;
        this.executorParam = executorParam==null?"":executorParam;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam==null?"":executorParam;
    }
}
