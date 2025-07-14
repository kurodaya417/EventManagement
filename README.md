# Event Management Backend API

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.3-orange.svg)](https://mybatis.org/)
[![Oracle](https://img.shields.io/badge/Oracle-Database-red.svg)](https://www.oracle.com/database/)

イベント管理システムのバックエンドAPI（Spring Boot + MyBatis + Oracle）

## 概要

このプロジェクトは、イベント管理システムのバックエンドAPIを提供します。REST APIベースで、イベントの作成、更新、削除、検索機能を提供します。

## 技術スタック

- **Java**: 17
- **Spring Boot**: 3.2.0
- **MyBatis**: 3.0.3
- **Oracle Database**: 19c+
- **Maven**: 3.8+
- **OpenAPI/Swagger**: 2.2.0

## 機能

- ✅ イベント管理（CRUD操作）
- ✅ ステータス別イベント検索
- ✅ 主催者別イベント検索
- ✅ イベント統計情報
- ✅ OpenAPI/Swagger ドキュメント生成
- ✅ バリデーション機能
- ✅ エラーハンドリング

## API エンドポイント

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

## セットアップ

### 前提条件

- Java 17以上
- Maven 3.8以上
- Oracle Database 19c以上

### 1. プロジェクトのクローン

```bash
git clone https://github.com/kurodaya417/EventManagement.git
cd EventManagement
```

### 2. データベース設定

Oracle Databaseを起動し、以下のスクリプトを実行してスキーマを作成:

```sql
-- src/main/resources/schema.sql を参照
```

### 3. アプリケーション設定

`src/main/resources/application.properties` でデータベース接続情報を設定:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. ビルドと実行

```bash
# プロジェクトのビルド
mvn clean compile

# テストの実行
mvn test

# アプリケーションの起動
mvn spring-boot:run
```

## API ドキュメント

アプリケーション起動後、以下のURLでAPI ドキュメントを確認できます：

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI仕様**: http://localhost:8080/api/docs

## 使用例

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

## データベーススキーマ

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
```

## プロジェクト構成

```
src/
├── main/
│   ├── java/com/eventmanagement/
│   │   ├── controller/         # REST Controllers
│   │   ├── service/           # Business Logic
│   │   ├── mapper/            # MyBatis Mappers
│   │   ├── entity/            # Domain Models
│   │   ├── dto/               # Data Transfer Objects
│   │   └── config/            # Configuration Classes
│   └── resources/
│       ├── mappers/           # MyBatis XML Mappers
│       ├── schema.sql         # Database Schema
│       └── application.properties
└── test/
    └── java/com/eventmanagement/
        └── controller/        # Controller Tests
```

## テスト

```bash
# 全テストの実行
mvn test

# 特定のテストクラスの実行
mvn test -Dtest=EventControllerTest
```

## 監視・ヘルスチェック

Spring Boot Actuatorを使用してヘルスチェックが可能です：

```bash
# ヘルスチェック
curl http://localhost:8080/api/actuator/health

# 情報取得
curl http://localhost:8080/api/actuator/info
```

## 将来的な拡張

- 🔐 認証・認可機能（Azure AD B2C連携）
- 📧 イベント通知機能
- 👥 参加者管理機能
- 📊 詳細レポート機能
- 💾 Redis キャッシュ機能

## 詳細設計書

より詳細なAPI仕様については、[API詳細設計書](docs/API_SPECIFICATION.md)を参照してください。

## ライセンス

このプロジェクトは MIT License の下で公開されています。

## 貢献

プルリクエストや Issue の報告をお待ちしています。

## サポート

質問やサポートが必要な場合は、Issue を作成してください。