/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.flume;

import org.apache.flume.lifecycle.LifecycleAware;
import org.apache.flume.lifecycle.LifecycleState;
import org.apache.flume.lifecycle.LifecycleSupervisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * A driver for {@linkplain Sink sinks} that polls them, attempting to
 * {@linkplain Sink#process() process} events if any are available in the
 * {@link Channel}.
 * </p>
 * <p>
 * <p>
 * Note that, unlike {@linkplain Source sources}, all sinks are polled.
 * </p>
 *
 * @see org.apache.flume.Sink
 * @see org.apache.flume.SourceRunner
 */
public class SinkRunner implements LifecycleAware {

    private static final Logger logger = LoggerFactory
            .getLogger(SinkRunner.class);

    public static final long DEFAULT_BACKOFF_SLEEP_INCREMENT = 100L;
    public static final long DEFAULT_MIN_BACKOFF_SLEEP = 500L;
    public static final long DEFAULT_MAX_BACKOFF_SLEEP = 5000L;

    public static long backoffSleepIncrement = DEFAULT_BACKOFF_SLEEP_INCREMENT;
    public static long minBackoffSleep = DEFAULT_MIN_BACKOFF_SLEEP;
    public static long maxBackoffSleep = DEFAULT_MAX_BACKOFF_SLEEP;
    public static long consecutiveBackoffCounter = 0L;

    private PollingRunner runner;
    private Thread runnerThread;
    private LifecycleState lifecycleState;
    private SinkProcessor policy;
    public static LifecycleSupervisor lifecycleSupervisor;

    public SinkRunner() {
        lifecycleState = LifecycleState.IDLE;
    }

    public SinkRunner(SinkProcessor policy) {
        this();
        setSink(policy);
    }

    public SinkProcessor getPolicy() {
        return policy;
    }

    public void setSink(SinkProcessor policy) {
        this.policy = policy;
    }

    public static void setBackoffSleepIncrement(long backoffSleepIncrement) {
        SinkRunner.backoffSleepIncrement = backoffSleepIncrement;
    }

    public static void setMinBackoffSleep(long minBackoffSleep) {
        SinkRunner.minBackoffSleep = minBackoffSleep;
    }

    public static void setMaxBackoffSleep(long maxBackoffSleep) {
        SinkRunner.maxBackoffSleep = maxBackoffSleep;
    }

    @Override
    public void start() {
        SinkProcessor policy = getPolicy();

        policy.start();

        runner = new PollingRunner();
        runner.setSinkRunner(this);

        runner.policy = policy;
        runner.shouldStop = new AtomicBoolean();

        runnerThread = new Thread(runner);
        runnerThread.setName("SinkRunner-PollingRunner-" +
                policy.getClass().getSimpleName());
        runnerThread.start();

        lifecycleState = LifecycleState.START;
    }

    @Override
    public void stop() {
        if (runnerThread != null) {
            runner.shouldStop.set(true);
            runnerThread.interrupt();

            while (runnerThread.isAlive()) {
                try {
                    logger.debug("Waiting for runner thread to exit");
                    runnerThread.join(500);
                } catch (InterruptedException e) {
                    logger.debug("Interrupted while waiting for runner thread to exit. Exception follows.",
                            e);
                }
            }
        }


        lifecycleState = LifecycleState.STOP;
    }

    @Override
    public String toString() {
        return "SinkRunner: { policy:" + getPolicy() + "}";
    }

    @Override
    public LifecycleState getLifecycleState() {
        return lifecycleState;
    }

    /**
     * {@link Runnable} that {@linkplain SinkProcessor#process() polls} a
     * {@link SinkProcessor} and manages event delivery notification,
     * {@link Sink.Status BACKOFF} delay handling, etc.
     */
    public static class PollingRunner implements Runnable {

        private SinkProcessor policy;
        private AtomicBoolean shouldStop;
        private SinkRunner sinkRunner;


        @Override
        public void run() {
            logger.debug("Polling sink runner starting");

            while (!shouldStop.get()) {
                try {
                    final Sink.Status status = policy.process();
                    if (status.equals(Sink.Status.BACKOFF)) {
                        backoff();
                    } else if (status.equals(Sink.Status.DONE)) {
                        shutdownFlume();
                    }
                } catch (InterruptedException e) {
                    logger.debug("Interrupted while processing an event. Exiting.");
                } catch (Exception e) {
                    logger.error("Unable to deliver event. Exception follows.", e);
                    try {
                        Thread.sleep(maxBackoffSleep);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            logger.debug("Polling runner exiting.");
        }

        private void backoff() throws InterruptedException {
            //todo: metric?
            final long sleepTime = Math.min(minBackoffSleep + (consecutiveBackoffCounter * backoffSleepIncrement), maxBackoffSleep);
            logger.debug("Sink Runner is backing of for {} milliseconds", sleepTime);
            Thread.sleep(sleepTime);
            consecutiveBackoffCounter++;
        }

        private void shutdownFlume() {
            logger.info("Flume agent {} is shutting down...", LifecycleSupervisor.agentName);
            sinkRunner.lifecycleState = LifecycleState.STOP;
            shouldStop.set(true);
            SinkRunner.lifecycleSupervisor.stop();
            if(LifecycleState.ERROR.equals(SinkRunner.lifecycleSupervisor.getLifecycleState())){
                logger.info("going to exit. current state is ERROR");
                new Thread("App-exit") {
                    @Override
                    public void run() {
                        System.exit(1);
                    }
                }.start();
            } else {
                logger.info("going to exit. current state is not ERROR.");
                new Thread("App-exit") {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }.start();
            }

        }

        public void setSinkRunner(SinkRunner sinkRunner) {
            this.sinkRunner = sinkRunner;
        }

    }
}

