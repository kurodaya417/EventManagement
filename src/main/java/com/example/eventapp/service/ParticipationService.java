package com.example.eventapp.service;

import com.example.eventapp.model.Event;
import com.example.eventapp.model.Participation;
import com.example.eventapp.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 参加サービス
 * ビジネスロジック（重複チェック、定員チェックなど）
 */
@Service
public class ParticipationService {

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private EventService eventService;

    /**
     * イベントの参加者一覧取得
     */
    public List<Participation> getParticipantsByEventId(Long eventId) {
        return participationRepository.findByEventId(eventId);
    }

    /**
     * 参加登録
     */
    public Participation registerParticipation(Participation participation) {
        // 重複チェック
        Participation existing = participationRepository.findByEventIdAndEmail(
            participation.getEventId(), 
            participation.getParticipantEmail()
        );
        if (existing != null) {
            throw new RuntimeException("既に登録済みです");
        }

        // 定員チェック
        Event event = eventService.getEventById(participation.getEventId());
        if (event == null) {
            throw new RuntimeException("イベントが見つかりません");
        }
        
        int currentCount = participationRepository.countByEventId(participation.getEventId());
        if (currentCount >= event.getCapacity()) {
            throw new RuntimeException("定員に達しています");
        }

        participation.setRegisteredAt(LocalDateTime.now());
        participationRepository.insert(participation);

        // 参加者数更新
        eventService.updateParticipantCount(participation.getEventId());

        return participation;
    }

    /**
     * 参加キャンセル
     */
    public boolean cancelParticipation(Long id) {
        Participation participation = participationRepository.findByEventId(id).stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
            
        if (participation == null) {
            return false;
        }

        boolean result = participationRepository.deleteById(id) > 0;
        if (result) {
            eventService.updateParticipantCount(participation.getEventId());
        }
        return result;
    }
}