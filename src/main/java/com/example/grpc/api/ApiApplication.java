package com.example.grpc.api;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.example.grpc.api.common.logger.Logger;
import com.example.grpc.api.common.util.Destructor;

import lombok.Getter;

@SpringBootApplication
public class ApiApplication extends SpringBootServletInitializer {

  private static final Logger logger = Logger.getLogger(ApiApplication.class.getSimpleName());

  @Getter @Autowired private ThreadContext thread;

  @PostConstruct
  public void init() {
    try {
      logger.info("init start.");
      thread.start();
      logger.info("servlet init complete");
    } catch (Exception e) {
      logger.error(e);
    }
  }

  @PreDestroy
  public void destroy() {
    logger.info("Destructor.destroyAll start");
    try {
      Destructor.destroyAll();
    } catch (Exception e) {
      logger.error(e);
    }
    logger.info("Destructor.destroyAll end");
  }

  public static void main(String[] args) {
    SpringApplication.run(ApiApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(ApiApplication.class);
  }
}
