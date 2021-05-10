package com.infoworld.jwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
  @GetMapping({ "/public" })
	public String publicEndpoint() {
		return "Public Endpoint Response";
	}
	@GetMapping({ "/protected" })
	public String protectedEndpoint() {
		return "Protected Endpoint Response";
	}
}
