package com.chat.chatService.controller;

import com.chat.chatService.dto.ChatMessageDTO;
import com.chat.chatService.entity.ChatMessage;
import com.chat.chatService.entity.ChatType;
import com.chat.chatService.file.FileFilter;
import com.chat.chatService.service.ChatService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final FileFilter fileFilter;


    @MessageMapping("/chat/team/{teamId}")
    @SendTo("/topic/team/{teamId}")
    public ChatMessageDTO sendTeamMessage(@DestinationVariable Long teamId, ChatMessageDTO message) {
        log.info("Team message received for team {}: {}", teamId, message);
        message.setType(ChatType.TEAM);
        message.setTimestamp(LocalDateTime.now());
        message.setChatRoomId(teamId.toString());
        message.setRecipientId(null);
        chatService.saveMessage(message);
        return message;
    }

    @MessageMapping("/chat/private/{recipientId}")
    public void sendPrivateMessage(
            @DestinationVariable Long recipientId,
            ChatMessageDTO message) {

        if (recipientId == null || message.getSender() == null) {
            log.error("Invalid private message - recipient: {}, sender: {}", recipientId, message.getSender());
            return;
        }

        log.info("Private message from {} to {}: {}",
                message.getSender(), recipientId, message.getContent());

        message.setType(ChatType.PRIVATE);
        message.setTimestamp(LocalDateTime.now());
        message.setRecipientId(recipientId.toString());
        message.setChatRoomId(null);

        try {
            ChatMessage savedMessage = chatService.saveMessage(message);

            messagingTemplate.convertAndSendToUser(
                    recipientId.toString(),
                    "/queue/private",
                    message
            );

            messagingTemplate.convertAndSendToUser(
                    message.getSender(),
                    "/queue/private",
                    message
            );
        } catch (IllegalArgumentException e) {
            log.error("Failed to save private message: {}", e.getMessage());
        }
    }

    @GetMapping("/private/{userId}/{recipientId}")
    public ResponseEntity<List<ChatMessageDTO>> getPrivateMessages(
            @PathVariable Long userId,
            @PathVariable Long recipientId) {
        return ResponseEntity.ok(chatService.getPrivateMessages(userId, recipientId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ChatMessageDTO>> getTeamMessages(
            @PathVariable Long teamId) {
        return ResponseEntity.ok(chatService.getTeamMessages(teamId));
    }
    @PostMapping("/upload/team/{teamId}")
    public ResponseEntity<ChatMessageDTO> uploadTeamFile(
            @PathVariable Long teamId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender) {
        try {
            String fileName = fileFilter.storeFile(file);
            ChatMessageDTO message = new ChatMessageDTO();
            message.setSender(sender);
            message.setFile(fileName);
            message.setContent("File: " + file.getOriginalFilename()); // Add file name as content
            message.setType(ChatType.TEAM);
            message.setTimestamp(LocalDateTime.now());
            message.setChatRoomId(teamId.toString());
            message.setRecipientId(null);
            chatService.saveMessage(message);
            messagingTemplate.convertAndSend("/topic/team/" + teamId, message);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload/private/{recipientId}")
    public ResponseEntity<ChatMessageDTO> uploadPrivateFile(
            @PathVariable Long recipientId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender) {
        try {
            String fileName = fileFilter.storeFile(file);
            ChatMessageDTO message = new ChatMessageDTO();
            message.setSender(sender);
            message.setFile(fileName);
            message.setContent("File: " + file.getOriginalFilename()); // Add file name as content
            message.setType(ChatType.PRIVATE);
            message.setTimestamp(LocalDateTime.now());
            message.setRecipientId(recipientId.toString());
            message.setChatRoomId(null);

            chatService.saveMessage(message);
            messagingTemplate.convertAndSendToUser(
                    recipientId.toString(),
                    "/queue/private",
                    message
            );
            messagingTemplate.convertAndSendToUser(
                    sender,
                    "/queue/private",
                    message
            );
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            log.info("Raw requested filename: {}", fileName);
            log.info("Decoded filename: {}", decodedFileName);

            Path uploadDir = Paths.get("C:/Users/hatem/Desktop/Portail_RH/chatService/uploads/cvs")
                    .toAbsolutePath()
                    .normalize();

            log.info("Upload directory: {}", uploadDir);
            Path filePath = uploadDir.resolve(decodedFileName).normalize();
            if (!Files.exists(filePath)) {
                log.info("Exact file not found, searching for timestamped version");
                try (Stream<Path> paths = Files.list(uploadDir)) {
                    Optional<Path> timestampedFile = paths
                            .filter(path -> {
                                String filename = path.getFileName().toString();
                                if (filename.contains("_")) {
                                    String nameWithoutTimestamp = filename.substring(filename.indexOf("_") + 1);
                                    return nameWithoutTimestamp.equalsIgnoreCase(decodedFileName);
                                }
                                return false;
                            })
                            .findFirst();

                    if (timestampedFile.isPresent()) {
                        filePath = timestampedFile.get();
                        log.info("Found timestamped file: {}", filePath.getFileName());
                    } else {
                        log.error("No matching file found for: {}", decodedFileName);
                        return ResponseEntity.notFound().build();
                    }
                }
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + decodedFileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);

        } catch (Exception e) {
            log.error("Download error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}