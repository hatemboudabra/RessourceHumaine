package com.chat.chatService.service;

import com.chat.chatService.dto.ChatMessageDTO;
import com.chat.chatService.entity.ChatMessage;
import com.chat.chatService.entity.ChatType;
import com.chat.chatService.file.FileFilter;
import com.chat.chatService.repo.ChatMessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepo chatMessageRepo;


    public ChatMessage saveMessage(ChatMessageDTO messageDTO) {
        if (messageDTO.getSender() == null) {
            throw new IllegalArgumentException("Sender cannot be null");
        }

        ChatMessage message = new ChatMessage();
        message.setContent(messageDTO.getContent());
        message.setSenderId(Long.parseLong(messageDTO.getSender()));
        message.setTimestamp(messageDTO.getTimestamp() != null ? messageDTO.getTimestamp() : LocalDateTime.now());
        message.setType(messageDTO.getType());
        message.setFile(messageDTO.getFile());
        message.setRead(false);

        if (messageDTO.getType() == ChatType.PRIVATE) {
            if (messageDTO.getRecipientId() == null) {
                throw new IllegalArgumentException("RecipientId is required for private messages");
            }
            message.setRecipientId(Long.parseLong(messageDTO.getRecipientId()));
            message.setChatRoomId(null);
        } else {
            if (messageDTO.getChatRoomId() == null) {
                throw new IllegalArgumentException("ChatRoomId is required for team messages");
            }
            message.setChatRoomId(Long.parseLong(messageDTO.getChatRoomId()));
            message.setRecipientId(null);
        }

        return chatMessageRepo.save(message);
    }
    public List<ChatMessageDTO> getMessagesByChatRoomId(Long chatRoomId) {
        return chatMessageRepo.findByChatRoomId(chatRoomId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ChatMessageDTO convertToDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setContent(message.getContent());
        dto.setSender(message.getSenderId().toString());
        dto.setTimestamp(message.getTimestamp());
        dto.setType(message.getType());
       // dto.setFile(message.getFile());
        if (message.getChatRoomId() != null) {
            dto.setChatRoomId(message.getChatRoomId().toString());
        }

        if (message.getRecipientId() != null) {
            dto.setRecipientId(message.getRecipientId().toString());
        }

        return dto;
    }

    public List<ChatMessageDTO> getPrivateMessages(Long userId, Long recipientId) {
        return chatMessageRepo.findPrivateMessagesBetweenUsers(userId, recipientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ChatMessageDTO> getTeamMessages(Long teamId) {
        return chatMessageRepo.findByChatRoomIdAndType(teamId, ChatType.TEAM).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}