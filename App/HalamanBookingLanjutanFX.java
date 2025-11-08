package App;

import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.BufferedWriter; 
import java.io.IOException;
import java.io.File; // Import File

public class HalamanBookingLanjutanFX {

    private final String namaKetua, nikKetua, tanggal, jalur;
    private final boolean pakaiGuide;
    private final int durasi;

    public HalamanBookingLanjutanFX(String namaKetua, String nikKetua, String tanggal, String jalur, boolean pakaiGuide, int durasi) {
        this.namaKetua = namaKetua;
        this.nikKetua = nikKetua;
        this.tanggal = tanggal;
        this.jalur = jalur;
        this.pakaiGuide = pakaiGuide;
        this.durasi = durasi;
    }

    public void start(Stage primaryStage) {
        Label lblTitle = new Label("Form Booking Pendakian - Tahap 2");
        lblTitle.getStyleClass().add("judul");

        // === TABEL ANGGOTA ===
        Label lblAnggota = new Label("Daftar Anggota Pendakian (Minimal 2 Orang):");

        TableView<Anggota> tableAnggota = new TableView<>();
        ObservableList<Anggota> anggotaList = FXCollections.observableArrayList();

        TableColumn<Anggota, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(c -> c.getValue().namaProperty());
        colNama.setPrefWidth(150);

        TableColumn<Anggota, String> colNIK = new TableColumn<>("NIK");
        colNIK.setCellValueFactory(c -> c.getValue().nikProperty());
        colNIK.setPrefWidth(150);

        TableColumn<Anggota, String> colHP = new TableColumn<>("No HP");
        colHP.setCellValueFactory(c -> c.getValue().hpProperty());
        colHP.setPrefWidth(150);

        tableAnggota.getColumns().addAll(colNama, colNIK, colHP);
        tableAnggota.setItems(anggotaList);
        tableAnggota.setPlaceholder(new Label("Belum ada anggota ditambahkan."));

        TextField txtNama = new TextField();
        txtNama.setPromptText("Nama");

        TextField txtNIK = new TextField();
        txtNIK.setPromptText("NIK");

        TextField txtHP = new TextField();
        txtHP.setPromptText("No HP");

        Button btnTambahAnggota = new Button("Tambah Anggota");
        btnTambahAnggota.setOnAction(e -> {
            if (txtNama.getText().isEmpty() || txtNIK.getText().isEmpty() || txtHP.getText().isEmpty()) return;
            anggotaList.add(new Anggota(txtNama.getText(), txtNIK.getText(), txtHP.getText()));
            txtNama.clear(); txtNIK.clear(); txtHP.clear();
        });

        Button btnHapusAnggota = new Button("Hapus Terpilih");
        btnHapusAnggota.setOnAction(e -> {
            Anggota selected = tableAnggota.getSelectionModel().getSelectedItem();
            if (selected != null) anggotaList.remove(selected);
        });

        HBox addAnggotaBox = new HBox(10, txtNama, txtNIK, txtHP, btnTambahAnggota, btnHapusAnggota);
        addAnggotaBox.setAlignment(Pos.CENTER);

        // === TABEL PERBEKALAN ===
        Label lblPerbekalan = new Label("Perbekalan Wajib dan Tambahan:");

        TableView<Perbekalan> tablePerbekalan = new TableView<>();
        ObservableList<Perbekalan> perbekalanList = FXCollections.observableArrayList(
                new Perbekalan("Jaket", 1),
                new Perbekalan("Handwarmer", 1),
                new Perbekalan("Bodywarmer", 1),
                new Perbekalan("Air minum (liter per orang)", 3)
        );

        TableColumn<Perbekalan, String> colNamaPerbekalan = new TableColumn<>("Nama Perbekalan");
        colNamaPerbekalan.setCellValueFactory(c -> c.getValue().namaProperty());
        colNamaPerbekalan.setPrefWidth(250);

        TableColumn<Perbekalan, Number> colJumlah = new TableColumn<>("Jumlah");
        colJumlah.setCellValueFactory(c -> c.getValue().jumlahProperty());
        colJumlah.setPrefWidth(100);

        tablePerbekalan.getColumns().addAll(colNamaPerbekalan, colJumlah);
        tablePerbekalan.setItems(perbekalanList);
        tablePerbekalan.setPlaceholder(new Label("Belum ada perbekalan tambahan."));

        TextField txtPerbekalan = new TextField();
        txtPerbekalan.setPromptText("Nama Perbekalan");
        TextField txtJumlah = new TextField();
        txtJumlah.setPromptText("Jumlah");

        Button btnTambahPerbekalan = new Button("Tambah");
        btnTambahPerbekalan.setOnAction(e -> {
            if (!txtPerbekalan.getText().isEmpty() && !txtJumlah.getText().isEmpty()) {
                try {
                    int jumlah = Integer.parseInt(txtJumlah.getText());
                    perbekalanList.add(new Perbekalan(txtPerbekalan.getText(), jumlah));
                    txtPerbekalan.clear(); txtJumlah.clear();
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Jumlah harus berupa angka!").showAndWait();
                }
            }
        });

        HBox addPerbekalanBox = new HBox(10, txtPerbekalan, txtJumlah, btnTambahPerbekalan);
        addPerbekalanBox.setAlignment(Pos.CENTER);

        // === BUTTON & LABEL INFO ===
        Label lblInfo = new Label();
        lblInfo.getStyleClass().add("deskripsi");

        Button btnSimpan = new Button("Simpan dan Lihat Status ➜");
        Button btnKembali = new Button("⬅ Kembali");
        btnSimpan.getStyleClass().add("btn-utama");
        btnKembali.getStyleClass().add("btn-utama");

        btnSimpan.setOnAction(e -> {
            if (anggotaList.size() < 2) {
                lblInfo.setText("⚠️ Minimal harus ada 2 anggota pendaki!");
                return;
            }

            // 1. Simpan data booking ke file booking.txt (Data Detail User)
            try (FileWriter writer = new FileWriter("booking.txt")) {
                writer.write("=== DATA BOOKING PENDAKIAN ===\n");
                writer.write("Nama Ketua : " + namaKetua + "\n");
                writer.write("NIK Ketua  : " + nikKetua + "\n");
                writer.write("Tanggal    : " + tanggal + "\n");
                writer.write("Durasi     : " + durasi + " hari\n");
                writer.write("Jalur      : " + jalur + "\n");
                writer.write("Guide      : " + (pakaiGuide ? "Ya" : "Tidak") + "\n\n");

                writer.write("Jumlah Anggota : " + anggotaList.size() + "\n");
                writer.write("Daftar Anggota:\n");
                for (Anggota a : anggotaList) {
                    writer.write("- " + a.getNama() + " | NIK: " + a.getNik() + " | HP: " + a.getHp() + "\n");
                }

                writer.write("\nPerbekalan:\n");
                for (Perbekalan p : perbekalanList) {
                    writer.write("- " + p.getNama() + " : " + p.getJumlah() + "\n");
                }

                writer.write("\nStatus Pembayaran: Belum Dibayar\n"); // Status Awal User
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            // 2. TULIS DATA RINGKASAN KE data_pembayaran.txt (Data Admin)
            // Status awal di admin: Menunggu Pembayaran
            try (FileWriter writer = new FileWriter("data_pembayaran.txt", true); // true = append
                 BufferedWriter bw = new BufferedWriter(writer)) {
                
                // Format yang dibaca Admin: Nama Ketua;Jalur;Tanggal;Status Awal
                bw.write(namaKetua + ";" + jalur + ";" + tanggal + ";" + "Menunggu Pembayaran"); 
                bw.newLine();

            } catch (IOException ex) {
                System.err.println("Gagal menulis ke data_pembayaran.txt: " + ex.getMessage());
            }


            new HalamanStatusBookingFX().start(primaryStage);
        });

        btnKembali.setOnAction(e -> new HalamanBookingFX().start(primaryStage));

        VBox form = new VBox(15,
                lblTitle,
                lblAnggota, tableAnggota, addAnggotaBox,
                lblPerbekalan, tablePerbekalan, addPerbekalanBox,
                btnSimpan, btnKembali, lblInfo
        );
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setMaxWidth(700);
        form.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10;");

        StackPane contentWrapper = new StackPane(form);
        contentWrapper.setAlignment(Pos.CENTER);

        // Pastikan path image benar
        try {
            BackgroundImage bgImage = new BackgroundImage(
                new Image(getClass().getResource("images/background.png").toExternalForm(), 800, 600, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            BorderPane root = new BorderPane(contentWrapper);
            root.setBackground(new Background(bgImage));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("HalamanUtama.css").toExternalForm());
            primaryStage.setTitle("Booking Pendakian - Tahap 2");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading background image or CSS: " + e.getMessage());
            BorderPane root = new BorderPane(contentWrapper);
            Scene scene = new Scene(root, 900, 600);
            primaryStage.setTitle("Booking Pendakian - Tahap 2");
            primaryStage.setScene(scene);
            primaryStage.show();
        }


        // update otomatis jumlah perbekalan wajib saat jumlah pendaki berubah
        anggotaList.addListener((ListChangeListener<Anggota>) c ->
                perbaruiJumlahPerbekalan(anggotaList, perbekalanList, tablePerbekalan));
        perbaruiJumlahPerbekalan(anggotaList, perbekalanList, tablePerbekalan);
    }

    /** Fungsi untuk memperbarui jumlah perbekalan wajib otomatis */
    private void perbaruiJumlahPerbekalan(ObservableList<Anggota> anggotaList,
                                           ObservableList<Perbekalan> perbekalanList,
                                           TableView<Perbekalan> tablePerbekalan) {
        int totalPendaki = Math.max(1, anggotaList.size() + 1); // ketua + anggota
        for (Perbekalan p : perbekalanList) {
            if (p.getNama().toLowerCase().contains("air"))
                p.setJumlah(totalPendaki * 3); // 3L per orang
            else if (p.getNama().equalsIgnoreCase("Jaket") ||
                     p.getNama().equalsIgnoreCase("Handwarmer") ||
                     p.getNama().equalsIgnoreCase("Bodywarmer"))
                p.setJumlah(totalPendaki);
        }
        tablePerbekalan.refresh();
    }

    // === MODEL ANGGOTA ===
    public static class Anggota {
        private final javafx.beans.property.SimpleStringProperty nama;
        private final javafx.beans.property.SimpleStringProperty nik;
        private final javafx.beans.property.SimpleStringProperty hp;

        public Anggota(String nama, String nik, String hp) {
            this.nama = new javafx.beans.property.SimpleStringProperty(nama);
            this.nik = new javafx.beans.property.SimpleStringProperty(nik);
            this.hp = new javafx.beans.property.SimpleStringProperty(hp);
        }

        public String getNama() { return nama.get(); }
        public String getNik() { return nik.get(); }
        public String getHp() { return hp.get(); }

        public javafx.beans.property.StringProperty namaProperty() { return nama; }
        public javafx.beans.property.StringProperty nikProperty() { return nik; }
        public javafx.beans.property.StringProperty hpProperty() { return hp; }
    }

    // === MODEL PERBEKALAN ===
    public static class Perbekalan {
        private final javafx.beans.property.SimpleStringProperty nama;
        private final javafx.beans.property.SimpleIntegerProperty jumlah;

        public Perbekalan(String nama, int jumlah) {
            this.nama = new javafx.beans.property.SimpleStringProperty(nama);
            this.jumlah = new javafx.beans.property.SimpleIntegerProperty(jumlah);
        }

        public String getNama() { return nama.get(); }
        public int getJumlah() { return jumlah.get(); }

        public void setJumlah(int value) { this.jumlah.set(value); }

        public javafx.beans.property.StringProperty namaProperty() { return nama; }
        public javafx.beans.property.IntegerProperty jumlahProperty() { return jumlah; }
    }
}