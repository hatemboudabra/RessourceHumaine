package com.chat.chatService.repo;

import com.chat.chatService.entity.Chat;
import com.chat.chatService.entity.ChatMessage;
import com.chat.chatService.entity.ChatType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends CrudRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomId(Long chatRoomId);

    List<ChatMessage> findByChatRoomIdAndType(Long chatRoomId, ChatType type);

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :senderId AND m.recipientId = :recipientId) OR " +
            "(m.senderId = :recipientId AND m.recipientId = :senderId)")
    List<ChatMessage> findPrivateMessagesBetweenUsers(
            @Param("senderId") Long senderId,
            @Param("recipientId") Long recipientId);
}
