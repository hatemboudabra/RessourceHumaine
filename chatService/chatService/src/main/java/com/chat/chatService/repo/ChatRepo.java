package com.chat.chatService.repo;

import com.chat.chatService.entity.Chat;
import com.chat.chatService.entity.ChatType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepo extends CrudRepository<Chat, Long> {
    List<Chat> findByTypeAndParticipantIdsContaining(ChatType type, Long userId);

    List<Chat> findByTypeAndTeamId(ChatType type, Long teamId);
}
