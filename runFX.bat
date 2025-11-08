@echo off
title 🚀 JavaFX Launcher - Proyek Pendakian
echo ===========================================
echo 🚀  Men-compile dan menjalankan JavaFX App
echo ===========================================
echo.

:: Langkah 1: Compile semua file Java di folder App
echo 🔧 Meng-compile file .java ...
javac --module-path "javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml App\*.java
if %errorlevel% neq 0 (
    echo ❌ Terjadi error saat compile!
    pause
    exit /b
)
echo ✅ Compile selesai!
echo.

:: Langkah 2: Jalankan aplikasi utama
echo ▶️ Menjalankan aplikasi...
java --module-path "javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml App.HalamanLoginFX

echo.
echo ✅ Program telah selesai dijalankan.
pause
