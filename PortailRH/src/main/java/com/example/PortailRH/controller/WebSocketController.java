package com.example.PortailRH.controller;
import com.example.PortailRH.entity.ChatMessage;
import com.example.PortailRH.entity.enummerations.ChatType;
import com.example.PortailRH.service.ChatService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
/*
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.message")
    public void handleChatMessage(
            @Payload String content,
            @Header("senderId") Long senderId,
            @Header("chatRoomId") Long chatRoomId) {
        ChatMessage savedMessage = chatService.saveMessage(content, senderId, chatRoomId);

        if (savedMessage.getChatRoom().getType() == ChatType.TEAM) {
            messagingTemplate.convertAndSend(
                    "/topic/team." + savedMessage.getChatRoom().getId(),
                    savedMessage
            );
        } else {
            savedMessage.getChatRoom().getParticipants().forEach(participant -> {
                messagingTemplate.convertAndSendToUser(
                        participant.getUsername(),
                        "/queue/private." + savedMessage.getChatRoom().getId(),
                        savedMessage
                );
            });
        }
    }*/
}
