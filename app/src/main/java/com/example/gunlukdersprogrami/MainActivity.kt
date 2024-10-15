package com.example.gunlukdersprogrami

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gunlukdersprogrami.databinding.ActivityMainBinding
import com.example.gunlukdersprogrami.databinding.YeniDersLayoutBinding
import com.shashank.sony.fancytoastlib.FancyToast

class MainActivity : AppCompatActivity() {

    // ActivityMainBinding obyektini yaratmaq üçün dəyişən. Bu obyekt istifadə edərək XML faylındakı elementlərə müraciət edə bilərik.
    private lateinit var binding: ActivityMainBinding

    // Dərslərin adlarını saxlayan bir array. Bu array AutoCompleteTextView üçün istifadə olunur.
    private val DERSLER = arrayOf("Riyaziyyat", "Fizka", "Edebiyyat", "Hendese", "Tarix")

    // Bütün dərslərin məlumatlarını saxlayan ArrayList. Bu, hər dərs haqqında məlumatı saxlayır.
    private val tumDerslerinBilgileri: ArrayList<Dersler> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActivityMainBinding obyektini tərtib edərək, XML faylı ilə bağlayırıq.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ekranın kənarlarını təyin etmək üçün WindowInsets istifadə olunur ki, sistem barları ilə müvafiq elementlərin üzləşməsi təmin edilsin.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // AutoCompleteTextView üçün adapter yaradılır və DERSLER array-i bu adapterə qoşulur.
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, DERSLER)
        binding.etDersAd.setAdapter(adapter)

        // İlk olaraq btnOrtalamaHesabla düyməsinin görünürlüğü yoxlanılır və bu düymənin görünürlüyü uyğun olaraq təyin edilir.
        binding.btnOrtalamaHesabla.visibility = if (binding.rootLayout.childCount == 0) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        // btnDersEkle düyməsinə kliklənərkən yeni dərs əlavə etmək üçün bir OnClickListener yaradılır.
        binding.btnDersEkle.setOnClickListener {

            // Yeni dərs üçün layout yaradılır və inflater ilə tərtib olunur.
            val inflater = LayoutInflater.from(this)
            val yeniDersLayoutBinding = YeniDersLayoutBinding.inflate(inflater, null, false)
            if (!binding.etDersAd.text.isNullOrEmpty()) {
                // Əgər dərs adı boş deyilsə, yeni dərs layoutu tərtib edilir.

                yeniDersLayoutBinding.etYeniDersAd.setAdapter(adapter)

                // Yeni dərs məlumatları əsas layoutdakı seçilmiş məlumatlarla təyin olunur.
                val dersAdi = binding.etDersAd.text.toString()
                val dersKredi = binding.spnDersKredi.selectedItem.toString()
                val dersHarf = binding.spnDersNot.selectedItem.toString()

                // Yeni dərs layoutunda göstərilən məlumatları təyin edir.
                yeniDersLayoutBinding.etYeniDersAd.setText(dersAdi)
                yeniDersLayoutBinding.spnYeniDersKredi.setSelection(
                    spinnerDegeriIndexiBul(
                        binding.spnDersKredi,
                        dersKredi
                    )
                )
                yeniDersLayoutBinding.spnYeniDersNot.setSelection(
                    spinnerDegeriIndexiBul(
                        binding.spnDersNot,
                        dersHarf
                    )
                )

                // Yeni yaradılan dərsi silmək üçün btnDersSil düyməsinə bir OnClickListener əlavə olunur.
                yeniDersLayoutBinding.btnDersSil.setOnClickListener {
                    binding.rootLayout.removeView(yeniDersLayoutBinding.root)
                    binding.btnOrtalamaHesabla.visibility =
                        if (binding.rootLayout.childCount == 0) {
                            View.INVISIBLE
                        } else {
                            View.VISIBLE
                        }
                }

                // Yeni dərs layoutu rootLayout-a əlavə olunur və btnOrtalamaHesabla düyməsi görünən edilir.
                binding.rootLayout.addView(yeniDersLayoutBinding.root)
                binding.btnOrtalamaHesabla.visibility = View.VISIBLE

                // Dərs əlavə edildikdən sonra giriş sahələrini sıfırlamaq üçün sifirla funksiyası çağırılır.
                sifirla()

            } else {
                // Əgər dərs adı boşdursa, xəbərdarlıq mesajı göstərilir.
                FancyToast.makeText(
                    this,
                    "Ders Adina Girin",
                    FancyToast.LENGTH_LONG,
                    FancyToast.WARNING,
                    false
                ).show()
            }
        }

        // btnOrtalamaHesabla düyməsinə kliklənərkən ortalamaHesabla funksiyası çağırılır.
        binding.btnOrtalamaHesabla.setOnClickListener {
            ortalamaHesabla()
        }
    }

    // Giriş sahələrini (dərs adı və spinnerlər) sıfırlamaq üçün istifadə olunan funksiya.
    private fun sifirla() {
        binding.etDersAd.setText("")
        binding.spnDersKredi.setSelection(0)
        binding.spnDersNot.setSelection(0)
    }

    // Spinner elementindəki seçilən dəyərin indexini tapmaq üçün funksiya.
    private fun spinnerDegeriIndexiBul(spinner: Spinner, aranacaqDeyer: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == aranacaqDeyer) {
                return i
            }
        }
        return 0
    }

    // Orta qiyməti hesablamaq üçün funksiya.
    private fun ortalamaHesabla() {
        var toplamNot = 0.0
        var toplamKredi = 0.0

        // rootLayout-dakı bütün uşaqları (yəni, əlavə edilən dərsləri) dövr edərək məlumatları yığırıq.
        for (i in 0 until binding.rootLayout.childCount) {
            val teksetir = binding.rootLayout.getChildAt(i)
            val yeniDersBinding = YeniDersLayoutBinding.bind(teksetir)
            val geciciDers = Dersler(
                yeniDersBinding.etYeniDersAd.text.toString(),
                (yeniDersBinding.spnYeniDersKredi.selectedItemPosition + 1).toString(),
                yeniDersBinding.spnYeniDersNot.selectedItem.toString()
            )
            tumDerslerinBilgileri.add(geciciDers)
        }

        // Hər dərsin qiymətini və kreditini toplayırıq.
        for (oankiDers in tumDerslerinBilgileri) {
            toplamNot += harfiNotaCevir(oankiDers.dersHarfNot) * oankiDers.dersKredi.toDouble()
            toplamKredi += oankiDers.dersKredi.toDouble()
        }

        // Orta qiymət hesablanır.
        val ortalama = if (toplamKredi > 0) toplamNot / toplamKredi else 0.0

        // Orta qiymət FancyToast vasitəsilə istifadəçiyə göstərilir.
        FancyToast.makeText(
            this,
            "Ortalama: $ortalama",
            FancyToast.LENGTH_LONG,
            FancyToast.WARNING,
            false
        ).show()
        // Bütün dərs məlumatları sıfırlanır.
        tumDerslerinBilgileri.clear()
    }

    // Dərs qiymətini hərf notuna çevirmək üçün istifadə olunan funksiya.
    private fun harfiNotaCevir(gelenNotHarfDeyeri: String): Double {
        return when (gelenNotHarfDeyeri) {
            "AA" -> 4.0
            "BA" -> 3.5
            "BB" -> 3.0
            "CB" -> 2.5
            "CC" -> 2.0
            "DC" -> 1.5
            "DD" -> 1.0
            "FF" -> 0.0
            else -> 0.0
        }
    }
}