package com.example.eventapp.service;

import com.example.eventapp.model.Event;
import com.example.eventapp.repository.EventRepository;
import com.example.eventapp.repository.ParticipationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * イベントサービスのテスト
 */
@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    @BeforeEach
    public void setUp() {
        testEvent = new Event("テストイベント", "テスト説明", LocalDateTime.now().plusDays(1), "東京", 50);
        testEvent.setId(1L);
    }

    @Test
    public void testGetAllEvents() {
        // テストデータの準備
        List<Event> expectedEvents = Arrays.asList(testEvent);
        when(eventRepository.findAll()).thenReturn(expectedEvents);

        // テスト実行
        List<Event> actualEvents = eventService.getAllEvents();

        // 検証
        assertEquals(1, actualEvents.size());
        assertEquals("テストイベント", actualEvents.get(0).getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testGetEventById() {
        // モックの設定
        when(eventRepository.findById(1L)).thenReturn(testEvent);

        // テスト実行
        Event actual = eventService.getEventById(1L);

        // 検証
        assertNotNull(actual);
        assertEquals("テストイベント", actual.getTitle());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateEvent() {
        // モックの設定
        when(eventRepository.insert(any(Event.class))).thenReturn(1);

        // テスト実行
        Event actual = eventService.createEvent(testEvent);

        // 検証
        assertNotNull(actual);
        assertEquals(0, actual.getCurrentParticipants());
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());
        verify(eventRepository, times(1)).insert(testEvent);
    }

    @Test
    public void testUpdateEvent() {
        // モックの設定
        when(eventRepository.update(any(Event.class))).thenReturn(1);

        // テスト実行
        Event actual = eventService.updateEvent(testEvent);

        // 検証
        assertNotNull(actual);
        assertNotNull(actual.getUpdatedAt());
        verify(eventRepository, times(1)).update(testEvent);
    }

    @Test
    public void testDeleteEvent() {
        // モックの設定
        when(eventRepository.deleteById(1L)).thenReturn(1);

        // テスト実行
        boolean result = eventService.deleteEvent(1L);

        // 検証
        assertTrue(result);
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateParticipantCount() {
        // モックの設定
        when(participationRepository.countByEventId(1L)).thenReturn(5);
        when(eventRepository.updateParticipantCount(1L, 5)).thenReturn(1);

        // テスト実行
        eventService.updateParticipantCount(1L);

        // 検証
        verify(participationRepository, times(1)).countByEventId(1L);
        verify(eventRepository, times(1)).updateParticipantCount(1L, 5);
    }
}