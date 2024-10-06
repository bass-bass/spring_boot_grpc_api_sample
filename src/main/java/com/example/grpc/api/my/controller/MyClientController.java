package com.example.grpc.api.my.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.grpc.api.my.service.MyClientService;

@RestController
public class MyClientController {
	@Autowired
	private MyClientService myClientService;

	@GetMapping("/grpc/api/{dataId}")
	public String test(@PathVariable String dataId) {
		System.out.println("MyClientController.test() start");
		return myClientService.test(dataId);
	}
}
