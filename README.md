# ChildCare - Ứng dụng Quản lý & Giám sát An toàn Trẻ em (Android Native - Kotlin & Jetpack Compose)

Ứng dụng Android phát triển cho đồ án môn học, tích hợp với Backend ASP.NET Core .NET 8 (`https://earlybrusheddart65.conveyor.cloud/`).

## 🚀 Công nghệ & Kiến trúc
- **Ngôn ngữ**: Kotlin
- **UI Toolkit**: Jetpack Compose & Material 3 (hỗ trợ Light / Dark Mode)
- **Kiến trúc**: MVVM + Repository Pattern + Clean Architecture principles
- **Mạng**: Retrofit 2 + OkHttp 3 + JWT Bearer Authentication (Interceptor)
- **Lưu trữ bảo mật**: Jetpack DataStore Preferences (Lưu token & role, không lưu password)
- **Các tính năng phần cứng & Nâng cao**:
  - **CameraX & Google ML Kit**: Quét mã QR ghép nối thiết bị (`/api/ChildManagement/pair-qr/{qrCode}`).
  - **Google Maps & Location Services**: Chia sẻ vị trí GPS thời gian thực & Giám sát vùng an toàn (Safe Zone).
  - **Audio Recording**: Đo mức tiếng ồn môi trường (Decibel - dB).
  - **SensorManager**: Phát hiện té ngã qua cảm biến gia tốc (Accelerometer).
  - **BiometricPrompt**: Đăng nhập nhanh bằng sinh trắc học (Khuôn mặt / Vân tay).
  - **Instant Safety Lock**: Khóa thiết bị khẩn cấp từ xa.

## 📂 Cấu trúc Thư mục
- `android/app/src/main/java/com/example/childcare/`:
  - `data/` (local, model, remote, repository)
  - `domain/` (model, repository)
  - `ui/` (auth, admin, parent, child, devices, rules, map, qr, telemetry, settings, navigation, theme)
  - `utils/` (BiometricHelper, NetworkResult)

## ⚙️ Cấu hình API
API Base URL được cấu hình tại `android/app/build.gradle.kts`:
```kotlin
buildConfigField("String", "BASE_URL", "\"https://earlybrusheddart65.conveyor.cloud/\"")
```
Các thành viên trong nhóm chỉ cần thay đổi URL tại đây và tiến hành Sync Gradle để chạy.
