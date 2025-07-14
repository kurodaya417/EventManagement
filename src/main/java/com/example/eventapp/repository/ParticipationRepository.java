package com.example.eventapp.repository;

import com.example.eventapp.model.Participation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 参加リポジトリ
 * MyBatis を使用した Oracle Database との接続
 */
@Mapper
public interface ParticipationRepository {

    /**
     * イベントIDによる参加者一覧取得
     */
    List<Participation> findByEventId(@Param("eventId") Long eventId);

    /**
     * 参加登録
     */
    int insert(Participation participation);

    /**
     * 重複参加チェック
     */
    Participation findByEventIdAndEmail(@Param("eventId") Long eventId, @Param("email") String email);

    /**
     * 参加キャンセル
     */
    int deleteById(@Param("id") Long id);

    /**
     * イベントの参加者数カウント
     */
    int countByEventId(@Param("eventId") Long eventId);
}