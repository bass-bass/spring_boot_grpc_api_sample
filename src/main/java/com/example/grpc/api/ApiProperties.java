package com.example.grpc.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "properties.other")
public class ApiProperties {

  private String dataLoadSpan;
  private int alertUsedMemoryRate;
  private String dataDir;
}
