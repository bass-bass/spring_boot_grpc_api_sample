package com.example.grpc.api.common.grpc;

import com.example.grpc.api.common.logger.Logger;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class LogGrpcServerInterceptor implements ServerInterceptor {
	private static final Logger logger = Logger.getLogger(LogGrpcServerInterceptor.class.getSimpleName());

	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
		logger.info(serverCall.getMethodDescriptor().getFullMethodName());
		return serverCallHandler.startCall(serverCall, metadata);
	}
}
