package com.example.grpc.api.my;

import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.grpc.api.common.data.MyData;
import com.example.grpc.api.common.logger.Logger;
import com.example.grpc.api.my.dto.MyRequestDTO;
import com.example.grpc.api.my.dto.MyResponseDTO;
import com.example.grpc.api.rpc.Rpc;
import com.example.grpc.api.rpc.RpcServiceGrpc;

import io.grpc.stub.StreamObserver;

@GRpcService
public class MyService extends RpcServiceGrpc.RpcServiceImplBase {
  private final Logger logger = Logger.getLogger(MyService.class.getSimpleName());

  @Autowired private MyData myData;

  @Override
  public void execute(Rpc.RpcRequest request, StreamObserver<Rpc.RpcResponse> responseObserver) {
    MyRequestDTO reqDto = MyRequestDTO.fromJson(request.getDataJson());
    logger.info("request json : " + request.getDataJson());

    MyResponseDTO resDto = new MyResponseDTO();
    resDto.setData(this.myData.getById(reqDto.getDataId()));

    logger.info("response json : " + resDto.toJson());
    Rpc.RpcResponse response = Rpc.RpcResponse.newBuilder().setSuccess(true).setDataJson(resDto.toJson()).build();

    // Send the reply back to the client.
    responseObserver.onNext(response);

    // Indicate that no further messages will be sent to the client.
    responseObserver.onCompleted();
  }
}
