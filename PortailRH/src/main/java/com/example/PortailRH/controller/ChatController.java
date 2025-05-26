package com.example.PortailRH.controller;

import com.example.PortailRH.entity.ChatMessage;
import com.example.PortailRH.entity.ChatRoom;
import com.example.PortailRH.entity.enummerations.ChatType;
import com.example.PortailRH.service.ChatService;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/api")
@RestController
@CrossOrigin(origins = "*")
public class ChatController {
   /* private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(@PathVariable Long roomId) {
        if (roomId == null || roomId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(chatService.getRoomMessages(roomId));
    }

    @PostMapping("/room/private")
    public ResponseEntity<ChatRoom> createPrivateChat(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {
        return ResponseEntity.ok(chatService.createOrGetPrivateChat(user1Id, user2Id));
    }

    @GetMapping("/user/{userId}/private-chats")
    public ResponseEntity<List<ChatRoom>> getUserPrivateChats(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserPrivateChats(userId));
    }*/
}