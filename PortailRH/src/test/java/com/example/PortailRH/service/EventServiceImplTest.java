package com.example.PortailRH.service;

import com.example.PortailRH.entity.Event;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.EventDTO;
import com.example.PortailRH.entity.enummerations.EventsType;
import com.example.PortailRH.repository.EventRepo;
import com.example.PortailRH.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepo eventRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private EventServiceImpl eventService;

    private EventDTO eventDTO;
    private Event event;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        eventDTO = new EventDTO();
        eventDTO.setTitle("Test Event");
        eventDTO.setDescription("Test Description");
        eventDTO.setType(EventsType.VOYAGE);
        eventDTO.setStartDate(new Date());
        eventDTO.setEndDate(new Date(System.currentTimeMillis() + 7200000)); // 2 hours later
        eventDTO.setUserId(1L);

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setType(EventsType.VOYAGE);
        event.setStartDate(new Date());
        event.setEndDate(new Date(System.currentTimeMillis() + 7200000));
        event.setUser(user);
    }

    @Test
    void getAllEvent_ShouldReturnAllEvents() {
        // Arrange
        when(eventRepo.findAll()).thenReturn(List.of(event));

        // Act
        List<Event> result = eventService.getAllEvent();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event.getTitle(), result.get(0).getTitle());
        verify(eventRepo, times(1)).findAll();
    }

    @Test
    void addEvent_WithValidData_ShouldReturnSavedEvent() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(eventRepo.save(any(Event.class))).thenReturn(event);

        // Act
        Event result = eventService.addEvent(eventDTO);

        // Assert
        assertNotNull(result);
        assertEquals(eventDTO.getTitle(), result.getTitle());
        assertEquals(eventDTO.getDescription(), result.getDescription());
        assertEquals(user, result.getUser());

        verify(userRepo, times(1)).findById(1);
        verify(eventRepo, times(1)).save(any(Event.class));
    }

    @Test
    void addEvent_WithInvalidUserId_ShouldThrowException() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            eventService.addEvent(eventDTO);
        });

        verify(userRepo, times(1)).findById(1);
        verify(eventRepo, never()).save(any());
    }

    @Test
    void findByUserId_ShouldReturnUserEvents() {
        // Arrange
        when(eventRepo.findByUserId(1L)).thenReturn(List.of(event));

        // Act
        List<Event> result = eventService.findByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(event.getTitle(), result.get(0).getTitle());
        verify(eventRepo, times(1)).findByUserId(1L);
    }

    @Test
    void deleteEvent_ShouldCallDeleteById() {
        // Arrange
        doNothing().when(eventRepo).deleteById(1L);

        // Act
        eventService.deleteEvent(1L);

        // Assert
        verify(eventRepo, times(1)).deleteById(1L);
    }

    @Test
    void updateEvent_WithValidData_ShouldReturnUpdatedEvent() {
        // Arrange
        EventDTO updatedDTO = new EventDTO();
        updatedDTO.setTitle("Updated Event");
        updatedDTO.setDescription("Updated Description");
        updatedDTO.setType(EventsType.TEAM_BUILDING);
        updatedDTO.setStartDate(new Date());
        updatedDTO.setEndDate(new Date(System.currentTimeMillis() + 10800000)); // 3 hours later
        updatedDTO.setUserId(1L);

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(eventRepo.save(any(Event.class))).thenReturn(event);

        // Act
        Event result = eventService.updateEvent(1L, updatedDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedDTO.getTitle(), result.getTitle());
        assertEquals(updatedDTO.getDescription(), result.getDescription());
        assertEquals(updatedDTO.getType(), result.getType());
        assertEquals(user, result.getUser());

        verify(eventRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1);
        verify(eventRepo, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent_WithInvalidEventId_ShouldThrowException() {
        // Arrange
        when(eventRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            eventService.updateEvent(1L, eventDTO);
        });

        verify(eventRepo, times(1)).findById(1L);
        verify(userRepo, never()).findById(any());
        verify(eventRepo, never()).save(any());
    }

    @Test
    void updateEvent_WithInvalidUserId_ShouldThrowException() {
        // Arrange
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            eventService.updateEvent(1L, eventDTO);
        });

        verify(eventRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1);
        verify(eventRepo, never()).save(any());
    }
}