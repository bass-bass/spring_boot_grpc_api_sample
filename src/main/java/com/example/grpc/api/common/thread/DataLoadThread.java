package com.example.grpc.api.common.thread;

import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.grpc.api.ApiProperties;
import com.example.grpc.api.common.data.MyData;
import com.example.grpc.api.common.logger.Logger;
import com.example.grpc.api.common.util.MemoryInfo;
import com.example.grpc.api.common.util.TimeInfo;

import lombok.Getter;

@Component
public class DataLoadThread extends ScheduleThread {

  private static final String name = DataLoadThread.class.getSimpleName();
  private static final Logger logger = Logger.getLogger(name);

  @Autowired private ApiProperties properties;

  @Autowired private MyData myData;

  @Getter private volatile boolean dataLoaded = false;
  private volatile boolean memoryAlert = false;

  public void start() {
    if (properties == null) {
      return;
    }
    System.out.println("prop -> " + properties);
    // data load thread
    TimeInfo info = new TimeInfo(properties.getDataLoadSpan());

    int processTime = info.getVal();
    TimeUnit unit = info.getUnit();

    logger.info("*** DataLoadThread start ***");
    logger.info("processTime -> " + processTime + " timeunit -> " + unit, true);

    this.scheduleWithDelay(name, 1, processTime, unit);
  }

  /**
   * ScheduleThread.scheduleWithDelay() -> run() -> run0()
   *
   * @throws Exception
   */
  @Override
  public void run0() throws Exception {
    logger.info("load start");
    loadEach();
    dataLoaded = true;
    logger.info("load end");
  }

  private void loadEach() throws Exception {
    // ここでデータ読み込み
    myData.load();
    MemoryInfo mem = new MemoryInfo();
    int alertRate = properties.getAlertUsedMemoryRate();
    if (alertRate < mem.getUsedRate()) {
      if (!memoryAlert) {
        logger.warn("used memory over " + alertRate + "%\n" + mem.toString());
        memoryAlert = true;
      }
    }
  }

  @PreDestroy
  public void destroy() {
    try {
      super.destroy();
    } catch (Exception e) {
      logger.error(e);
    }
  }
}
