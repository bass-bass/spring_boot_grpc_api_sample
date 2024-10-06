package com.example.grpc.api.common.grpc;

import com.example.grpc.api.common.logger.Logger;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public class LogGrpcClientInterceptor implements ClientInterceptor {
	private static final Logger logger = Logger.getLogger(LogGrpcClientInterceptor.class.getSimpleName());

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
			MethodDescriptor<ReqT, RespT> method,
			CallOptions callOptions,
			Channel next) {

		logger.info("Received call to " + method.getFullMethodName());

		return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

			@Override
			public void sendMessage(ReqT message) {
				logger.debug("Request message: " + message);
				super.sendMessage(message);
			}

			@Override
			public void start(Listener<RespT> responseListener, Metadata headers) {
				super.start(
						new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
							@Override
							public void onMessage(RespT message) {
								logger.debug("Response message: " + message);
								super.onMessage(message);
							}

							@Override
							public void onHeaders(Metadata headers) {
								logger.debug("gRPC headers: " + headers);
								super.onHeaders(headers);
							}

							@Override
							public void onClose(Status status, Metadata trailers) {
								logger.info("Interaction ends with status: " + status);
								logger.info("Trailers: " + trailers);
								super.onClose(status, trailers);
							}
						}, headers);
			}
		};
	}
}
