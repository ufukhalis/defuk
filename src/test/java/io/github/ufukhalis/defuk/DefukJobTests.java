package io.github.ufukhalis.defuk;

import io.github.ufukhalis.defuk.config.JobConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class DefukJobTests {

    private final JobConfig<Integer> jobConfig = new JobConfig.Builder<Integer>()
            .withInitialDelay(0L)
            .withPeriod(1L)
            .withThreadPoolSize(1)
            .withOperation(() -> 1)
            .withTimeUnit(TimeUnit.SECONDS)
            .build();

    @Test
    void whenJobStarted_thenDataShouldBeEmpty_Before1Second() {
        DefukJob<Integer> defukJob = DefukJob.periodic(jobConfig);
        defukJob.start();

        Assertions.assertFalse(defukJob.get().isPresent());
    }

    @Test
    void whenJobStarted_thenDataShouldNotBeEmpty_After1Second() throws InterruptedException {
        DefukJob<Integer> defukJob = DefukJob.periodic(jobConfig);
        defukJob.start();

        Thread.sleep(1050);

        Assertions.assertTrue(defukJob.get().isPresent());
    }

    @Test
    void whenJobNotStarted_thenDataShouldBeEmpty() {
        DefukJob<Integer> defukJob = DefukJob.periodic(jobConfig);

        Assertions.assertFalse(defukJob.get().isPresent());
    }
}
