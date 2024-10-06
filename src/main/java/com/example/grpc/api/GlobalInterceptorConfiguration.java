package com.example.grpc.api;

import org.springframework.context.annotation.Configuration;

import com.example.grpc.api.common.grpc.LogGrpcClientInterceptor;
import com.example.grpc.api.common.grpc.LogGrpcServerInterceptor;

import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@Configuration(proxyBeanMethods = false)
public class GlobalInterceptorConfiguration {

	@GrpcGlobalServerInterceptor
	LogGrpcServerInterceptor logServerInterceptor() {
		return new LogGrpcServerInterceptor();
	}

	@GrpcGlobalClientInterceptor
	LogGrpcClientInterceptor logClientInterceptor() {
		return new LogGrpcClientInterceptor();
	}

}
