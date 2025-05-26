package com.chat.chatService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat")
public class Chat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatType type;
    @Column(name = "team_id")
    private Long teamId;

    @ElementCollection
    @CollectionTable(name = "chatroom_participants", joinColumns = @JoinColumn(name = "chatroom_id"))
    @Column(name = "user_id")
    private Set<Long> participantIds = new HashSet<>();
    @OneToMany(mappedBy = "chatRoomId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();
}
