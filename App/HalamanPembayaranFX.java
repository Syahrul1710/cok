package App;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HalamanPembayaranFX {

    public void start(Stage primaryStage) {
        // --- 1. Hitung Total Biaya ---
        double totalBiaya = hitungTotalBiaya();

        Label lblTitle = new Label("Halaman Pembayaran");
        lblTitle.getStyleClass().add("judul");

        TextArea txtDetail = new TextArea();
        txtDetail.setEditable(false);
        txtDetail.setText(getDetailBooking() + "\n\nTotal Biaya yang Harus Dibayar: Rp " + String.format("%,.0f", totalBiaya));
        txtDetail.setPrefHeight(250);

        // Update tarif biaya di instruksi
        Label lblInstruksi = new Label("Silakan lakukan transfer ke Rekening BNI 123456789 (a/n Merbabu Trekking).\n" +
                                        "Biaya dihitung berdasarkan: Tiket (Rp 35.000/orang/hari) + Guide (Rp 100.000/hari).");
        
        Button btnKonfirmasiBayar = new Button("✅ Konfirmasi Pembayaran Selesai");
        btnKonfirmasiBayar.getStyleClass().add("btn-utama");
        
        Button btnBatal = new Button("⬅ Kembali ke Status");

        // --- Aksi Konfirmasi Pembayaran ---
        btnKonfirmasiBayar.setOnAction(e -> {
            // Ubah Status di booking.txt menjadi "Menunggu Konfirmasi Admin"
            updateStatusBookingFile("Menunggu Konfirmasi Admin");
            
            new Alert(Alert.AlertType.INFORMATION, 
                "✅ Konfirmasi pembayaran diterima! Status booking akan diperbarui setelah dikonfirmasi oleh Admin.").showAndWait();
            
            // Kembali ke halaman status
            new HalamanStatusBookingFX().start(primaryStage);
        });
        
        btnBatal.setOnAction(e -> new HalamanStatusBookingFX().start(primaryStage));

        VBox root = new VBox(15, lblTitle, txtDetail, lblInstruksi, btnKonfirmasiBayar, btnBatal);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30;");

        Scene scene = new Scene(root, 600, 600);
        try {
            scene.getStylesheets().add(getClass().getResource("HalamanUtama.css").toExternalForm());
        } catch (Exception ex) {
            System.err.println("Warning: CSS file 'HalamanUtama.css' not found.");
        }
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pembayaran");
        primaryStage.show();
    }
    
    /** Membaca data booking.txt dan menghitung total biaya */
    private double hitungTotalBiaya() {
        // Tarif Baru
        double biayaTiketPerHari = 35000; 
        double biayaGuidePerHari = 100000; 
        int totalAnggota = 0;
        int durasi = 0;
        boolean pakaiGuide = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader("booking.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Jumlah Anggota")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                         try {
                            // Total pendaki = Anggota yang terdaftar + 1 (Ketua)
                            totalAnggota = Integer.parseInt(parts[1].trim()) + 1;
                        } catch (NumberFormatException ignored) {}
                    }
                } else if (line.startsWith("Durasi")) {
                    String[] parts = line.split(":");
                    if (parts.length > 1) {
                        try {
                            durasi = Integer.parseInt(parts[1].trim().split(" ")[0]);
                        } catch (NumberFormatException ignored) {}
                    }
                } else if (line.startsWith("Guide")) {
                    pakaiGuide = line.split(":")[1].trim().equalsIgnoreCase("Ya");
                }
            }
        } catch (IOException e) {
            return 0; 
        }

        double totalTiket = totalAnggota * biayaTiketPerHari * durasi;
        double totalGuide = pakaiGuide ? (biayaGuidePerHari * durasi) : 0;
        
        return totalTiket + totalGuide;
    }
    
    /** Membaca detail booking untuk ditampilkan */
    private String getDetailBooking() {
        try (BufferedReader reader = new BufferedReader(new FileReader("booking.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Jangan tampilkan Status Pembayaran yang lama
                if (!line.startsWith("Status Pembayaran:")) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString().trim();
        } catch (IOException e) {
            return "Detail booking tidak ditemukan.";
        }
    }

    /** Mengubah status pembayaran di booking.txt (file yang dibaca user) */
    private void updateStatusBookingFile(String statusBaru) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("booking.txt");
            List<String> lines = java.nio.file.Files.readAllLines(path);
            List<String> newLines = new ArrayList<>();
            boolean statusLineFound = false;

            for (String line : lines) {
                if (line.startsWith("Status Pembayaran:")) {
                    newLines.add("Status Pembayaran: " + statusBaru);
                    statusLineFound = true;
                } else {
                    newLines.add(line);
                }
            }
            
            if (!statusLineFound) {
                 newLines.add("\nStatus Pembayaran: " + statusBaru);
            }

            java.nio.file.Files.write(path, newLines);
        } catch (IOException e) {
            System.err.println("Gagal mengupdate booking.txt: " + e.getMessage());
        }
    }
}