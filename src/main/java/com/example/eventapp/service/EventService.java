package com.example.eventapp.service;

import com.example.eventapp.model.Event;
import com.example.eventapp.repository.EventRepository;
import com.example.eventapp.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * イベントサービス
 * ビジネスロジック（重複チェック、検証など）
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    /**
     * 全イベント取得
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * イベント詳細取得
     */
    public Event getEventById(Long id) {
        return eventRepository.findById(id);
    }

    /**
     * イベント作成
     */
    public Event createEvent(Event event) {
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setCurrentParticipants(0);
        
        eventRepository.insert(event);
        return event;
    }

    /**
     * イベント更新
     */
    public Event updateEvent(Event event) {
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.update(event);
        return event;
    }

    /**
     * イベント削除
     */
    public boolean deleteEvent(Long id) {
        return eventRepository.deleteById(id) > 0;
    }

    /**
     * 参加者数更新
     */
    public void updateParticipantCount(Long eventId) {
        int count = participationRepository.countByEventId(eventId);
        eventRepository.updateParticipantCount(eventId, count);
    }
}