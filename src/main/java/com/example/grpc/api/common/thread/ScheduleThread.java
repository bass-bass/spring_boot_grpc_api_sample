package com.example.grpc.api.common.thread;

import com.example.grpc.api.common.logger.Logger;
import com.example.grpc.api.common.util.Destructor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class ScheduleThread implements Destructor.Destructable, Runnable {

  private final ScheduledExecutorService executor =
      Executors.newScheduledThreadPool(
          1,
          new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
              Thread t = new Thread(r);
              t.setDaemon(true);
              return t;
            }
          });

  protected Logger logger;
  private ScheduledFuture<?> scheduledFuture;
  protected volatile boolean running = false;

  public void scheduleWithDelay(String threadName, long processSpanMillis) {
    int sec = (int) Math.max(processSpanMillis / 1000, 1L);
    scheduleWithDelay(threadName, sec, TimeUnit.SECONDS);
  }

  public void scheduleWithDelay(String threadName, long processSpan, TimeUnit unit) {
    scheduleWithDelay(threadName, 15, processSpan, unit);
  }

  public void scheduleWithDelay(
      String threadName, long initialDelay, long processSpan, TimeUnit unit) {
    if (running) {
      return;
    }
    if (unit == null) {
      throw new NullPointerException("unit is null");
    }
    running = true;
    if (logger == null) logger = Logger.getLogger(threadName);
    scheduledFuture = executor.scheduleWithFixedDelay(this, initialDelay, processSpan, unit);
    Destructor.put(threadName, this);
  }

  public void scheduleAtRate(String threadName, long processSpanMillis) {
    int sec = (int) Math.max(processSpanMillis / 1000, 1L);
    scheduleAtRate(threadName, sec, TimeUnit.SECONDS);
  }

  public void scheduleAtRate(String threadName, long processSpan, TimeUnit unit) {
    scheduleAtRate(threadName, 15, processSpan, unit);
  }

  public void scheduleAtRate(
      String threadName, long initialDelay, long processSpan, TimeUnit unit) {
    if (running) {
      return;
    }
    running = true;
    if (logger == null) logger = Logger.getLogger(threadName);
    scheduledFuture = executor.scheduleAtFixedRate(this, initialDelay, processSpan, unit);
    Destructor.put(threadName, this);
  }

  @Override
  public void run() {
    try {
      run0();
    } catch (Throwable t) {
      logger.error(t);
    }
  }

  public abstract void run0() throws Exception;

  @Override
  public void destroy() throws Exception {
    running = false;
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.MINUTES);
  }

  public boolean isAlive() {
    if (scheduledFuture == null) return false;
    return !scheduledFuture.isCancelled();
  }

  public boolean isRunning() {
    return running;
  }
}
