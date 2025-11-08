package App;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HalamanPanduanBookingFX {

    public void start(Stage primaryStage) {
        // ========== NAVBAR ==========
        Button btnHome = new Button("TNGM");
        Button btnPanduanBooking = new Button("Panduan Booking");
        Button btnPanduanPembayaran = new Button("Panduan Pembayaran");
        Button btnLogout = new Button("Logout");

        HBox navbar = new HBox(20, btnHome, btnPanduanBooking, btnPanduanPembayaran, btnLogout);
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(15));
        navbar.getStyleClass().add("navbar");

        // ========== ISI KONTEN ==========
        Label lblTitle = new Label("Panduan Booking Pendakian");
        lblTitle.getStyleClass().add("judul");

        Label lblText = new Label(
            "Langkah-langkah booking:\n" +
            "1. Login ke sistem.\n" +
            "2. Klik tombol 'Booking Pendakian'.\n" +
            "3. Isi data ketua, NIK, tanggal, jalur, dan pilihan guide.\n" +
            "4. Isi data anggota dan perbekalan.\n" +
            "5. Simpan data untuk melanjutkan pembayaran."
        );
        lblText.getStyleClass().add("deskripsi");
        lblText.setWrapText(true);

        Button btnBack = new Button("⬅ Kembali");
        btnBack.getStyleClass().add("btn-utama");
        btnBack.setOnAction(e -> new HalamanUtamaFX().start(primaryStage));

        VBox content = new VBox(15, lblTitle, lblText, btnBack);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40, 30, 30, 30));

        // ========== BACKGROUND IMAGE ==========
        BackgroundImage bgImage = new BackgroundImage(
                new Image("App/images/background.png", 800, 600, false, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );

        BorderPane root = new BorderPane();
        root.setTop(navbar);
        root.setCenter(content);
        root.setBackground(new Background(bgImage));

        // ========== EVENT HANDLER ==========
        btnHome.setOnAction(e -> new HalamanUtamaFX().start(primaryStage));
        btnPanduanPembayaran.setOnAction(e -> new HalamanPanduanPembayaranFX().start(primaryStage));
        btnLogout.setOnAction(e -> {
            try {
                new HalamanLoginFX().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("HalamanUtama.css").toExternalForm());

        primaryStage.setTitle("Panduan Booking");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
