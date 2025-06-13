package com.xuanluan.practice.interviewnonblocking.service.imp;

import com.xuanluan.practice.interviewnonblocking.model.request.JobClockworkRequest;
import com.xuanluan.practice.interviewnonblocking.service.imp.job.OutboxPublisherJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.utils.Key;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@ConditionalOnProperty(name = "quartz.clockwork.enable", havingValue = "true")
@RequiredArgsConstructor
@Service
public class JobClockworkServiceImp {
    private final Scheduler scheduler;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        getScheduleJobs().forEach(request -> {
            try {
                if (request.isEnable()) {
                    scheduler.scheduleJob(request.jobDetail(), request.trigger());
                }
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<JobClockworkRequest> getScheduleJobs() {
        return Arrays.asList(
                new JobClockworkRequest(
                        buildJob(OutboxPublisherJob.class).build(),
                        TriggerBuilder.newTrigger()
                                .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(1))
                                .build()
                )
        );
    }

    private JobBuilder buildJob(Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(Key.createUniqueName(null), jobClass.getSimpleName())
                .storeDurably()
                .requestRecovery();
    }
}
