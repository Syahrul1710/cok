package App;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
// Hapus 'import java.util.*' dan ganti dengan import spesifik untuk menghindari ambiguitas List
import java.util.ArrayList;
import java.util.List;

public class GenerateTiketPendakian {

    public static void buatTiketPDF() {
        try {
            // Baca data dari booking.txt
            // FIX: Menggunakan java.util.List secara eksplisit
            List<String> lines = Files.readAllLines(Paths.get("booking.txt"));

            String namaKetua = "";
            String nikKetua = "";
            String tanggal = "";
            String jalur = "";
            String durasi = "";
            
            // FIX: Menggunakan java.util.List dan java.util.ArrayList secara eksplisit
            List<String> anggota = new ArrayList<>();
            List<String> perbekalan = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith("Nama Ketua")) namaKetua = line.split(":")[1].trim();
                else if (line.startsWith("NIK Ketua")) nikKetua = line.split(":")[1].trim();
                else if (line.startsWith("Tanggal")) tanggal = line.split(":")[1].trim();
                else if (line.startsWith("Durasi")) durasi = line.split(":")[1].trim();
                else if (line.startsWith("Jalur")) jalur = line.split(":")[1].trim();
                else if (line.startsWith("- ") && line.contains("| NIK")) anggota.add(line.substring(2));
                else if (line.startsWith("- ") && !line.contains("|")) perbekalan.add(line.substring(2));
            }

            // Inisialisasi Dokumen PDF
            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(doc, new FileOutputStream("Tiket_Pendakian.pdf"));
            doc.open();

            // Menggunakan fully qualified name untuk Font dan konstanta
            Paragraph title = new Paragraph("TIKET PENDAKIAN GUNUNG MERBABU\n\n",
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            // Detail Booking
            doc.add(new Paragraph("Tanggal Booking : " + tanggal));
            doc.add(new Paragraph("Jalur Pendakian : " + jalur));
            doc.add(new Paragraph("Durasi : " + durasi + "\n\n"));

            // Ketua Kelompok
            doc.add(new Paragraph("Ketua Kelompok:"));
            doc.add(new Paragraph("Nama: " + namaKetua));
            doc.add(new Paragraph("NIK: " + nikKetua + "\n"));

            // Daftar Anggota
            doc.add(new Paragraph("Daftar Anggota:"));
            for (String a : anggota) doc.add(new Paragraph(" - " + a));

            // Perbekalan
            doc.add(new Paragraph("\nPerbekalan:"));
            for (String p : perbekalan) doc.add(new Paragraph(" - " + p));

            // Menggunakan fully qualified name untuk Font dan konstanta
            doc.add(new Paragraph("\n\nPERATURAN PENDAKIAN:", 
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD)));
            doc.add(new Paragraph("""
1. Dilarang membawa minuman keras, narkoba, atau senjata tajam.
2. Wajib menjaga kebersihan dan tidak membuang sampah sembarangan.
3. Menjaga kelestarian flora dan fauna.
4. Bertanggung jawab atas keselamatan diri sendiri.
"""));

            doc.add(new Paragraph("\n\nTanda Tangan Ketua Kelompok:\n\n\n"));
            
            // Menggunakan fully qualified name untuk Image dan metodenya
            try {
                com.itextpdf.text.Image materai = com.itextpdf.text.Image.getInstance("App/images/materai.png");
                materai.scaleAbsolute(100, 60);
                doc.add(materai);
            } catch (Exception ex) {
                doc.add(new Paragraph("[Materai tidak ditemukan]"));
            }
            
            doc.add(new Paragraph("_____________________________"));
            doc.add(new Paragraph(namaKetua));

            doc.close();
            System.out.println("✅ PDF Tiket berhasil dibuat!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}