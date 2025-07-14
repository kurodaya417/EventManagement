# 📘 イベント管理アプリ (Event Management Application)

## ✅ アプリの目的
参加者がWeb上でイベントを閲覧・申し込みし、主催者がイベントと参加者を管理できるアプリ。  
Azure App Service、Oracle Database、Blob Storage、AD B2C連携の実験対象にも適用可能。

## 🏛️ システム構成（サーバーサイド）

| 層 | 使用技術 | 説明 |
|--|--|--|
| Controller | Spring Web (REST API) | /api/events, /api/participation 等のエンドポイント |
| Service | Spring Service | ビジネスロジック（重複チェック、検証など） |
| Repository | MyBatis (XML Mapper) | Oracle DBとの接続（SQLはXMLに記述） |
| DB | Oracle Database | event, participation テーブル |

## 📂 ディレクトリ構成

```
src/
├── main/java/com/example/eventapp/
│   ├── controller/
│   │   ├── EventController.java
│   │   └── ParticipationController.java
│   ├── service/
│   │   ├── EventService.java
│   │   └── ParticipationService.java
│   ├── model/
│   │   ├── Event.java
│   │   └── Participation.java
│   ├── repository/
│   │   ├── EventRepository.java
│   │   └── ParticipationRepository.java
│   └── EventAppApplication.java
├── resources/
│   ├── mappers/
│   │   ├── EventMapper.xml
│   │   └── ParticipationMapper.xml
│   └── application.properties
├── test/
│   └── java/com/example/eventapp/
│       ├── controller/
│       │   └── EventControllerTest.java
│       └── service/
│           └── EventServiceTest.java
```

## 🚀 技術スタック

- **Java 21**
- **Spring Boot 3.1.0**
- **MyBatis 3.0.2**
- **Oracle Database**
- **Maven** (ビルドツール)

## 📋 API エンドポイント

### イベント管理 (/api/events)
- `GET /api/events` - 全イベント取得
- `GET /api/events/{id}` - イベント詳細取得
- `POST /api/events` - イベント作成
- `PUT /api/events/{id}` - イベント更新
- `DELETE /api/events/{id}` - イベント削除

### 参加管理 (/api/participation)
- `GET /api/participation/event/{eventId}` - イベントの参加者一覧取得
- `POST /api/participation` - 参加登録
- `DELETE /api/participation/{id}` - 参加キャンセル

## 🛠️ セットアップ手順

### 1. 前提条件
- Java 21以上
- Maven 3.6以上
- Oracle Database

### 2. アプリケーションの起動

```bash
# リポジトリのクローン
git clone https://github.com/kurodaya417/EventManagement.git
cd EventManagement

# Maven依存関係のインストール
mvn clean install

# アプリケーションの起動
mvn spring-boot:run
```

### 3. データベース設定

`src/main/resources/application.properties` で Oracle Database の接続情報を設定：

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=eventapp
spring.datasource.password=password
```

## 🧪 テスト実行

```bash
# 全テストの実行
mvn test

# 特定のテストクラスの実行
mvn test -Dtest=EventControllerTest
```

## 🌐 Azure対応

本アプリケーションは以下のAzureサービスとの連携に対応：

- **Azure App Service** - Webアプリケーションホスティング
- **Oracle Database** - データベース
- **Azure Blob Storage** - ファイルストレージ
- **Azure AD B2C** - 認証・認可

## 📈 今後の拡張予定

- [ ] UIデザイン案の実装
- [ ] Azureへのデプロイ手順の整備
- [ ] OpenAPIドキュメントの生成
- [ ] 検索機能の追加
- [ ] CSV出力機能の追加
- [ ] Azure AD B2C連携
- [ ] Blob Storage連携