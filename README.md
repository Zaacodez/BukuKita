# BukuKita 📚

Aplikasi katalog buku berbasis Android yang dirancang untuk **Toko Buku Semut Alas**. Aplikasi ini mengintegrasikan kecerdasan buatan berupa asisten pintar (*chatbot*) untuk memberikan layanan informasi buku yang interaktif, tanpa terbatas jam operasi toko fisik.

Proyek ini dikembangkan sebagai bagian dari Tugas Akhir / Skripsi, dengan fokus pengembangan akhir pada AI chatbot pelayanan dan arsitektur aliran data yang aman dan responsif.

---

## 🚀 Deskripsi Aplikasi

**BukuKita** adalah solusi digital untuk memperluas jangkauan Toko Buku Semut Alas. Dengan menggabungkan basis data modern dan *Large Language Model* (LLM), aplikasi ini berfungsi sebagai katalog buku digital sekaligus asisten informasi bagi pengguna.

Melalui *chatbot* yang terintegrasi, pengguna dapat berdiskusi secara natural untuk mencari rekomendasi buku serta menanyakan informasi buku berdasarkan data katalog yang tersimpan, seperti judul, penulis, kategori, dan harga yang tercatat di database.

> Fokus versi akhir aplikasi ini adalah katalog buku dan AI chatbot pelayanan. Versi ini tidak mencakup fitur transaksi pembelian di dalam aplikasi.

---

## ✨ Fitur Utama

*   **Otentikasi & Manajemen Sesi**: Registrasi akun (termasuk foto profil) dan sistem *login/logout* yang terintegrasi dengan Supabase Auth.
*   **Katalog Buku Dinamis**: Tampilan beranda yang menyajikan katalog buku terstruktur, lengkap dengan gambar sampul, penulis, dan harga berdasarkan data yang tersimpan di database.
*   **Asisten Pintar BukuKita (Chatbot AI)**:
    *   **Intent & Entity Recognition**: Memahami maksud pengguna, misalnya pencarian berdasarkan penulis, judul, maupun kategori buku.
    *   **Natural Language Processing (NLP)**: Membantu memahami variasi bahasa informal, singkatan, dan *typo* dalam pertanyaan pengguna.
    *   **Data Grounding**: Menggunakan data katalog buku dari Supabase sebagai konteks untuk membantu meningkatkan relevansi dan akurasi jawaban chatbot terkait informasi produk, harga, dan ketersediaan stok. Mekanisme ini membantu, bukan menjamin jawaban selalu akurat — sistem AI generatif tetap bisa salah memahami data atau memberi jawaban yang keliru, sehingga informasi penting tetap perlu dicek pada data katalog.
*   **Manajemen Profil Pengguna**: Pengguna dapat memperbarui informasi akun, seperti *display name* dan *username*.

---

## 📸 Screenshot

<!-- Ganti file placeholder di bawah dengan screenshot asli dari aplikasi. Simpan gambar di folder screenshots/ di repo ini. -->

| Katalog buku | Detail buku |
|---|---|
| ![Katalog buku](screenshots/katalog.png) | ![Detail buku](screenshots/detail-buku.png) |

| Chatbot AI | Profil pengguna |
|---|---|
| ![Chatbot AI](screenshots/chatbot.png) | ![Profil pengguna](screenshots/profil.png) |

---

## 🛠️ Arsitektur & Teknologi

Aplikasi ini dibangun menggunakan ekosistem teknologi berikut:

*   **Mobile**: Kotlin dan Jetpack Compose (Material 3) untuk aplikasi Android native.
*   **Core Engine (AI)**: Gemini API (Google AI) untuk pemrosesan bahasa alami (*Natural Language Processing*) pada chatbot.
*   **Backend & Database**: Supabase (PostgreSQL, Auth, & Storage) sebagai pusat penyimpanan data katalog dan akun pengguna.
*   **Data Source**: Open Library API untuk mengambil data katalog buku melalui API.
*   **Pendukung**: Retrofit untuk komunikasi API, Coil untuk memuat gambar sampul, dan Room untuk penyimpanan lokal.

---

## ▶️ Menjalankan Proyek

1. Clone repo ini, lalu buka di Android Studio.
2. Siapkan proyek Supabase dan akses Gemini API milikmu sendiri.
3. Masukkan konfigurasi Supabase dan Gemini API ke konfigurasi lokal proyek. Jangan pernah meng-commit API key atau kredensial ke repo publik.
4. Jalankan aplikasi di emulator atau perangkat Android (minSdk 28).

---
