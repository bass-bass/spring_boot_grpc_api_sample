package com.example.grpc.api.my.dto;

import com.example.grpc.api.common.data.MyData.ProcessedData;
import com.google.gson.Gson;

import lombok.Data;

@Data
public class MyResponseDTO {
  private ProcessedData data;

  public String toJson() {
    return new Gson().toJson(this);
  }
}
