package com.example.PortailRH.service;

import com.example.PortailRH.entity.Event;
import com.example.PortailRH.entity.dto.EventDTO;

import java.util.List;

public interface EventService {
    public List<Event> getAllEvent ();
    public Event addEvent(EventDTO eventDTO);
    List<Event> findByUserId(Long userId);
    public void deleteEvent(Long id);
    public Event updateEvent(Long id, EventDTO eventDTO);
}
