package com.example.PortailRH.repository;

import com.example.PortailRH.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepo extends JpaRepository<ChatRoom,Long> {
 /*   @Query("SELECT cr FROM ChatRoom cr WHERE cr.type = 'PRIVATE' AND :userId IN (SELECT u.id FROM cr.participants u)")
    List<ChatRoom> findPrivateChatsForUser(@Param("userId") Long userId);

    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE cr.type = 'PRIVATE' " +
            "AND :user1Id IN (SELECT u.id FROM cr.participants u) " +
            "AND :user2Id IN (SELECT u.id FROM cr.participants u)")
    Optional<ChatRoom> findPrivateChatBetweenUsers(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );*/
}
