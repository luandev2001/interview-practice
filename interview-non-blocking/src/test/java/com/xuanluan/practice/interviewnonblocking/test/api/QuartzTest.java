package com.xuanluan.practice.interviewnonblocking.test.api;

import com.xuanluan.practice.interviewnonblocking.service.imp.job.OutboxPublisherJob;
import com.xuanluan.practice.interviewnonblocking.test.InterviewNonBlockingApplicationTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuartzTest extends InterviewNonBlockingApplicationTests {
    @Autowired
    private Scheduler scheduler;

    @Test
    void shouldAddJobAndCheckDataUpdate() throws Exception {
        String jobName = "test-job-1";
        JobDataMap dataMap = new JobDataMap(Map.of("id", UUID.randomUUID().toString(), "value", "test"));

        JobDetail jobDetail = JobBuilder.newJob(OutboxPublisherJob.class)
                .withIdentity(jobName, "test")
                .usingJobData(dataMap)
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .startNow()
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

        // đợi job chạy (ví dụ job chèn vào DB)
        Thread.sleep(1000);

        // assert: kiểm tra số lượng job
        int count = scheduler.getJobKeys(GroupMatcher.anyJobGroup()).size();
        assertEquals(1, count);
    }
}
