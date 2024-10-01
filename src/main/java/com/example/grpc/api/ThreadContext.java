package com.example.grpc.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.grpc.api.common.logger.Logger;
import com.example.grpc.api.common.thread.DataLoadThread;

import lombok.Getter;

@Component
public class ThreadContext {
  private static final Logger logger = Logger.getLogger(ThreadContext.class.getSimpleName());
  @Getter @Autowired public DataLoadThread dataLoadThread;

  private static volatile boolean ready = false;

  public static boolean isReady() {
    return ready;
  }

  public void start() {
    logger.info("init.start");
    dataLoadThread.start();
    logger.info("init.end");

    while (!ready()) {}
    ready = true;
  }

  public boolean ready() {
    return dataLoadThread.isDataLoaded();
  }
}
