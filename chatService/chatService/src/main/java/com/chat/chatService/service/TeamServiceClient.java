package com.chat.chatService.service;
import com.chat.chatService.dto.TeamDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "portailrh-service")
public interface TeamServiceClient {
    @GetMapping("/by-chef")
    TeamDTO getTeamsByChef(@PathVariable("userId") Long chefId);
}
