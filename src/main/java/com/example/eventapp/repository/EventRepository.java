package com.example.eventapp.repository;

import com.example.eventapp.model.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * イベントリポジトリ
 * MyBatis を使用した Oracle Database との接続
 */
@Mapper
public interface EventRepository {

    /**
     * 全イベントを取得
     */
    List<Event> findAll();

    /**
     * IDによるイベント取得
     */
    Event findById(@Param("id") Long id);

    /**
     * イベント作成
     */
    int insert(Event event);

    /**
     * イベント更新
     */
    int update(Event event);

    /**
     * イベント削除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 参加者数更新
     */
    int updateParticipantCount(@Param("id") Long id, @Param("count") Integer count);
}