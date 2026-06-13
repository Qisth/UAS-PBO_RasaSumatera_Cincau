package com.example.rasasumaterabackend.config;

import com.example.rasasumaterabackend.model.Daerah;
import com.example.rasasumaterabackend.model.Kuliner;
import com.example.rasasumaterabackend.repository.DaerahRepository;
import com.example.rasasumaterabackend.repository.KulinerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DaerahRepository daerahRepository;
    private final KulinerRepository kulinerRepository;

    // Dependency Injection via Constructor
    public DataInitializer(DaerahRepository daerahRepository, KulinerRepository kulinerRepository) {
        this.daerahRepository = daerahRepository;
        this.kulinerRepository = kulinerRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Validasi pengaman: Data hanya akan dimasukkan jika tabel Daerah masih kosong
        if (daerahRepository.count() > 0) {
            System.out.println(">>> Data dummy Daerah dan Kuliner sudah ada di database. Lewati inisialisasi.");
            return;
        }

        System.out.println(">>> Memulai proses inisialisasi data dummy RasaSumatera...");

        // ==========================================
        // 1. MEMBUAT DATA DUMMY TABEL DAERAH
        // ==========================================
        Daerah sumut = new Daerah();
        sumut.setNama("Sumut");

        Daerah sumsel = new Daerah();
        sumsel.setNama("Sumsel");

        Daerah sumbar = new Daerah();
        sumbar.setNama("Sumbar");

        // Simpan daerah ke database terlebih dahulu agar mendapatkan ID dari H2
        sumut = daerahRepository.save(sumut);
        sumsel = daerahRepository.save(sumsel);
        sumbar = daerahRepository.save(sumbar);

        // ==========================================
        // 2. MEMBUAT DATA DUMMY TABEL KULINER
        // ==========================================

        // Kuliner untuk Daerah: Sumut
        Kuliner bikaAmbon = new Kuliner();
        bikaAmbon.setNama("Bika Ambon");
        bikaAmbon.setDeskripsi("Kue basah tradisional bertekstur rongga semut dengan rasa pandan-lemak yang legit.");
        bikaAmbon.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Kue_bika_ambon.JPG/250px-Kue_bika_ambon.JPG"); // Contoh URL Gambar bebas
        bikaAmbon.setDaerah(sumut); // Mengikat objek Daerah Sumut (Prinsip Relasi OOP/JPA)
        kulinerRepository.save(bikaAmbon);

        Kuliner sotoSumut = new Kuliner();
        sotoSumut.setNama("Soto Sumut");
        sotoSumut.setDeskripsi("Soto berkuah santan kental berwarna kuning jernih dengan cita rasa rempah jinten yang khas.");
        sotoSumut.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Soto_sumut.jpg/330px-Soto_sumut.jpg");
        sotoSumut.setDaerah(sumut);
        kulinerRepository.save(sotoSumut);

        // Kuliner untuk Daerah: Sumsel
        Kuliner pempek = new Kuliner();
        pempek.setNama("Pempek Sumsel");
        pempek.setDeskripsi("Makanan olahan daging ikan dikombinasikan tepung sagu, disajikan bersama kuah cuko asam pedas manis.");
        pempek.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Pempek_Kuah_Cuko.jpg/250px-Pempek_Kuah_Cuko.jpg");
        pempek.setDaerah(sumsel);
        kulinerRepository.save(pempek);

        Kuliner tekwan = new Kuliner();
        tekwan.setNama("Tekwan");
        tekwan.setDeskripsi("Sup bakso ikan khas Sumsel berkuah kaldu udang gurih, ditaburi jamur kuping dan soun.");
        tekwan.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Tekwan.JPG/250px-Tekwan.JPG");
        tekwan.setDaerah(sumsel);
        kulinerRepository.save(tekwan);

        // Kuliner untuk Daerah: Sumbar
        Kuliner rendang = new Kuliner();
        rendang.setNama("Rendang Daging");
        rendang.setDeskripsi("Hidangan daging sapi kaya rempah yang dimasak perlahan dalam santan hingga kering berwarna cokelat gelap.");
        rendang.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/Rendang_daging_sapi_asli_Sumbar.JPG/250px-Rendang_daging_sapi_asli_Sumbar.JPG");
        rendang.setDaerah(sumbar);
        kulinerRepository.save(rendang);

        System.out.println(">>> Pengisian data dummy Daerah dan Kuliner berhasil diselesaikan!");
    }
}