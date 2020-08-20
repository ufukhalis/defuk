package io.github.ufukhalis.defuk.config;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;

public class JobConfig<T> {

    private final Long initialDelay;
    private final Long period;
    private final TimeUnit timeUnit;
    private final Integer threadPoolSize;
    private final Supplier<T> operation;
    private final Level logLevel;

    public JobConfig(Long initialDelay, Long period, TimeUnit timeUnit, Integer threadPoolSize, Supplier<T> operation, Level logLevel) {
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
        this.threadPoolSize = threadPoolSize;
        this.operation = operation;
        this.logLevel = logLevel;
    }

    public static class Builder<A> {
        private Long initialDelay;
        private Long period;
        private TimeUnit timeUnit;
        private Supplier<A> operation;
        private Integer threadPoolSize = 1;
        private Level logLevel = Level.CONFIG;

        public Builder<A> withLogLevel(Level logLevel) {
            Objects.requireNonNull(logLevel, "Log level cannot be null!");
            this.logLevel = logLevel;
            return this;
        }

        public Builder<A> withOperation(Supplier<A> operation) {
            Objects.requireNonNull(operation, "Operation delay cannot be null!");
            this.operation = operation;
            return this;
        }

        public Builder<A> withInitialDelay(Long initialDelay) {
            Objects.requireNonNull(initialDelay, "Initial delay cannot be null!");
            this.initialDelay = initialDelay;
            return this;
        }

        public Builder<A> withPeriod(Long period) {
            Objects.requireNonNull(period, "Period cannot be null!");
            this.period = period;
            return this;
        }

        public Builder<A> withTimeUnit(TimeUnit timeUnit) {
            Objects.requireNonNull(timeUnit, "Time Unit cannot be null!");
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder<A> withThreadPoolSize(Integer threadPoolSize) {
            Objects.requireNonNull(threadPoolSize, "Thread pool size cannot be null!");
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public JobConfig<A> build() {
            return new JobConfig<>(this.initialDelay, this.period, this.timeUnit, threadPoolSize, operation, logLevel);
        }
    }

    public Long getInitialDelay() {
        return initialDelay;
    }

    public Long getPeriod() {
        return period;
    }

    public Supplier<T> getOperation() {
        return operation;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public Level getLogLevel() {
        return logLevel;
    }
}
