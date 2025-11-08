package App;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HalamanPanduanPembayaranFX {

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
        Label lblTitle = new Label("Panduan Pembayaran Pendakian");
        lblTitle.getStyleClass().add("judul");

        Label lblText = new Label(
            "Langkah-langkah pembayaran:\n" +
            "1. Setelah booking, buka menu 'Status Booking'.\n" +
            "2. Klik 'Lakukan Pembayaran'.\n" +
            "3. Transfer biaya ke rekening resmi:\n" +
            "   BCA 123-456-789 a.n Taman Nasional Merbabu.\n" +
            "4. Tekan 'Konfirmasi Pembayaran'.\n" +
            "5. Unduh tiket pendakian setelah pembayaran diterima."
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
        btnPanduanBooking.setOnAction(e -> new HalamanPanduanBookingFX().start(primaryStage));
        btnLogout.setOnAction(e -> {
            try {
                new HalamanLoginFX().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // ========== SCENE ==========
        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("HalamanUtama.css").toExternalForm());

        primaryStage.setTitle("Panduan Pembayaran");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
