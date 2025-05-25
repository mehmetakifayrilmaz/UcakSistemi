import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

class Ucak {
    String isim;
    String nereden;
    String nereye;
    int sure;
    boolean aktif;
    String tarihSaat;
    String[][] koltuklar;
    String iptalNedeni = "";

    public Ucak(String isim, String nereden, String nereye, int satir, int sutun, int sure, String tarihSaat) {
        this.isim = isim;
        this.nereden = nereden;
        this.nereye = nereye;
        this.sure = sure;
        this.tarihSaat = tarihSaat;
        this.aktif = true;
        this.koltuklar = new String[satir * sutun][2];

        for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {
                int index = i * sutun + j;
                koltuklar[index][0] = (char) ('A' + i) + String.valueOf(j + 1);
                koltuklar[index][1] = "";
            }
        }
    }

    public void yazdir() {
        System.out.println("Uçak: " + isim + " | " + nereden + " -> " + nereye + " | Süre: " + sure + " saat | Tarih/Saat: " + tarihSaat +
                " | Durum: " + (aktif ? "Aktif" : "Pasif") + (!iptalNedeni.isEmpty() ? " | İptal Nedeni: " + iptalNedeni : ""));
    }
}

class Bilet {
    static AtomicInteger sayac = new AtomicInteger(1000);
    int biletNo;
    String ad;
    String tc;
    String email;
    String koltuk;
    LocalDateTime alisZamani;

    public Bilet(String ad, String tc, String email, String koltuk) {
        this.biletNo = sayac.getAndIncrement();
        this.ad = ad;
        this.tc = tc;
        this.email = email;
        this.koltuk = koltuk;
        this.alisZamani = LocalDateTime.now();
    }

    public void yazdir() {
        System.out.println("Bilet No: " + biletNo + " | Bilet Sahibi: " + ad + " | TC: " + tc + " | Email: " + email + " | Koltuk: " + koltuk +
                " | Alım Zamanı: " + alisZamani.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    public boolean iptalEdilebilir() {
        return Duration.between(alisZamani, LocalDateTime.now()).toHours() < 2;
    }
}

public class UcakBiletSistemi {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Ucak> ucaklar = new ArrayList<>();
    static ArrayList<Bilet> biletler = new ArrayList<>();

    public static void main(String[] args) {
        anaMenu();
    }

    public static void anaMenu() {
        while (true) {
            System.out.println("--- Uçak Bilet Rezervasyon Sistemine Hoşgeldiniz ---");
            System.out.print("Kullanıcı türü (yolcu/firma/çıkış): ");
            String rol = scanner.nextLine().toLowerCase();

            switch (rol) {
                case "firma" -> firmaGiris();
                case "yolcu" -> yolcuPaneli();
                case "çıkış" -> {
                    System.out.println("Programdan çıkılıyor...");
                    return;
                }
                default -> System.out.println("Geçersiz kullanıcı türü.\n");
            }
        }
    }

    public static void firmaGiris() {
        while (true) {
            System.out.print("Kullanıcı adı: ");
            String kullanici = scanner.nextLine();
            System.out.print("Şifre: ");
            String sifre = scanner.nextLine();

            if (kullanici.equals("admin") && sifre.equals("admin")) {
                firmaPaneli();
                break;
            } else {
                System.out.print("Hatalı giriş. Tekrar dene (e), Ana Menü (a), Çıkış (ç): ");
                String secim = scanner.nextLine().toLowerCase();
                if (secim.equals("a")) return;
                if (secim.equals("ç")) System.exit(0);
            }
        }
    }

    public static void firmaPaneli() {
        while (true) {
            System.out.println("\n[Firma Paneli] Seçenekler: ");
            System.out.println("1. Uçuş Ekle");
            System.out.println("2. Uçuşları Listele/Düzenle");
            System.out.println("3. Uçuş Sil");
            System.out.println("4. Ana Menüye Dön");
            System.out.print("Seçiminiz: ");
            int secim = Integer.parseInt(scanner.nextLine());

            switch (secim) {
                case 1 -> ucusEkle();
                case 2 -> ucuslariYonet();
                case 3 -> ucusSil();
                case 4 -> {
                    return;
                }
                default -> System.out.println("Geçersiz seçim.");
            }
        }
    }

    public static void ucusEkle() {
        System.out.print("Uçak ismi: ");
        String isim = scanner.nextLine();
        System.out.print("Nereden: ");
        String nereden = scanner.nextLine();
        System.out.print("Nereye: ");
        String nereye = scanner.nextLine();
        System.out.print("Kaç sıra olacak (örn: 10): ");
        int satir = Integer.parseInt(scanner.nextLine());
        System.out.print("Sıra başına kaç koltuk olacak (örn: 4): ");
        int sutun = Integer.parseInt(scanner.nextLine());
        System.out.print("Uçuş süresi (saat): ");
        int sure = Integer.parseInt(scanner.nextLine());
        System.out.print("Tarih/Saat (yyyy-MM-dd HH:mm): ");
        String tarihSaat = scanner.nextLine();

        ucaklar.add(new Ucak(isim, nereden, nereye, satir, sutun, sure, tarihSaat));
        System.out.println("Uçuş başarıyla eklendi.");
    }

    public static void ucuslariYonet() {
        if (ucaklar.isEmpty()) {
            System.out.println("Kayıtlı uçuş yok.");
            return;
        }

        for (int i = 0; i < ucaklar.size(); i++) {
            System.out.print((i + 1) + ". ");
            ucaklar.get(i).yazdir();
        }

        System.out.print("Düzenlemek istediğiniz uçuşun numarası: ");
        int sec = Integer.parseInt(scanner.nextLine()) - 1;
        if (sec >= 0 && sec < ucaklar.size()) {
            Ucak u = ucaklar.get(sec);
            System.out.print("Bu uçuşu aktif yapmak için 'a', pasif yapmak için 'p' girin: ");
            String durum = scanner.nextLine();
            if (durum.equals("p")) {
                System.out.print("İptal nedeni girin: ");
                u.iptalNedeni = scanner.nextLine();
                u.aktif = false;
            } else if (durum.equals("a")) {
                u.aktif = true;
                u.iptalNedeni = "";
            }
            System.out.println("Durum güncellendi.");
        } else {
            System.out.println("Geçersiz seçim.");
        }
    }

    public static void ucusSil() {
        if (ucaklar.isEmpty()) {
            System.out.println("Silinecek uçuş bulunmamaktadır.");
            return;
        }
        for (int i = 0; i < ucaklar.size(); i++) {
            System.out.print((i + 1) + ". ");
            ucaklar.get(i).yazdir();
        }
        System.out.print("Silmek istediğiniz uçuşun numarası: ");
        int sec = Integer.parseInt(scanner.nextLine()) - 1;
        if (sec >= 0 && sec < ucaklar.size()) {
            ucaklar.remove(sec);
            System.out.println("Uçuş silindi.");
        } else {
            System.out.println("Geçersiz seçim.");
        }
    }

    public static void yolcuPaneli() {
        if (ucaklar.stream().noneMatch(u -> u.aktif)) {
            System.out.print("Şu anda aktif uçuş yok. Ana Menü (a), Çıkış (ç): ");
            String sec = scanner.nextLine();
            if (sec.equals("ç")) System.exit(0);
            return;
        }

        for (int i = 0; i < ucaklar.size(); i++) {
            if (ucaklar.get(i).aktif) {
                System.out.print((i + 1) + ". ");
                ucaklar.get(i).yazdir();
            }
        }

        System.out.print("Bilet almak istediğiniz uçuş numarasını girin: ");
        int secim = Integer.parseInt(scanner.nextLine()) - 1;
        if (secim < 0 || secim >= ucaklar.size() || !ucaklar.get(secim).aktif) {
            System.out.println("Geçersiz seçim.");
            return;
        }

        Ucak secilen = ucaklar.get(secim);

        System.out.println("Koltuk Durumu:");
        for (int i = 0; i < secilen.koltuklar.length; i++) {
            System.out.printf("%-10s", secilen.koltuklar[i][0] + ": " + (secilen.koltuklar[i][1].isEmpty() ? "Boş" : "Dolu"));
            if ((i + 1) % 4 == 0) System.out.println();
        }

        System.out.print("Seçmek istediğiniz koltuk kodu (örnek A1): ");
        String kod = scanner.nextLine().toUpperCase();
        boolean bulundu = false;
        for (String[] koltuk : secilen.koltuklar) {
            if (koltuk[0].equals(kod)) {
                if (!koltuk[1].isEmpty()) {
                    System.out.println("Bu koltuk dolu. Lütfen başka koltuk seçin.");
                    return;
                }
                System.out.print("Adınız: ");
                String ad = scanner.nextLine();
                System.out.print("TC No: ");
                String tc = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();

                koltuk[1] = ad;
                Bilet b = new Bilet(ad, tc, email, kod);
                biletler.add(b);
                System.out.println("Bilet alındı.\n");
                b.yazdir();
                bulundu = true;
                break;
            }
        }
        if (!bulundu) System.out.println("Koltuk bulunamadı.");

        System.out.print("Bilet iptal menüsüne gitmek ister misiniz? (e/h): ");
        if (scanner.nextLine().equalsIgnoreCase("e")) {
            biletIptalMenusu();
        }
    }

    public static void biletIptalMenusu() {
        if (biletler.isEmpty()) {
            System.out.println("İptal edilebilecek bilet bulunamadı.");
            return;
        }
        for (int i = 0; i < biletler.size(); i++) {
            Bilet b = biletler.get(i);
            b.yazdir();
            if (b.iptalEdilebilir()) {
                System.out.print("Bu bileti iptal etmek istiyor musunuz? (e/h): ");
                if (scanner.nextLine().equalsIgnoreCase("e")) {
                    for (Ucak u : ucaklar) {
                        for (String[] k : u.koltuklar) {
                            if (k[0].equals(b.koltuk) && k[1].equals(b.ad)) {
                                k[1] = "";
                            }
                        }
                    }
                    biletler.remove(i);
                    System.out.println("Bilet iptal edildi.");
                    return;
                }
            } else {
                System.out.println("Bu bilet 2 saatten eski, iptal edilemez.");
            }
        }
    }
}
