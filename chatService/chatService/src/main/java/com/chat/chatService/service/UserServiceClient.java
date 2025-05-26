package com.chat.chatService.service;

import com.chat.chatService.dto.PortailUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "portailrh-service")
public interface UserServiceClient {
    @GetMapping("{id}")
    PortailUserDTO getUserById(@PathVariable("id") Long id);
}
