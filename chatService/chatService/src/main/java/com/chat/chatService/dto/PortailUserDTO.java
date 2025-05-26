package com.chat.chatService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortailUserDTO {
    private Long id;
    private String username;
    private String email;
 //   private String post;
    private Long teamId;
}
