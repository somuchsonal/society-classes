package com.frontrow.samaajseva.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Value("${generic.asyncExecutor.corePoolSize:20}")
    private int genericCorePoolSize;
    @Value("${generic.asyncExecutor.maxPoolSize:30}")
    private int genericMaxPoolSize;
    @Value("${generic.asyncExecutor.queueSize:20}")
    private int genericQueuePoolSize;
    @Value("${generic.asyncExecutor.threadPriority:5}")
    private int genericThreadPriority;

    @Value("${scheduler.asyncExecutor.corePoolSize:20}")
    private int schedulerCorePoolSize;
    @Value("${scheduler.asyncExecutor.maxPoolSize:20}")
    private int schedulerMaxPoolSize;

    @Bean(name = "updateMembershipStartDateExecutor")
    public ThreadPoolTaskExecutor updateMembershipStartDateExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "updateSessionInstructorExecutor")
    public ThreadPoolTaskExecutor updateSessionInstructorExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "populateBatchSessionExecutor")
    public ThreadPoolTaskExecutor populateBatchSessionExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "updateSessionScheduleExecutor")
    public ThreadPoolTaskExecutor updateSessionScheduleExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "deleteSessionFromScheduleExecutor")
    public ThreadPoolTaskExecutor deleteSessionFromScheduleExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "deleteBatchScheduleExecutor")
    public ThreadPoolTaskExecutor deleteBatchScheduleExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "extendMembershipExecutor")
    public ThreadPoolTaskExecutor extendMembershipExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "userOnboardedNotificationExecutor")
    public ThreadPoolTaskExecutor userOnboardedNotificationExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "classCancellationNotificationExecutor")
    public ThreadPoolTaskExecutor classCancellationNotificationExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "batchKidEnrollmentNotificationExecutor")
    public ThreadPoolTaskExecutor batchKidEnrollmentNotificationExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "instructorDelayNotificationExecutor")
    public ThreadPoolTaskExecutor instructorDelayNotificationExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "createVideoKidMappingExecutor")
    public ThreadPoolTaskExecutor createVideoKidMappingExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "markReportCompletionExecutor")
    public ThreadPoolTaskExecutor markReportCompletionExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(genericCorePoolSize);
        asyncExecutor.setMaxPoolSize(genericMaxPoolSize);
        asyncExecutor.setQueueCapacity(genericQueuePoolSize);
        asyncExecutor.setThreadPriority(genericThreadPriority);
        asyncExecutor.initialize();
        return asyncExecutor;
    }

    @Bean(name = "scheduledExecutor")
    public ScheduledThreadPoolExecutor scheduledExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(schedulerCorePoolSize);
        scheduledThreadPoolExecutor.setMaximumPoolSize(schedulerMaxPoolSize);
        scheduledThreadPoolExecutor.setKeepAliveTime(60L, TimeUnit.SECONDS);
        return scheduledThreadPoolExecutor;
    }

}
