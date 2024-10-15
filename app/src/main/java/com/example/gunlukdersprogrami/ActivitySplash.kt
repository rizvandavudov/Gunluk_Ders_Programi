package com.example.gunlukdersprogrami

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gunlukdersprogrami.databinding.ActivitySplashBinding

class ActivitySplash : AppCompatActivity() {
    // binding obyektini yaradır ki, bu vasitəsilə layout elementlərinə daha asan giriş etmək mümkün olur
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sistem barlarını tam ekran şəkildə göstərmək üçün istifadə olunur
        enableEdgeToEdge()
        // Layout'u inflate edir və binding obyektinə təyin edir
        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // View-in padding-lərini sistem barlarının ölçüsünə görə təyin edir
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Aşağıdan gələn animasiya üçün animation obyektini yaradır
        val asagidanGelenButon = AnimationUtils.loadAnimation(this, R.anim.asagidangelenbutton)
        // Yuxarıdan gələn animasiya üçün animation obyektini yaradır
        val yuxaridanGelenBalon = AnimationUtils.loadAnimation(this, R.anim.yuxaridangelenbalon)
        // Aşağıya gedən animasiya üçün animation obyektini yaradır
        val asgiyaGeriDonenButon = AnimationUtils.loadAnimation(this, R.anim.asagigedenbutton)
        // Yuxarıya gedən animasiya üçün animation obyektini yaradır
        val yuxariyaGeriDonenBalon = AnimationUtils.loadAnimation(this, R.anim.yuxariyagedenballon)

        // buttonstart düyməsinə aşağıdan gələn animasiyanı tətbiq edir
        binding.buttonstart.animation = asagidanGelenButon
        // imageView üçün yuxarıdan gələn animasiyanı tətbiq edir
        binding.imageView.animation = yuxaridanGelenBalon

        // buttonstart düyməsinə klik edildikdə baş verənləri təyin edir
        binding.buttonstart.setOnClickListener {
            // Düyməyə aşağıya gedən animasiyanı tətbiq edir
            binding.buttonstart.startAnimation(asgiyaGeriDonenButon)
            // Balona yuxarıya gedən animasiyanı tətbiq edir
            binding.imageView.startAnimation(yuxariyaGeriDonenBalon)

            // 1 saniyəlik geri sayım (CountDownTimer) başlatmaq üçün istifadə olunur
            object : CountDownTimer(1000, 1000) {

                override fun onFinish() {
                    // Geri sayım bitdikdə MainActivity-ə keçid edir
                    var intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }

                override fun onTick(p0: Long) {
                    // Geri sayım ərzində hər tikdə (hər saniyə keçdikdə) burada əməliyyat aparıla bilər, lakin bu halda heç bir əməliyyat aparılmır.
                }

            }.start()

        }
    }
}