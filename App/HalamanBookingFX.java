package App;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;

public class HalamanBookingFX {

    private static final int KUOTA_MAKSIMAL = 300;
    private static final String DATA_FILE = "booking_data.txt";

    public void start(Stage primaryStage) {
        // Navbar
        Button btnHome = new Button("TNGM");
        Button btnPanduanBooking = new Button("Panduan Booking");
        Button btnPanduanPembayaran = new Button("Panduan Pembayaran");
        Button btnLogout = new Button("Logout");

        HBox navbar = new HBox(20, btnHome, btnPanduanBooking, btnPanduanPembayaran, btnLogout);
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(15));
        navbar.getStyleClass().add("navbar");

        // Judul
        Label lblTitle = new Label("Form Booking Pendakian - Tahap 1");
        lblTitle.getStyleClass().add("judul");

        // Input data ketua
        TextField txtNamaKetua = new TextField();
        txtNamaKetua.setPromptText("Nama Ketua Pendaki");

        TextField txtNIK = new TextField();
        txtNIK.setPromptText("NIK Ketua Pendaki");

        TextField txtNoHPKetua = new TextField();
        txtNoHPKetua.setPromptText("Nomor HP Ketua Pendaki");

        // Input data keluarga darurat
        TextField txtNamaKontak = new TextField();
        txtNamaKontak.setPromptText("Nama Kontak Darurat");

        TextField txtNoHPKontak = new TextField();
        txtNoHPKontak.setPromptText("Nomor HP Keluarga yang Dapat Dihubungi");

        ComboBox<String> cbStatusHubungan = new ComboBox<>();
        cbStatusHubungan.getItems().addAll("Orang Tua", "Teman", "Saudara", "Pasangan");
        cbStatusHubungan.setPromptText("Hubungan dengan Ketua");

        // Input tanggal, jalur, guide
        DatePicker dpTanggal = new DatePicker();
        dpTanggal.setPromptText("Tanggal Pendakian");

        Spinner<Integer> spDurasi = new Spinner<>(1, 5, 1);
        spDurasi.setEditable(true);

        ComboBox<String> cbJalur = new ComboBox<>();
        cbJalur.getItems().addAll("Selo", "Wekas", "Cuntel", "Thekelan", "Suwanting");
        cbJalur.setPromptText("Pilih Jalur Pendakian");

        ToggleGroup guideGroup = new ToggleGroup();
        RadioButton rbYa = new RadioButton("Dengan Guide");
        RadioButton rbTidak = new RadioButton("Tanpa Guide");
        rbYa.setToggleGroup(guideGroup);
        rbTidak.setToggleGroup(guideGroup);

        HBox guideBox = new HBox(10, rbYa, rbTidak);
        guideBox.setAlignment(Pos.CENTER);

        Label lblInfo = new Label();
        lblInfo.getStyleClass().add("deskripsi");

        // Tombol lanjut
        Button btnLanjut = new Button("Lanjut ➜");
        btnLanjut.getStyleClass().add("btn-utama");

        btnLanjut.setOnAction(e -> {
            // Validasi semua field wajib diisi
            if (txtNamaKetua.getText().isEmpty() || txtNIK.getText().isEmpty() ||
                txtNoHPKetua.getText().isEmpty() || txtNamaKontak.getText().isEmpty() ||
                txtNoHPKontak.getText().isEmpty() || cbStatusHubungan.getValue() == null ||
                dpTanggal.getValue() == null || cbJalur.getValue() == null ||
                guideGroup.getSelectedToggle() == null) {

                lblInfo.setText("⚠️ Harap isi semua data dengan lengkap!");
                return;
            }

            LocalDate tanggal = dpTanggal.getValue();
            String jalur = cbJalur.getValue();

            int kuotaTerpakai = getJumlahBookingUntukTanggalDanJalur(tanggal, jalur);
            if (kuotaTerpakai >= KUOTA_MAKSIMAL) {
                lblInfo.setText("❌ Kuota untuk jalur " + jalur + " pada " + tanggal + " sudah penuh!");
                return;
            }

            boolean pakaiGuide = rbYa.isSelected();
            int durasi = spDurasi.getValue();
            simpanBookingSementara(tanggal, jalur);

            // Kirim ke halaman booking lanjutan
            new HalamanBookingLanjutanFX(
                    txtNamaKetua.getText(),
                    txtNIK.getText(),
                    tanggal.toString(),
                    jalur,
                    pakaiGuide,
                    durasi
            ).start(primaryStage);
        });

        // Layout form
        VBox form = new VBox(10,
                lblTitle,
                new Label("Data Ketua Pendaki"),
                txtNamaKetua, txtNIK, txtNoHPKetua,
                new Label("Kontak Darurat (Keluarga/Teman yang Dapat Dihubungi)"),
                txtNamaKontak, txtNoHPKontak, cbStatusHubungan,
                new Label("Tanggal Pendakian"), dpTanggal,
                new Label("Durasi Pendakian (hari)"), spDurasi,
                new Label("Pilih Jalur Pendakian"), cbJalur,
                new Label("Apakah menggunakan guide?"), guideBox,
                btnLanjut, lblInfo
        );
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30, 40, 30, 40));
        form.setMaxWidth(400);
        form.setStyle("-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 10;");

        StackPane content = new StackPane(form);
        content.setAlignment(Pos.CENTER);

        // Background
        BackgroundImage bg = new BackgroundImage(
                new Image("App/images/background.png", 800, 600, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );

        BorderPane root = new BorderPane();
        root.setTop(navbar);
        root.setCenter(content);
        root.setBackground(new Background(bg));

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("HalamanUtama.css").toExternalForm());
        primaryStage.setTitle("Booking Pendakian");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int getJumlahBookingUntukTanggalDanJalur(LocalDate tanggal, String jalur) {
        int jumlah = 0;
        File file = new File(DATA_FILE);
        if (!file.exists()) return 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(tanggal + ";" + jalur)) jumlah++;
            }
        } catch (IOException e) { e.printStackTrace(); }
        return jumlah;
    }

    private void simpanBookingSementara(LocalDate tanggal, String jalur) {
        try (FileWriter writer = new FileWriter(DATA_FILE, true)) {
            writer.write(tanggal + ";" + jalur + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
