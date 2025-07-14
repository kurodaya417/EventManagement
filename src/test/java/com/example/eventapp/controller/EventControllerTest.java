package com.example.eventapp.controller;

import com.example.eventapp.model.Event;
import com.example.eventapp.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * イベントコントローラーのテスト
 */
@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllEvents() throws Exception {
        // テストデータの準備
        Event event1 = new Event("テストイベント1", "説明1", LocalDateTime.now().plusDays(1), "東京", 50);
        Event event2 = new Event("テストイベント2", "説明2", LocalDateTime.now().plusDays(2), "大阪", 30);
        List<Event> events = Arrays.asList(event1, event2);

        // モックの設定
        when(eventService.getAllEvents()).thenReturn(events);

        // テスト実行・検証
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("テストイベント1"))
                .andExpect(jsonPath("$[1].title").value("テストイベント2"));
    }

    @Test
    public void testGetEventById() throws Exception {
        // テストデータの準備
        Event event = new Event("テストイベント", "説明", LocalDateTime.now().plusDays(1), "東京", 50);
        event.setId(1L);

        // モックの設定
        when(eventService.getEventById(1L)).thenReturn(event);

        // テスト実行・検証
        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("テストイベント"));
    }

    @Test
    public void testCreateEvent() throws Exception {
        // テストデータの準備
        Event event = new Event("新しいイベント", "新しい説明", LocalDateTime.now().plusDays(1), "東京", 50);
        event.setId(1L);

        // モックの設定
        when(eventService.createEvent(any(Event.class))).thenReturn(event);

        // テスト実行・検証
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("新しいイベント"));
    }
}