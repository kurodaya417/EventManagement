package com.example.eventapp.controller;

import com.example.eventapp.model.Participation;
import com.example.eventapp.service.ParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参加コントローラー
 * REST API エンドポイント: /api/participation
 */
@RestController
@RequestMapping("/api/participation")
@CrossOrigin(origins = "*")
public class ParticipationController {

    @Autowired
    private ParticipationService participationService;

    /**
     * イベントの参加者一覧取得
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Participation>> getParticipantsByEventId(@PathVariable Long eventId) {
        List<Participation> participants = participationService.getParticipantsByEventId(eventId);
        return ResponseEntity.ok(participants);
    }

    /**
     * 参加登録
     */
    @PostMapping
    public ResponseEntity<String> registerParticipation(@RequestBody Participation participation) {
        try {
            Participation registered = participationService.registerParticipation(participation);
            return ResponseEntity.status(HttpStatus.CREATED).body("参加登録が完了しました");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登録処理中にエラーが発生しました");
        }
    }

    /**
     * 参加キャンセル
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelParticipation(@PathVariable Long id) {
        boolean cancelled = participationService.cancelParticipation(id);
        if (cancelled) {
            return ResponseEntity.ok("参加をキャンセルしました");
        }
        return ResponseEntity.notFound().build();
    }
}