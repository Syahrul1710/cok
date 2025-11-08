package App;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HalamanStatusBookingFX {

    public void start(Stage primaryStage) {
        Label lblTitle = new Label("Status Booking Pendakian");
        lblTitle.getStyleClass().add("judul"); 

        TextArea txtStatus = new TextArea();
        txtStatus.setEditable(false);
        txtStatus.getStyleClass().add("area-status");

        // --- Mendapatkan Status dan Nama Ketua ---
        String namaKetua = getNamaKetua(); 
        // Dapatkan status dari file admin, lalu update status di file booking user
        String currentStatus = syncStatus(namaKetua); 

        // Membaca file booking dan menampilkan status terbaru
        try (BufferedReader reader = new BufferedReader(new FileReader("booking.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            txtStatus.setText(sb.toString());
        } catch (IOException e) {
            txtStatus.setText("Belum ada data booking yang tersimpan.");
        }

        Button btnBayar = new Button("Lanjut ke Pembayaran ➜");
        Button btnUnduh = new Button("Unduh Tiket");
        Button btnKembali = new Button("⬅ Kembali ke Menu");

        btnBayar.getStyleClass().add("btn-utama");
        btnUnduh.getStyleClass().add("btn-utama");
        btnKembali.getStyleClass().add("btn-kembali");

        // Kontrol tombol berdasarkan status dari file user
        boolean isApproved = currentStatus.toLowerCase().contains("disetujui");
        boolean isAwaitingPayment = currentStatus.toLowerCase().contains("belum dibayar");
        
        // Aturan:
        // 1. Tombol Unduh Tiket: HANYA aktif jika status DISUTUJUI ADMIN.
        btnUnduh.setDisable(!isApproved); 
        
        // 2. Tombol Bayar: HANYA aktif jika statusnya BELUM DIBAYAR.
        btnBayar.setDisable(!isAwaitingPayment);

        // Tombol pembayaran
        btnBayar.setOnAction(e -> {
            if (isAwaitingPayment) {
                 new HalamanPembayaranFX().start(primaryStage); // Panggil halaman pembayaran yang benar
            } else {
                 new Alert(Alert.AlertType.WARNING, "Pembayaran sudah dikonfirmasi atau menunggu konfirmasi Admin.").showAndWait();
            }
        });

        // Aksi Unduh Tiket
        btnUnduh.setOnAction(e -> {
            if (isApproved) {
                GenerateTiketPendakian.buatTiketPDF(); 
                new Alert(Alert.AlertType.INFORMATION, "✅ Tiket berhasil diunduh dan disimpan sebagai PDF (Tiket_Pendakian.pdf).").showAndWait();
            } else {
                new Alert(Alert.AlertType.WARNING,
                        "❌ Tiket belum bisa diunduh.\nStatus pembayaran saat ini: " + currentStatus + ".").showAndWait();
            }
        });

        btnKembali.setOnAction(e -> new HalamanUtamaFX().start(primaryStage));

        VBox root = new VBox(10, lblTitle, txtStatus, btnBayar, btnUnduh, btnKembali);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 500, 500);
        try {
            scene.getStylesheets().add(getClass().getResource("HalamanUtama.css").toExternalForm());
        } catch (Exception ex) {
            System.err.println("Warning: CSS file 'HalamanUtama.css' not found.");
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("Status Booking");
        primaryStage.show();
    }
    
    /** Mengambil Nama Ketua dari booking.txt */
    private String getNamaKetua() {
        try (BufferedReader reader = new BufferedReader(new FileReader("booking.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Nama Ketua")) { 
                    return line.split(":")[1].trim();
                }
            }
        } catch (IOException e) {
            // File booking.txt tidak ada
        }
        return null;
    }

    /** Memeriksa status di data_pembayaran.txt dan mengupdate status di booking.txt */
    private String syncStatus(String namaKetua) {
        if (namaKetua == null) return "Belum Dibayar";
        
        String currentStatusInAdminFile = "Belum Dibayar"; // Status default jika file admin kosong atau tidak ada

        // 1. Cek status di file Admin (data_pembayaran.txt)
        File fileAdmin = new File("data_pembayaran.txt");
        if (fileAdmin.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileAdmin))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";"); 
                    if (parts.length >= 4 && parts[0].trim().equalsIgnoreCase(namaKetua)) {
                        String statusAdmin = parts[3].trim().toLowerCase();
                        if (statusAdmin.equals("disetujui")) currentStatusInAdminFile = "Sudah Dibayar (Disetujui Admin)";
                        else if (statusAdmin.equals("ditolak")) currentStatusInAdminFile = "Pembayaran Ditolak Admin";
                        else if (statusAdmin.equals("menunggu konfirmasi")) currentStatusInAdminFile = "Menunggu Konfirmasi Admin";
                        else if (statusAdmin.equals("menunggu pembayaran")) currentStatusInAdminFile = "Belum Dibayar";
                        
                        break; // Data user ditemukan
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // 2. Sinkronkan status kembali ke booking.txt (file yang dibaca user)
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("booking.txt");
            if (!java.nio.file.Files.exists(path)) return currentStatusInAdminFile; // Tidak ada file booking

            List<String> lines = java.nio.file.Files.readAllLines(path);
            List<String> newLines = new ArrayList<>();
            boolean statusLineFound = false;

            for (String line : lines) {
                if (line.startsWith("Status Pembayaran:")) {
                    newLines.add("Status Pembayaran: " + currentStatusInAdminFile);
                    statusLineFound = true;
                } else {
                    newLines.add(line);
                }
            }
            
            if (!statusLineFound) {
                 newLines.add("\nStatus Pembayaran: " + currentStatusInAdminFile);
            }

            java.nio.file.Files.write(path, newLines);
        } catch (IOException e) {
            System.err.println("Gagal mengupdate booking.txt: " + e.getMessage());
        }

        return currentStatusInAdminFile;
    }
}