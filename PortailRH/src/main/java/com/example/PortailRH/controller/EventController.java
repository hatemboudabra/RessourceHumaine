package com.example.PortailRH.controller;

import com.example.PortailRH.entity.Event;
import com.example.PortailRH.entity.Reclamation;
import com.example.PortailRH.entity.dto.EventDTO;
import com.example.PortailRH.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    @GetMapping("/allevent")
    public ResponseEntity<List<Event>> getallEvent() {
       List<Event> eventList =  eventService.getAllEvent();
        return  ResponseEntity.ok(eventList);
    }
    @PostMapping("/addEvent")
    public ResponseEntity<Event> addEvent(@RequestBody EventDTO eventDTO) {
        eventService.addEvent(eventDTO);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("Event/{id}")
    public List<Event> getEventByUser(@PathVariable Long id) {
        return eventService.findByUserId(id);
    }
    @PutMapping("/updateEvent/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        Event updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }
    @DeleteMapping("/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
