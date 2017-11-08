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

import org.apache.commons.cli.Option;
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
    private static final long backoffSleepIncrement = 500;
    public static long minBackoffSleep = 500;
    public static long maxBackoffSleep = 5000;
    public static long consecutiveBackoffCounter = 0;

    private CounterGroup counterGroup;
    private PollingRunner runner;
    private Thread runnerThread;
    private LifecycleState lifecycleState;
    private SinkProcessor policy;
    public static LifecycleSupervisor lifecycleSupervisor;

    public SinkRunner() {
        counterGroup = new CounterGroup();
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

    @Override
    public void start() {
        SinkProcessor policy = getPolicy();

        policy.start();

        runner = new PollingRunner();
        runner.setSinkRunner(this);

        runner.policy = policy;
        runner.counterGroup = counterGroup;
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
        return "SinkRunner: { policy:" + getPolicy() + " counterGroup:"
                + counterGroup + " }";
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
        private CounterGroup counterGroup;
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
                    } else {
                        counterGroup.set("runner.backoffs.consecutive", 0L);
                    }
                } catch (InterruptedException e) {
                    logger.debug("Interrupted while processing an event. Exiting.");
                    counterGroup.incrementAndGet("runner.interruptions");
                } catch (Exception e) {
                    logger.error("Unable to deliver event. Exception follows.", e);
                    if (e instanceof EventDeliveryException) {
                        counterGroup.incrementAndGet("runner.deliveryErrors");
                    } else {
                        counterGroup.incrementAndGet("runner.errors");
                    }
                    try {
                        Thread.sleep(maxBackoffSleep);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            logger.debug("Polling runner exiting. Metrics:{}", counterGroup);
        }

        private void backoff() throws InterruptedException {
            //todo: metric?
            final long sleepTime = Math.min(minBackoffSleep + (consecutiveBackoffCounter * backoffSleepIncrement), maxBackoffSleep);
            logger.debug("Sink Runner is backing of for {} milliseconds", sleepTime);
            Thread.sleep(sleepTime);
            consecutiveBackoffCounter++;
        }

        private void shutdownFlume() {
            final Option agentName = LifecycleSupervisor.options.getOption("name");
            logger.info("Flume agent {} is shutting down...", agentName);
            sinkRunner.lifecycleState = LifecycleState.STOP;
            shouldStop.set(true);
            SinkRunner.lifecycleSupervisor.stop();
            new Thread("App-exit") {
                @Override
                public void run() {
                    System.exit(0);
                }
            }.start();
        }

        public void setSinkRunner(SinkRunner sinkRunner) {
            this.sinkRunner = sinkRunner;
        }

    }
}

