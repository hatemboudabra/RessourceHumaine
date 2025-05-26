package com.chat.chatService.dto;

import com.chat.chatService.entity.ChatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String sender;
    private String content;
    private String chatRoomId;
    private ChatType type;
    private String recipientId;
    //type team ou entre 2 users
    // service portailRh
    private String senderUsername;
    private String senderTeamName;
    private LocalDateTime timestamp;

    private String file;


}
