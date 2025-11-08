package App;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TiketGenerator {
    /**
     * Menghasilkan PDF tiket dan menampilkan dialog FileChooser.
     * @param stage Stage utama aplikasi untuk dialog.
     */
    public static void generatePDF(Stage stage) {
        // 1. Panggil logika utama pembuatan PDF.
        // File ini akan membuat file sementara bernama "Tiket_Pendakian.pdf"
        GenerateTiketPendakian.buatTiketPDF();

        File sourceFile = new File("Tiket_Pendakian.pdf");

        // 2. Tampilkan FileChooser kepada user untuk memilih lokasi simpan
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Tiket Pendakian");
        fileChooser.setInitialFileName(sourceFile.getName());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));

        File destFile = fileChooser.showSaveDialog(stage);

        // 3. Pindahkan file jika user memilih lokasi
        if (destFile != null && sourceFile.exists()) {
            try {
                // Pindahkan file ke lokasi yang dipilih user
                Files.move(sourceFile.toPath(), destFile.toPath(),
                           StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (sourceFile.exists()) {
             // Jika user membatalkan dialog, hapus file sementara
             sourceFile.delete();
        }
    }
}