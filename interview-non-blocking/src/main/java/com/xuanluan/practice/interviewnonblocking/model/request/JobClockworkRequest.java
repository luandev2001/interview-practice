package com.xuanluan.practice.interviewnonblocking.model.request;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public record JobClockworkRequest(JobDetail jobDetail, Trigger trigger, boolean isEnable) {
    public JobClockworkRequest(JobDetail jobDetail, Trigger trigger) {
        this(jobDetail, trigger, true);
    }
}
