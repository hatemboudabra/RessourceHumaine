package com.example.PortailRH.service;

import com.example.PortailRH.entity.Event;
import com.example.PortailRH.entity.User;
import com.example.PortailRH.entity.dto.EventDTO;
import com.example.PortailRH.repository.EventRepo;
import com.example.PortailRH.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    @Override
    public List<Event> getAllEvent() {
        return eventRepo.findAll();
    }

    @Override
    public Event addEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setType(eventDTO.getType());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        User user = userRepo.findById(Math.toIntExact(eventDTO.getUserId())).get();
        event.setUser(user);

        return eventRepo.save(event);
    }

    @Override
    public List<Event> findByUserId(Long userId) {
        return eventRepo.findByUserId(userId);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepo.deleteById(id);
    }

    @Override
    public Event updateEvent(Long id, EventDTO eventDTO) {
        Event event = eventRepo.findById(id).get();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setType(eventDTO.getType());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        User user = userRepo.findById(Math.toIntExact(eventDTO.getUserId())).get();
        event.setUser(user);
        return eventRepo.save(event);
    }
}
