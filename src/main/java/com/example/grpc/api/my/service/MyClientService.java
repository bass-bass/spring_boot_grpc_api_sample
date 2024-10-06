package com.example.grpc.api.my.service;

import org.springframework.stereotype.Service;

import com.example.grpc.api.my.dto.MyRequestDTO;
import com.example.grpc.api.rpc.Rpc;
import com.example.grpc.api.rpc.RpcServiceGrpc;
import com.google.gson.Gson;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class MyClientService {

	@GrpcClient("my-grpc-server")
	private RpcServiceGrpc.RpcServiceBlockingStub stub;

	public String test(String dataId) {
		System.out.println("MyClientService.test() start");

		// request
		MyRequestDTO reqDto = new MyRequestDTO();
		reqDto.setDataId(Integer.parseInt(dataId));
		Rpc.RpcRequest request = Rpc.RpcRequest.newBuilder()
				.setControl("my")
				.setAction("execute")
				.setDataJson(new Gson().toJson(reqDto))
				.build();

		// response
		Rpc.RpcResponse response = stub.execute(request);
		System.out.println("response: success: " + response.getSuccess() + ", dataJson: " + response.getDataJson());
		return response.getDataJson();
	}
}
