# UAS-PBO_RasaSumatera_Cincau
## Deskripsi
Aplikasi RasaSumatera adalah aplikasi yang berisi kuliner dari pulau Sumatra. Aplikasi ini dibuat untuk Ujian Akhir Semester Praktikum Pemrograman Berbasis Objek, dan diperuntukkan bagi masyarakat dari luar Sumatra ataupun turis yang berlibur ke Sumatra. Aplikasi ini menggunakan JavaFX untuk frontend dan Spring Boot untuk backend, serta H2 untuk database-nya.

## Fitur Utama
### Pencarian Kuliner (Tidak Perlu Login)
Pengguna bisa mencari kuliner yang diinginkan di bagian list kuliner. Pencarian bisa dilakukan dengan memanfaatkan search bar, pemilihan daerah melalui selection bar ataupun keduanya. Dari sini, pengguna dapat melihat detail dari kuliner, yang meliputi nama, daerah asal, deskripsi, dan gambar.

### Pembuatan Ulasan Kuliner (Perlu Login)
Pengguna bisa membuat ulasan terhadap suatu kuliner dengan cara mengisi kolom ulasan di bawah deskripsi kuliner. Lalu, pengguna dapat memilih berapa bintang yang akan diberikan. Saat pengguna mengirim ulasan, ulasan tersebut bisa langsung dilihat di bawah kolom ulasan.

### Manajemen Kuliner (Khusus Admin)
Khusus bagi admin disediakan halaman untuk memasukkan dan menghapus kuliner. Di sini admin bisa menentukan nama, daerah asal, deskripsi, dan gambar dari kuliner yang akan dimasukkan. Admin juga bisa menghapus kuliner apabila diinginkan.

## Cara Menjalankan
1. Pertama clone dulu repositori ini:
   
   ```
   git clone https://github.com/Qisth/UAS-PBO_RasaSumatera_Cincau
   ```
2. Buka IntelliJ IDEA (kalau belum ada, silakan download dari link ini: https://www.jetbrains.com/idea/download/), lalu buka folder frontend dan backend sebagai proyek terpisah.
3. Khusus untuk proyek frontend, klik menu File di sebelah kiri atas jendela IntelliJ, lalu klik Project Structure. Nanti akan muncul jendela baru untuk struktur proyek.
4. Di bagian Libraries tekan tombol tambah (+), pilih Java, lalu pilih folder lib milik SDK JavaFX (bisa didownload di sini: https://gluonhq.com/products/javafx/#downloads). Proyek akan menggunakan JavaFX untuk menjalankan komponen-komponen frontend.
5. Di bagian tengah atas jendela editor, tekan teks "Current File", lalu klik Edit Configurations. Nanti akan muncul jendela baru untuk konfigurasi.
6. Tekan tombol tambah (+) lagi, lalu tekan Application untuk membuat aplikasi baru. Nama aplikasi ini buat saja "Main", fungsinya untuk menjalankan program dari sisi frontend nanti.
7. Klik bagian Modify options dan tekan "Add VM options". Di bagian "VM options" isi `--module-path "(isi dengan lokasi folder lib di SDK JavaFX)" --add-modules javafx.controls,javafx.fxml`. Lalu di bagian "Main class" isi `com.example.Main`
8. Tekan Apply, lalu tekan OK. Untuk menjalankan proyeknya jalankan dulu bagian backend dengan cara menekan tombol segitiga hijau di tengah atas, lalu bagian frontend dengan cara yang sama.
9. Aplikasi sudah bisa dijalankan :)

## Link Video Presentasi: https://youtu.be/IlZKmSmTgoM?si=1sYY4mfordyxtpaC
