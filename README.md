# BukuKita 📚

Aplikasi *e-commerce* buku berbasis *mobile* yang dirancang khusus untuk **Toko Buku Semut Alas**. Aplikasi ini mengintegrasikan kecerdasan buatan berupa asisten pintar (*chatbot*) untuk memberikan layanan informasi produk yang interaktif dan *real-time* tanpa batasan waktu operasi toko fisik.

Proyek ini dikembangkan sebagai bagian dari Tugas Akhir / Skripsi dengan fokus penyesuaian arsitektur aliran data yang aman dan responsif.

---

## 🚀 Deskripsi Aplikasi

**BukuKita** adalah solusi digital untuk memperluas jangkauan pasar Toko Buku Semut Alas. Dengan menggabungkan teknologi basis data modern dan *Large Language Model* (LLM), aplikasi ini tidak hanya berfungsi sebagai katalog buku digital biasa, melainkan juga sebagai asisten belanja pribadi pengguna. 

Melalui fitur *chatbot* yang terintegrasi, pengguna dapat berdiskusi secara natural untuk mencari rekomendasi buku, memeriksa ketersediaan stok aktual, hingga menanyakan detail harga secara presisi tanpa risiko halusinasi informasi data.

---

## ✨ Fitur Utama

*   **Otentikasi & Manajemen Sesi Aman**: Registrasi akun baru secara lengkap (termasuk foto profil) dan sistem *login/logout* yang terintegrasi langsung dengan Supabase Auth.
*   **Katalog Buku Dinamis**: Tampilan beranda modern yang menyajikan katalog produk terstruktur, lengkap dengan gambar sampul, penulis, dan harga hasil sinkronisasi data.
*   **Asisten Pintar BukuKita (Chatbot AI)**:
    *   **Intent & Entity Recognition**: Memahami maksud spesifik pengguna (mencari berdasarkan penulis, judul, maupun kategori buku).
    *   **Natural Language Processing (NLP)**: Cerdas dan toleran terhadap variasi bahasa informal, singkatan, *typo*, maupun bahasa gaul sehari-hari.
    *   **Anti-Halusinasi (Data Grounding)**: Mengikat konteks jawaban Gemini API dengan data riil pada *database* Supabase untuk memastikan informasi stok dan harga 100% akurat.
*   **Manajemen Profil Pengguna**: Kemudahan memperbarui data informasi akun (*display name* dan *username*) secara *real-time*.

---

## 🛠️ Arsitektur & Teknologi

Aplikasi ini dibangun menggunakan ekosistem teknologi berikut:

*   **Core Engine (AI)**: Gemini API (Google AI) untuk pemrosesan bahasa alami (*Natural Language Processing*).
*   **Backend & Database**: Supabase (PostgreSQL, Auth, & Storage) sebagai pusat penyimpanan data terelasi.
*   **Data Source**: Open Library API untuk mekanisme penarikan (*scraping*) katalog buku.

---
