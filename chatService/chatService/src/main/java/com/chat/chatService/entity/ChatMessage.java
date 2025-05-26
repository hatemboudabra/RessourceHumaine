package com.chat.chatService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  //  @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(name = "chatroom_id")
    private Long chatRoomId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    private String file;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatType type;


}
