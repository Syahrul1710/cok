package App;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;

public class HalamanLoginFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // ====== Elemen Login ======
        Label lblTitle = new Label("SISTEM REGISTRASI PENDAKIAN GUNUNG MERBABU");
        lblTitle.getStyleClass().add("login-title");

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Masukkan Username");
        txtUsername.getStyleClass().add("text-field");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Masukkan Password");
        txtPassword.getStyleClass().add("password-field");

        Button btnLogin = new Button("Login");
        btnLogin.getStyleClass().add("login-button");

        Button btnGoRegister = new Button("Belum punya akun? Daftar");
        btnGoRegister.getStyleClass().add("login-button");

        Label lblInfo = new Label();
        lblInfo.getStyleClass().add("login-message");

        VBox loginBox = new VBox(12, lblTitle, txtUsername, txtPassword, btnLogin, btnGoRegister, lblInfo);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getStyleClass().add("login-container");

        StackPane root = new StackPane(loginBox);
        root.getStyleClass().add("stack-pane");

        Scene loginScene = new Scene(root, 800, 600);
        // Gunakan CSS halaman login
        loginScene.getStylesheets().add(getClass().getResource("HalamanLoginFX.css").toExternalForm());

        // ====== Aksi Login ======
        btnLogin.setOnAction(e -> {
            String user = txtUsername.getText().trim();
            String pass = txtPassword.getText().trim();

            // Jika admin login
            if (user.equals("admin") && pass.equals("admin")) {
                // KOREKSI: Memanggil AndminKonfirmasi, BUKAN AdminKonfirmasiFX
                new AndminKonfirmasi().start(primaryStage);
                return;
            }

            // Jika user biasa login
            if (cekLogin(user, pass)) {
                new HalamanUtamaFX().start(primaryStage);
            } else {
                lblInfo.setText("❌ Username atau password salah!");
            }
        });

        // ====== Tombol ke halaman registrasi ======
        btnGoRegister.setOnAction(e -> {
            Scene regScene = buatHalamanRegister(primaryStage, loginScene);
            primaryStage.setScene(regScene);
        });

        primaryStage.setTitle("Login Pendaki Gunung Merbabu");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    // ====== Membuat Halaman Registrasi ======
    private Scene buatHalamanRegister(Stage primaryStage, Scene loginScene) {
        Label lblTitle = new Label("FORM PENDAFTARAN AKUN PENDAKI");
        lblTitle.getStyleClass().add("login-title");

        TextField txtNama = new TextField();
        txtNama.setPromptText("Nama Lengkap");
        txtNama.getStyleClass().add("text-field");

        TextField txtNIK = new TextField();
        txtNIK.setPromptText("Nomor Induk Kependudukan (NIK)");
        txtNIK.getStyleClass().add("text-field");

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        txtUsername.getStyleClass().add("text-field");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.getStyleClass().add("password-field");

        PasswordField txtUlangPassword = new PasswordField();
        txtUlangPassword.setPromptText("Ulangi Password");
        txtUlangPassword.getStyleClass().add("password-field");

        Button btnRegister = new Button("Daftar");
        btnRegister.getStyleClass().add("login-button");

        Button btnKembali = new Button("Kembali ke Login");
        btnKembali.getStyleClass().add("login-button");

        Label lblInfo = new Label();
        lblInfo.getStyleClass().add("login-message");

        VBox regBox = new VBox(12, lblTitle, txtNama, txtNIK, txtUsername, txtPassword, txtUlangPassword, btnRegister, btnKembali, lblInfo);
        regBox.setAlignment(Pos.CENTER);
        regBox.getStyleClass().add("login-container");

        StackPane root = new StackPane(regBox);
        root.getStyleClass().add("stack-pane");

        Scene regScene = new Scene(root, 800, 600);
        regScene.getStylesheets().add(getClass().getResource("HalamanLoginFX.css").toExternalForm());

        // ====== Aksi tombol daftar ======
        btnRegister.setOnAction(e -> {
            String nama = txtNama.getText().trim();
            String nik = txtNIK.getText().trim();
            String user = txtUsername.getText().trim();
            String pass = txtPassword.getText().trim();
            String ulangPass = txtUlangPassword.getText().trim();

            if (nama.isEmpty() || nik.isEmpty() || user.isEmpty() || pass.isEmpty() || ulangPass.isEmpty()) {
                lblInfo.setText("⚠️ Semua kolom wajib diisi!");
                return;
            }
            if (!pass.equals(ulangPass)) {
                lblInfo.setText("❌ Password tidak cocok!");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("user.txt", true))) {
                writer.write(nama + "|" + nik + "|" + user + "|" + pass + "\n");
                lblInfo.setText("✅ Registrasi berhasil! Silakan login.");
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Platform.runLater(() -> primaryStage.setScene(loginScene));
                }).start();
            } catch (IOException ex) {
                lblInfo.setText("❌ Gagal menyimpan data.");
            }
        });

        // ====== Tombol kembali ======
        btnKembali.setOnAction(e -> primaryStage.setScene(loginScene));

        return regScene;
    }

    // ====== Fungsi cek login dari file user.txt ======
    private boolean cekLogin(String user, String pass) {
        try (BufferedReader reader = new BufferedReader(new FileReader("user.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length == 4 && data[2].equals(user) && data[3].equals(pass)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}