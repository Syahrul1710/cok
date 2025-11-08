package App;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files; 
import java.nio.file.Paths; 
import java.util.ArrayList;
import java.util.List;

public class AndminKonfirmasi {

    private TableView<Pembayaran> table;
    private ObservableList<Pembayaran> dataPembayaran;

    public void start(Stage primaryStage) {
        Label lblTitle = new Label("Panel Admin - Konfirmasi Pembayaran Pendakian");
        lblTitle.getStyleClass().add("judul-halaman");

        table = new TableView<>();
        dataPembayaran = FXCollections.observableArrayList();
        loadDataPembayaran();

        TableColumn<Pembayaran, String> colNama = new TableColumn<>("Nama Ketua Pendaki");
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colNama.setPrefWidth(150);

        TableColumn<Pembayaran, String> colGunung = new TableColumn<>("Gunung");
        colGunung.setCellValueFactory(cellData -> cellData.getValue().gunungProperty());
        colGunung.setPrefWidth(120);

        TableColumn<Pembayaran, String> colTanggal = new TableColumn<>("Tanggal Pendakian");
        colTanggal.setCellValueFactory(cellData -> cellData.getValue().tanggalProperty());
        colTanggal.setPrefWidth(150);

        TableColumn<Pembayaran, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colStatus.setPrefWidth(150);

        table.getColumns().addAll(colNama, colGunung, colTanggal, colStatus);
        table.setItems(dataPembayaran);
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); 

        Button btnSetuju = new Button("✅ Setujui Pembayaran");
        Button btnTolak = new Button("❌ Tolak Pembayaran");
        Button btnKembali = new Button("⬅ Kembali ke Login");

        btnSetuju.getStyleClass().add("menu-button");
        btnTolak.getStyleClass().add("menu-button");
        btnKembali.getStyleClass().add("menu-button");

        // Aksi Konfirmasi: Memanggil fungsi ubahStatus
        btnSetuju.setOnAction(e -> ubahStatus("disetujui"));
        btnTolak.setOnAction(e -> ubahStatus("ditolak"));

        // Aksi Kembali: Kembali ke Halaman Login
        btnKembali.setOnAction(e -> new HalamanLoginFX().start(primaryStage));

        HBox tombolBox = new HBox(10, btnSetuju, btnTolak, btnKembali);
        tombolBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, lblTitle, table, tombolBox);
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("main-container");
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 750, 500);
        
        try {
            scene.getStylesheets().add(getClass().getResource("HalamanUtama.css").toExternalForm());
        } catch (Exception ex) {
            System.err.println("Warning: CSS file 'HalamanUtama.css' not found or failed to load.");
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Konfirmasi Pembayaran");
    }

    /** Membaca data pembayaran dari file data_pembayaran.txt */
    private void loadDataPembayaran() {
        dataPembayaran.clear();
        File file = new File("data_pembayaran.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                // Format: Nama;Gunung;Tanggal;Status
                if (parts.length >= 4) {
                    dataPembayaran.add(new Pembayaran(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Mengubah status pembayaran di file admin dan file user */
    private void ubahStatus(String statusBaru) {
        Pembayaran selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Pilih dulu nama pendaki yang ingin dikonfirmasi.").showAndWait();
            return;
        }
        
        // VALIDASI BARU: Jika status di admin masih 'Menunggu Pembayaran', Admin tidak boleh menyetujui
        if (statusBaru.equalsIgnoreCase("disetujui") && 
            selected.getStatus().equalsIgnoreCase("Menunggu Pembayaran")) {
             new Alert(Alert.AlertType.WARNING, 
                "⚠️ Tidak dapat menyetujui. Status saat ini 'Menunggu Pembayaran'. \nPendaki belum mengkonfirmasi transfer.").showAndWait();
            return;
        }

        // 1. Ubah status di file Admin (data_pembayaran.txt)
        selected.setStatus(statusBaru);
        simpanKeFile();
        table.refresh();

        // 2. Update status di file User (booking.txt) - PENTING UNTUK SINKRONISASI
        updateUserStatus(selected.getNama(), statusBaru); 

        new Alert(Alert.AlertType.INFORMATION,
                "✅ Pembayaran atas nama " + selected.getNama() + " telah diubah status menjadi " + statusBaru + ".").showAndWait();
    }

    /** Menulis ulang semua data ke file data_pembayaran.txt */
    private void simpanKeFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data_pembayaran.txt"))) {
            for (Pembayaran p : dataPembayaran) {
                // Format: Nama;Gunung;Tanggal;Status
                writer.write(p.getNama() + ";" + p.getGunung() + ";" + p.getTanggal() + ";" + p.getStatus());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Memperbarui status pembayaran di booking.txt (file yang dibaca user) */
    private void updateUserStatus(String namaKetua, String statusBaru) {
        // Tentukan status yang ditampilkan ke user
        String statusUser;
        if (statusBaru.equalsIgnoreCase("disetujui")) {
            statusUser = "Sudah Dibayar (Disetujui Admin)";
        } else if (statusBaru.equalsIgnoreCase("ditolak")) {
            statusUser = "Pembayaran Ditolak Admin";
        } else if (statusBaru.equalsIgnoreCase("menunggu konfirmasi")) {
            statusUser = "Menunggu Konfirmasi Admin";
        } else {
             statusUser = "Belum Dibayar"; // Menunggu Pembayaran
        }

        try {
            java.nio.file.Path path = java.nio.file.Paths.get("booking.txt");
            if (!Files.exists(path)) return; // Jika file user tidak ada

            // Logika untuk memastikan hanya mengupdate booking.txt yang sesuai (jika ada file lain)
            List<String> lines = Files.readAllLines(path);
            String namaKetuaDiBooking = "";
            for (String line : lines) {
                 if (line.startsWith("Nama Ketua")) { 
                    namaKetuaDiBooking = line.split(":")[1].trim();
                    break;
                }
            }

            // Hanya update jika booking.txt yang ada adalah milik ketua yang dikonfirmasi
            if (namaKetuaDiBooking.equalsIgnoreCase(namaKetua)) {
                List<String> newLines = new ArrayList<>();
                boolean statusLineFound = false;

                for (String line : lines) {
                    if (line.startsWith("Status Pembayaran:")) {
                        newLines.add("Status Pembayaran: " + statusUser);
                        statusLineFound = true;
                    } else {
                        newLines.add(line);
                    }
                }
                
                if (!statusLineFound) {
                    newLines.add("\nStatus Pembayaran: " + statusUser);
                }

                Files.write(Paths.get("booking.txt"), newLines);
            }

        } catch (IOException e) {
            System.err.println("Gagal mengupdate booking.txt: " + e.getMessage());
        }
    }
}