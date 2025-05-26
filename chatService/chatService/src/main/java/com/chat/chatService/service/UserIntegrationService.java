package com.chat.chatService.service;

import com.chat.chatService.dto.PortailUserDTO;
import com.chat.chatService.dto.TeamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserIntegrationService {

    private final UserServiceClient userClient;
    private final TeamServiceClient teamClient;

    public UserIntegrationService(UserServiceClient userClient, TeamServiceClient teamClient) {
        this.userClient = userClient;
        this.teamClient = teamClient;
    }

    public PortailUserDTO getUserById(Long id) {
        return userClient.getUserById(id);
    }

    public TeamDTO getTeamsByChef(Long chefId) {
        return teamClient.getTeamsByChef(chefId);
    }
}
