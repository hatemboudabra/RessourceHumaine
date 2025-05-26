package com.example.PortailRH.repository;

import com.example.PortailRH.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage,Long> {
 /*   List<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId);
    List<ChatMessage> findByChatRoomIdAndTimestampAfter(Long chatRoomId, LocalDateTime timestamp);
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId AND m.isRead = false AND m.sender.id != :userId")
    List<ChatMessage> findUnreadMessages(Long chatRoomId, Long userId);*/
}
