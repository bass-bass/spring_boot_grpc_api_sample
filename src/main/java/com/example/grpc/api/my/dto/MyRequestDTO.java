package com.example.grpc.api.my.dto;

import com.google.gson.Gson;

import lombok.Data;

@Data
public class MyRequestDTO {
	private int dataId;

	public static MyRequestDTO fromJson(String json) {
		return new Gson().fromJson(json, MyRequestDTO.class);
	}
}
