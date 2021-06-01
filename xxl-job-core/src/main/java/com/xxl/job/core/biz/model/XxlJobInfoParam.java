package com.xxl.job.core.biz.model;

/**
 * @author: YangWeiDong
 * @date: 2021/05/30 11:16
 */
public class XxlJobInfoParam extends XxlJobInfo {

    private String jobGroupName;		// 执行器名称

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }
}
