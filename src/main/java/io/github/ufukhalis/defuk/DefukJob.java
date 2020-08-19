package io.github.ufukhalis.defuk;

import io.github.ufukhalis.defuk.config.JobConfig;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class DefukJob<D> {

    private final static String MAP_KEY = "DefukJob";
    private final ScheduledExecutorService scheduler;

    private final Map<String, D> DEFUK_MAP = new ConcurrentHashMap<>();

    private final JobConfig<D> jobConfig;

    public DefukJob(ScheduledExecutorService scheduler, JobConfig<D> jobConfig) {
        this.scheduler = scheduler;
        this.jobConfig = jobConfig;
    }

    public static <T> DefukJob<T> periodic(JobConfig<T> jobConfig) {

        return new DefukJob<>(Executors.newScheduledThreadPool(jobConfig.getThreadPoolSize()), jobConfig);
    }

    public void start() {
        Runnable runnable = () -> {
            D result = jobConfig.getOperation().get();
            DEFUK_MAP.put(MAP_KEY, result);
            System.out.println(Thread.currentThread().getName());
        };
        scheduler.scheduleAtFixedRate(runnable, jobConfig.getInitialDelay(), jobConfig.getPeriod(), jobConfig.getTimeUnit());
    }

    public Optional<D> get() {
        return Optional.ofNullable(DEFUK_MAP.get(MAP_KEY));
    }
}
