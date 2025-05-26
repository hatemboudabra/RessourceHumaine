package com.example.PortailRH.service;

import com.example.PortailRH.entity.ChatMessage;
import com.example.PortailRH.entity.ChatRoom;
import com.example.PortailRH.entity.Team;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.ChatMessageDTO;
import com.example.PortailRH.entity.enummerations.ChatType;
import com.example.PortailRH.exception.ResourceNotFoundException;
import com.example.PortailRH.repository.ChatMessageRepo;
import com.example.PortailRH.repository.ChatRoomRepo;
import com.example.PortailRH.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ChatService {
   /* private final UserRepo userRepo;
    private final ChatRoomRepo chatRoomRepo;
    private final ChatMessageRepo chatMessageRepo;

    public ChatService(UserRepo userRepo, ChatRoomRepo chatRoomRepo, ChatMessageRepo chatMessageRepo) {
        this.userRepo = userRepo;
        this.chatRoomRepo = chatRoomRepo;
        this.chatMessageRepo = chatMessageRepo;
    }
    public ChatMessage saveMessage(String content, Long senderId, Long chatRoomId) {
        User sender = userRepo.findById(Math.toIntExact(senderId))
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        ChatRoom chatRoom = chatRoomRepo.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom not found"));

        ChatMessage message = new ChatMessage();
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setSender(sender);
        message.setChatRoom(chatRoom);
        message.setRead(false);
        message.setImage(message.getImage());
        return chatMessageRepo.save(message);
    }

    public List<ChatMessage> getRoomMessages(Long roomId) {
        return chatMessageRepo.findByChatRoomIdOrderByTimestampDesc(roomId);
    }

    public ChatRoom createOrGetPrivateChat(Long user1Id, Long user2Id) {
        Optional<ChatRoom> existingChat = chatRoomRepo.findPrivateChatBetweenUsers(user1Id, user2Id);

        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        User user1 = userRepo.findById(Math.toIntExact(user1Id))
                .orElseThrow(() -> new ResourceNotFoundException("User 1 not found"));
        User user2 = userRepo.findById(Math.toIntExact(user2Id))
                .orElseThrow(() -> new ResourceNotFoundException("User 2 not found"));

        ChatRoom newChat = new ChatRoom();
        newChat.setName("Private Chat");
        newChat.setType(ChatType.PRIVATE);
        newChat.setParticipants(new HashSet<>(Arrays.asList(user1, user2)));
        newChat.setMessages(new ArrayList<>());

        return chatRoomRepo.save(newChat);
    }

    public ChatRoom createTeamChatRoom(Team team) {
        ChatRoom teamChat = new ChatRoom();
        teamChat.setName("Team Chat - " + team.getName());
        teamChat.setType(ChatType.TEAM);
        teamChat.setTeam(team);
        teamChat.setParticipants(new HashSet<>(team.getCollaborators()));
        teamChat.setMessages(new ArrayList<>());
        return chatRoomRepo.save(teamChat);
    }

    public List<ChatMessage> getUnreadMessages(Long roomId, Long userId) {
        return chatMessageRepo.findUnreadMessages(roomId, userId);
    }

    public List<ChatRoom> getUserPrivateChats(Long userId) {
        return chatRoomRepo.findPrivateChatsForUser(userId);
    }*/
}
