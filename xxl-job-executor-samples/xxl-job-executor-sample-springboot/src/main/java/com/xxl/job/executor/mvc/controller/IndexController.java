package com.xxl.job.executor.mvc.controller;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.XxlJobInfo;
import com.xxl.job.core.biz.model.XxlJobInfoParam;
import com.xxl.job.core.biz.model.XxlJobKey;
import com.xxl.job.core.context.XxlJobBizHelper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class IndexController {

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<Integer> add(XxlJobInfo jobInfo) {
        return new ReturnT<>(XxlJobBizHelper.addJob(jobInfo));
    }

    @RequestMapping("/key/add")
    @ResponseBody
    public ReturnT<Integer> addKey(XxlJobInfoParam jobInfo) {
        return new ReturnT<>(XxlJobBizHelper.addJobByKey(jobInfo));
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(XxlJobInfo jobInfo) {
        XxlJobBizHelper.updateJob(jobInfo);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/key/update")
    @ResponseBody
    public ReturnT<String> updateKey(XxlJobInfoParam jobInfo) {
        XxlJobBizHelper.updateJobByKey(jobInfo);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {
        XxlJobBizHelper.removeJob(id);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/key/remove")
    @ResponseBody
    public ReturnT<String> removeKey(XxlJobKey key) {
        XxlJobBizHelper.removeJobByKey(key);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/stop")
    @ResponseBody
    public ReturnT<String> pause(int id) {
        XxlJobBizHelper.pauseJob(id);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/key/stop")
    @ResponseBody
    public ReturnT<String> pauseKey(XxlJobKey key) {
        XxlJobBizHelper.pauseJobByKey(key);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/start")
    @ResponseBody
    public ReturnT<String> start(int id) {
        XxlJobBizHelper.startJob(id);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/key/start")
    @ResponseBody
    public ReturnT<String> startKey(XxlJobKey key) {
        XxlJobBizHelper.startJobByKey(key);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/addAndStart")
    @ResponseBody
    public ReturnT<Integer> addAndStart(XxlJobInfo jobInfo) {
        return new ReturnT<>(XxlJobBizHelper.addAndStartJob(jobInfo));
    }

    @RequestMapping("/key/addAndStart")
    @ResponseBody
    public ReturnT<Integer> addAndStartKey(XxlJobInfoParam jobInfo) {
        return new ReturnT<>(XxlJobBizHelper.addAndStartJobByKey(jobInfo));
    }

}