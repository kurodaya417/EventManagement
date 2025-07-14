# 📘 バックエンド API 詳細設計書

## 基本情報

| 項目 | 内容 |
|------|------|
| ベースURL | `http://localhost:8080/api` |
| レスポンス形式 | JSON |
| 認証 | なし（将来的に Azure AD B2C 連携可能） |
| 使用技術 | Spring Boot 3.2.0, MyBatis 3.0.3, Oracle Database |

## API エンドポイント一覧

### 1. イベント管理 API

#### 1.1 全イベント取得
- **エンドポイント**: `GET /events`
- **概要**: システム内の全イベントを取得
- **認証**: 不要

**レスポンス例**:
```json
{
  "success": true,
  "message": "Events retrieved successfully",
  "data": [
    {
      "eventId": 1,
      "eventName": "Spring Boot Workshop",
      "description": "Learn Spring Boot fundamentals and advanced features",
      "startDateTime": "2024-02-01T10:00:00",
      "endDateTime": "2024-02-01T17:00:00",
      "location": "Conference Room A",
      "organizer": "John Doe",
      "maxParticipants": 30,
      "currentParticipants": 15,
      "status": "ACTIVE",
      "createdAt": "2024-01-15T09:30:00",
      "updatedAt": "2024-01-15T09:30:00"
    }
  ],
  "timestamp": "2024-01-15T10:00:00"
}
```

#### 1.2 イベント詳細取得
- **エンドポイント**: `GET /events/{id}`
- **概要**: 指定されたIDのイベント詳細を取得
- **認証**: 不要

**パラメータ**:
| パラメータ | 型 | 必須 | 説明 |
|-----------|----|----|------|
| id | Long | ✓ | イベントID |

**レスポンス例**:
```json
{
  "success": true,
  "message": "Event retrieved successfully",
  "data": {
    "eventId": 1,
    "eventName": "Spring Boot Workshop",
    "description": "Learn Spring Boot fundamentals and advanced features",
    "startDateTime": "2024-02-01T10:00:00",
    "endDateTime": "2024-02-01T17:00:00",
    "location": "Conference Room A",
    "organizer": "John Doe",
    "maxParticipants": 30,
    "currentParticipants": 15,
    "status": "ACTIVE",
    "createdAt": "2024-01-15T09:30:00",
    "updatedAt": "2024-01-15T09:30:00"
  },
  "timestamp": "2024-01-15T10:00:00"
}
```

#### 1.3 ステータス別イベント取得
- **エンドポイント**: `GET /events/status/{status}`
- **概要**: 指定されたステータスのイベントを取得
- **認証**: 不要

**パラメータ**:
| パラメータ | 型 | 必須 | 説明 |
|-----------|----|----|------|
| status | String | ✓ | イベントステータス (ACTIVE, COMPLETED, CANCELLED) |

#### 1.4 主催者別イベント取得
- **エンドポイント**: `GET /events/organizer/{organizer}`
- **概要**: 指定された主催者のイベントを取得
- **認証**: 不要

**パラメータ**:
| パラメータ | 型 | 必須 | 説明 |
|-----------|----|----|------|
| organizer | String | ✓ | 主催者名 |

#### 1.5 イベント作成
- **エンドポイント**: `POST /events`
- **概要**: 新しいイベントを作成
- **認証**: 不要

**リクエストボディ**:
```json
{
  "eventName": "Spring Boot Workshop",
  "description": "Learn Spring Boot fundamentals and advanced features",
  "startDateTime": "2024-02-01T10:00:00",
  "endDateTime": "2024-02-01T17:00:00",
  "location": "Conference Room A",
  "organizer": "John Doe",
  "maxParticipants": 30
}
```

**バリデーション**:
| フィールド | 制約 |
|-----------|------|
| eventName | 必須、空文字不可 |
| startDateTime | 必須、未来の日時 |
| endDateTime | 必須、未来の日時 |
| location | 必須、空文字不可 |
| organizer | 必須、空文字不可 |
| maxParticipants | 正の整数 |

#### 1.6 イベント更新
- **エンドポイント**: `PUT /events/{id}`
- **概要**: 既存のイベントを更新
- **認証**: 不要

**パラメータ**:
| パラメータ | 型 | 必須 | 説明 |
|-----------|----|----|------|
| id | Long | ✓ | イベントID |

**リクエストボディ**: イベント作成と同じ形式

#### 1.7 イベント削除
- **エンドポイント**: `DELETE /events/{id}`
- **概要**: 指定されたイベントを削除
- **認証**: 不要

**パラメータ**:
| パラメータ | 型 | 必須 | 説明 |
|-----------|----|----|------|
| id | Long | ✓ | イベントID |

#### 1.8 イベント統計取得
- **エンドポイント**: `GET /events/statistics`
- **概要**: イベント統計情報を取得
- **認証**: 不要

**レスポンス例**:
```json
{
  "success": true,
  "message": "Statistics retrieved successfully",
  "data": {
    "totalEvents": 100,
    "activeEvents": 45,
    "completedEvents": 50,
    "cancelledEvents": 5
  },
  "timestamp": "2024-01-15T10:00:00"
}
```

## データモデル

### Event Entity
```java
{
  "eventId": "Long - イベントID（主キー）",
  "eventName": "String - イベント名",
  "description": "String - イベント説明",
  "startDateTime": "LocalDateTime - 開始日時",
  "endDateTime": "LocalDateTime - 終了日時",
  "location": "String - 開催場所",
  "organizer": "String - 主催者",
  "maxParticipants": "Integer - 最大参加者数",
  "currentParticipants": "Integer - 現在の参加者数",
  "status": "String - ステータス（ACTIVE, COMPLETED, CANCELLED）",
  "createdAt": "LocalDateTime - 作成日時",
  "updatedAt": "LocalDateTime - 更新日時"
}
```

## エラーハンドリング

### 標準エラーレスポンス
```json
{
  "success": false,
  "message": "エラーメッセージ",
  "data": null,
  "timestamp": "2024-01-15T10:00:00"
}
```

### HTTPステータスコード
| ステータス | 説明 |
|-----------|------|
| 200 | 成功 |
| 201 | 作成成功 |
| 400 | リクエストエラー |
| 404 | リソースが見つからない |
| 500 | サーバーエラー |

## データベース設計

### テーブル: events
| カラム名 | データ型 | 制約 | 説明 |
|---------|---------|------|------|
| event_id | NUMBER(19) | PRIMARY KEY | イベントID |
| event_name | VARCHAR2(255) | NOT NULL | イベント名 |
| description | CLOB | | イベント説明 |
| start_date_time | TIMESTAMP | NOT NULL | 開始日時 |
| end_date_time | TIMESTAMP | NOT NULL | 終了日時 |
| location | VARCHAR2(255) | NOT NULL | 開催場所 |
| organizer | VARCHAR2(255) | NOT NULL | 主催者 |
| max_participants | NUMBER(10) | NOT NULL | 最大参加者数 |
| current_participants | NUMBER(10) | DEFAULT 0 | 現在の参加者数 |
| status | VARCHAR2(20) | DEFAULT 'ACTIVE' | ステータス |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 更新日時 |

## 技術構成

### フレームワーク・ライブラリ
- **Spring Boot**: 3.2.0
- **MyBatis**: 3.0.3
- **Oracle JDBC**: 23.3.0.23.09
- **SpringDoc OpenAPI**: 2.2.0
- **Bean Validation**: 3.0系

### パッケージ構成
```
com.eventmanagement/
├── controller/     # REST Controller
├── service/        # Business Logic
├── mapper/         # MyBatis Mapper
├── entity/         # Domain Model
├── dto/           # Data Transfer Object
├── config/        # Configuration
└── exception/     # Exception Handling
```

## 開発・運用

### 開発環境
- **Java**: 17
- **Maven**: 3.8+
- **Oracle Database**: 19c+

### ログ設定
- **レベル**: DEBUG（開発）, INFO（本番）
- **出力先**: コンソール + ファイル
- **フォーマット**: JSON形式

### ヘルスチェック
- **エンドポイント**: `/actuator/health`
- **監視項目**: データベース接続、アプリケーション状態

## OpenAPI/Swagger

### アクセスURL
- **API仕様**: `http://localhost:8080/api/docs`
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`

### 自動生成機能
- OpenAPI 3.0形式での仕様書自動生成
- 対話型APIドキュメント（Swagger UI）
- リクエスト/レスポンス例の自動生成

## 将来的な拡張

### 認証・認可
- Azure AD B2C連携
- JWT トークンベース認証
- ロールベースアクセス制御

### 機能拡張
- 参加者管理機能
- イベント通知機能
- ファイルアップロード機能
- レポート生成機能

### パフォーマンス改善
- Redis キャッシュ
- データベース接続プール調整
- 検索機能の最適化