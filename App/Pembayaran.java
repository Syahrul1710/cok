package App;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pembayaran {

    private final StringProperty nama;
    private final StringProperty gunung;
    private final StringProperty tanggal;
    private final StringProperty status;

    public Pembayaran(String nama, String gunung, String tanggal, String status) {
        this.nama = new SimpleStringProperty(nama);
        this.gunung = new SimpleStringProperty(gunung);
        this.tanggal = new SimpleStringProperty(tanggal);
        this.status = new SimpleStringProperty(status);
    }

    public String getNama() {
        return nama.get();
    }

    public StringProperty namaProperty() {
        return nama;
    }

    public StringProperty gunungProperty() { 
        return gunung;
    }

    public StringProperty tanggalProperty() { 
        return tanggal;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String newStatus) {
        this.status.set(newStatus);
    }
    
    public String getGunung() { return gunung.get(); }
    public String getTanggal() { return tanggal.get(); }
}