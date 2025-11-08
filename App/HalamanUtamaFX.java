package App;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HalamanUtamaFX {

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

        // ========== TENGAH (CONTENT) ==========
        Label lblJudul = new Label("Gunung Merbabu");
        lblJudul.getStyleClass().add("judul");

        Label lblDeskripsi = new Label(
                "Puncak Merbabu dikenal dengan nama puncak Kenteng Songo\n" +
                "dengan ketinggian 3.142 mdpl."
        );
        lblDeskripsi.getStyleClass().add("deskripsi");

        Button btnBooking = new Button("Booking");
        Button btnStatus = new Button("Status Booking");
        btnBooking.getStyleClass().add("btn-utama");
        btnStatus.getStyleClass().add("btn-utama");

        HBox tombolBox = new HBox(15, btnBooking, btnStatus);
        tombolBox.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, lblJudul, lblDeskripsi, tombolBox);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(80, 0, 0, 0));

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
        btnPanduanBooking.setOnAction(e -> new HalamanPanduanBookingFX().start(primaryStage));
        btnPanduanPembayaran.setOnAction(e -> new HalamanPanduanPembayaranFX().start(primaryStage));
        btnBooking.setOnAction(e -> new HalamanBookingFX().start(primaryStage));
        btnStatus.setOnAction(e -> new HalamanStatusBookingFX().start(primaryStage));
        btnLogout.setOnAction(e -> {
            try {
                new HalamanLoginFX().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("HalamanUtama.css").toExternalForm());

        primaryStage.setTitle("Sistem Registrasi Pendakian");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
