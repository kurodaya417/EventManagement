# 📘 イベント管理アプリ (Event Management Application)

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.3-orange.svg)](https://mybatis.org/)
[![Oracle](https://img.shields.io/badge/Oracle-Database-red.svg)](https://www.oracle.com/database/)

## ✅ アプリの目的
参加者がWeb上でイベントを閲覧・申し込みし、主催者がイベントと参加者を管理できるアプリ。  
Azure App Service、Oracle Database、Blob Storage、AD B2C連携の実験対象にも適用可能。

**🎯 現在の実装状況**: バックエンドAPI完全実装済み（Spring Boot + MyBatis + Oracle）

## 概要

このプロジェクトは、イベント管理システムの完全なバックエンドAPIを提供します。REST APIベースで、イベントの作成、更新、削除、検索機能に加えて、参加者の登録・キャンセル・一覧取得機能を提供します。

## 🚀 実装済み機能

- ✅ イベント管理（CRUD操作）
- ✅ ステータス別イベント検索（ACTIVE, COMPLETED, CANCELLED）
- ✅ 主催者別イベント検索
- ✅ イベント統計情報
- ✅ 参加者管理機能（参加登録・キャンセル・一覧取得）
- ✅ OpenAPI/Swagger ドキュメント生成
- ✅ バリデーション機能
- ✅ エラーハンドリング
- ✅ 単体テスト（コントローラー層100%カバレッジ）

## 🏛️ システム構成（サーバーサイド）

| 層 | 使用技術 | 説明 |
|--|--|--|
| Controller | Spring Web (REST API) | /api/events エンドポイント |
| Service | Spring Service | ビジネスロジック（重複チェック、検証など） |
| Repository | MyBatis (XML Mapper) | Oracle DBとの接続（SQLはXMLに記述） |
| DB | Oracle Database | events テーブル |

## 🚀 技術スタック

- **Java**: 17
- **Spring Boot**: 3.2.0
- **MyBatis**: 3.0.3
- **Oracle Database**: 19c+
- **Maven**: 3.8+
- **OpenAPI/Swagger**: 2.2.0

## 📋 API エンドポイント

### ベースURL
```
http://localhost:8080/api
```

### 主要エンドポイント

| Method | Endpoint | 説明 |
|--------|----------|------|
| GET | `/events` | 全イベント取得 |
| GET | `/events/{id}` | イベント詳細取得 |
| GET | `/events/status/{status}` | ステータス別イベント取得 |
| GET | `/events/organizer/{organizer}` | 主催者別イベント取得 |
| GET | `/events/statistics` | イベント統計取得 |
| POST | `/events` | イベント作成 |
| PUT | `/events/{id}` | イベント更新 |
| DELETE | `/events/{id}` | イベント削除 |
| GET | `/participants/event/{eventId}` | イベント参加者一覧取得 |
| GET | `/participants/participant/{email}` | 参加者のイベント一覧取得 |
| POST | `/participants/register` | 参加者登録 |
| DELETE | `/participants/cancel/{participationId}` | 参加キャンセル |
| DELETE | `/participants/cancel/event/{eventId}/email/{email}` | 参加キャンセル（イベント・メール指定） |

## 📂 実装済みディレクトリ構成

```
src/
├── main/java/com/eventmanagement/
│   ├── controller/
│   │   ├── EventController.java          # Event REST Controllers
│   │   └── ParticipantController.java    # Participant REST Controllers
│   ├── service/
│   │   ├── EventService.java            # Event Business Logic
│   │   └── ParticipantService.java      # Participant Business Logic
│   ├── mapper/
│   │   ├── EventMapper.java             # Event MyBatis Mappers
│   │   └── ParticipantMapper.java       # Participant MyBatis Mappers
│   ├── entity/
│   │   ├── Event.java                   # Event Domain Model
│   │   └── Participant.java             # Participant Domain Model
│   ├── dto/
│   │   ├── EventRequest.java            # Event Request DTOs
│   │   ├── EventResponse.java           # Event Response DTOs
│   │   ├── ParticipantRequest.java      # Participant Request DTOs
│   │   ├── ParticipantResponse.java     # Participant Response DTOs
│   │   └── ApiResponse.java             # Common Response DTOs
│   ├── config/
│   │   └── SwaggerConfig.java           # Configuration Classes
│   └── EventManagementApplication.java  # Main Application
├── resources/
│   ├── mappers/
│   │   ├── EventMapper.xml              # Event MyBatis XML Mappers
│   │   └── ParticipationMapper.xml      # Participant MyBatis XML Mappers
│   ├── schema.sql                       # Database Schema
│   └── application.properties           # Application Configuration
└── test/
    └── java/com/eventmanagement/
        └── controller/
            ├── EventControllerTest.java  # Event Controller Tests
            └── ParticipantControllerTest.java # Participant Controller Tests
```

## 🛠️ セットアップ手順

### 1. 前提条件
- Java 17以上
- Maven 3.8以上
- Oracle Database 19c以上

### 2. プロジェクトのクローン

```bash
git clone https://github.com/kurodaya417/EventManagement.git
cd EventManagement
```

### 3. データベース設定

Oracle Databaseを起動し、以下のスクリプトを実行してスキーマを作成:

```sql
-- src/main/resources/schema.sql を参照
```

### 4. アプリケーション設定

`src/main/resources/application.properties` でデータベース接続情報を設定:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 5. ビルドと実行

```bash
# プロジェクトのビルド
mvn clean compile

# テストの実行
mvn test

# アプリケーションの起動
mvn spring-boot:run
```

## 📚 API ドキュメント

アプリケーション起動後、以下のURLでAPI ドキュメントを確認できます：

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI仕様**: http://localhost:8080/api/docs

## 📋 使用例

### イベント作成

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventName": "Spring Boot Workshop",
    "description": "Learn Spring Boot fundamentals",
    "startDateTime": "2024-02-01T10:00:00",
    "endDateTime": "2024-02-01T17:00:00",
    "location": "Conference Room A",
    "organizer": "John Doe",
    "maxParticipants": 30
  }'
```

### イベント一覧取得

```bash
curl http://localhost:8080/api/events
```

### 参加者登録

```bash
curl -X POST http://localhost:8080/api/participants/register \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": 1,
    "participantName": "山田太郎",
    "participantEmail": "yamada@example.com",
    "participantPhone": "090-1234-5678"
  }'
```

### イベント参加者一覧取得

```bash
curl http://localhost:8080/api/participants/event/1
```

### 参加キャンセル

```bash
curl -X DELETE http://localhost:8080/api/participants/cancel/event/1/email/yamada@example.com
```

## 💾 データベーススキーマ

```sql
CREATE TABLE events (
    event_id NUMBER(19) PRIMARY KEY,
    event_name VARCHAR2(255) NOT NULL,
    description CLOB,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    location VARCHAR2(255) NOT NULL,
    organizer VARCHAR2(255) NOT NULL,
    max_participants NUMBER(10) NOT NULL,
    current_participants NUMBER(10) DEFAULT 0,
    status VARCHAR2(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- シーケンス
CREATE SEQUENCE event_seq START WITH 1 INCREMENT BY 1;

-- トリガー
CREATE OR REPLACE TRIGGER event_trigger
  BEFORE INSERT ON events
  FOR EACH ROW
BEGIN
  :NEW.event_id := event_seq.NEXTVAL;
  :NEW.created_at := CURRENT_TIMESTAMP;
  :NEW.updated_at := CURRENT_TIMESTAMP;
END;

-- 参加者テーブル
CREATE TABLE participants (
    participation_id NUMBER(19) PRIMARY KEY,
    event_id NUMBER(19) NOT NULL,
    participant_name VARCHAR2(100) NOT NULL,
    participant_email VARCHAR2(255) NOT NULL,
    participant_phone VARCHAR2(20),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_participants_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT uk_participants_event_email UNIQUE (event_id, participant_email)
);

-- 参加者IDシーケンス
CREATE SEQUENCE participation_id_seq START WITH 1 INCREMENT BY 1;

-- 参加者IDトリガー
CREATE OR REPLACE TRIGGER participation_id_trigger
  BEFORE INSERT ON participants
  FOR EACH ROW
BEGIN
  :NEW.participation_id := participation_id_seq.NEXTVAL;
END;
```

## 🧪 テスト

```bash
# 全テストの実行
mvn test

# 特定のテストクラスの実行
mvn test -Dtest=EventControllerTest

# テストカバレッジの確認
mvn test jacoco:report
```

## 📊 監視・ヘルスチェック

Spring Boot Actuatorを使用してヘルスチェックが可能です：

```bash
# ヘルスチェック
curl http://localhost:8080/api/actuator/health

# 情報取得
curl http://localhost:8080/api/actuator/info
```

## 🌐 Azure対応

本アプリケーションは以下のAzureサービスとの連携に対応：

- **Azure App Service** - Webアプリケーションホスティング
- **Oracle Database** - データベース
- **Azure Blob Storage** - ファイルストレージ
- **Azure AD B2C** - 認証・認可

## 📈 今後の拡張予定

### 完了済み ✅
- [x] バックエンドAPI詳細設計と実装
- [x] Spring Boot + MyBatis + Oracle統合
- [x] OpenAPIドキュメント自動生成
- [x] 単体テスト実装
- [x] バリデーション機能
- [x] エラーハンドリング
- [x] 統計情報API
- [x] 参加者管理機能（/api/participants）

### 計画中 📋
- [ ] UIデザイン案の実装
- [ ] Azureへのデプロイ手順の整備
- [ ] 検索機能の拡張
- [ ] CSV出力機能の追加
- [ ] Azure AD B2C連携
- [ ] Blob Storage連携
- [ ] イベント通知機能
- [ ] Redis キャッシュ機能

## 📖 詳細設計書

より詳細なAPI仕様については、[API詳細設計書](docs/API_SPECIFICATION.md)を参照してください。

## ライセンス

このプロジェクトは MIT License の下で公開されています。

## 貢献

プルリクエストや Issue の報告をお待ちしています。

## サポート

質問やサポートが必要な場合は、Issue を作成してください。