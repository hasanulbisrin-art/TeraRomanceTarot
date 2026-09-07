package com.tera.romancetarot

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class Tarot(val name:String,val emoji:String,val meaning:String)

class MainActivity: AppCompatActivity() {
    private lateinit var root: LinearLayout
    private val prefs by lazy { getSharedPreferences("tarot", Context.MODE_PRIVATE) }
    private val cards = listOf(
        Tarot("THE LOVERS","💕","Ada chemistry kuat dan pilihan hati yang penting. Dalam romance, kartu ini membawa pesan tentang ketertarikan, kejujuran, dan hubungan yang dipilih dengan sadar."),
        Tarot("TWO OF CUPS","🥂","Perasaan berbalas dan koneksi emosional yang manis. Ini pertanda hubungan yang saling menghargai, komunikasi hangat, dan kemungkinan kedekatan yang semakin dalam."),
        Tarot("TEN OF CUPS","🏡","Energi bahagia, damai, dan rasa memiliki. Romance-mu hari ini membawa vibe hubungan yang nyaman, penuh dukungan, dan berpotensi menuju kebahagiaan bersama."),
        Tarot("ACE OF CUPS","💗","Awal baru dalam urusan hati. Bisa berarti munculnya perasaan baru, hati yang kembali terbuka, atau kesempatan untuk menyembuhkan dan mencintai dengan lebih tulus."),
        Tarot("FOUR OF WANDS","🎀","Rayakan hubunganmu! Kartu ini membawa energi stabil, hangat, dan menyenangkan. Ada tanda bahwa romance dapat terasa lebih aman, playful, dan penuh momen yang layak dirayakan.")
    )

    override fun onCreate(b: Bundle?) { super.onCreate(b); showHome() }

    private fun base(): LinearLayout {
        root=LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER
            setPadding(28,40,28,28); setBackgroundColor(Color.rgb(255,243,247))
        }
        setContentView(root); return root
    }
    private fun text(s:String,size:Float,bold:Boolean=false)=TextView(this).apply{
        text=s;textSize=size;setTextColor(Color.rgb(112,65,83));gravity=Gravity.CENTER
        if(bold) typeface=android.graphics.Typeface.DEFAULT_BOLD
        setPadding(10,8,10,8)
    }
    private fun button(s:String)=Button(this).apply{
        text=s; textSize=17f; setTextColor(Color.WHITE); background=getDrawable(com.tera.romancetarot.R.drawable.button_pink)
        isAllCaps=false; stateListAnimator=null
    }

    private fun showHome(){
        val r=base()
        r.addView(text("✦  TERA'S ROMANCE TAROT  ✦",20,true))
        r.addView(text("🔮",64f))
        r.addView(text("Hai Tera Hemustra,\napakah kamu mau melihat tarot romance-mu hari ini?",22,true),
            LinearLayout.LayoutParams(-1,0,1f))
        val b=button("IYA 💕"); r.addView(b,LinearLayout.LayoutParams(-1,64))
        b.setOnClickListener{ startShuffle() }
        val remaining=remaining()
        if(remaining>0) lockHome(r,remaining)
    }

    private fun lockHome(r:LinearLayout, ms:Long){
        r.removeViews(3,2)
        r.addView(text("🔒 Ramalanmu hari ini sudah dibuka.\nKembali lagi setelah:",18,true))
        val tv=text("",18,true);r.addView(tv)
        object:CountDownTimer(ms,1000){
            override fun onTick(x:Long){tv.text=format(x)}
            override fun onFinish(){showHome()}
        }.start()
    }

    private fun startShuffle(){
        val r=base()
        r.addView(text("✨ Mengocok kartu... ✨",24,true))
        val card=TextView(this).apply{text="🔮";textSize=78f;gravity=Gravity.CENTER;background=getDrawable(R.drawable.card_back)}
        r.addView(card,LinearLayout.LayoutParams(190,280))
        val anim=ObjectAnimator.ofFloat(card,"rotationY",0f,1080f).apply{duration=1800;interpolator=DecelerateInterpolator()}
        anim.start(); card.postDelayed({showChoices()},1900)
    }

    private fun showChoices(){
        val r=base()
        r.addView(text("Pilih satu kartu 💗",25,true))
        val row=LinearLayout(this).apply{gravity=Gravity.CENTER}
        repeat(5){ i->
            val c=TextView(this).apply{text="✦";textSize=28f;gravity=Gravity.CENTER;setTextColor(Color.WHITE);background=getDrawable(R.drawable.card_back)}
            val lp=LinearLayout.LayoutParams(0,230,1f);lp.setMargins(4,4,4,4);row.addView(c,lp)
            c.setOnClickListener{reveal(cards[Random.nextInt(cards.size)],c)}
        }
        r.addView(row)
        r.addView(text("Ikuti kartu yang paling memanggil hatimu.",15f))
    }

    private fun reveal(t:Tarot, source:View){
        val r=base()
        r.addView(text("💞 Kartu romance-mu hari ini 💞",22,true))
        val card=TextView(this).apply{text="${t.emoji}\n\n${t.name}";textSize=25f;gravity=Gravity.CENTER;setTextColor(Color.rgb(112,65,83));background=getDrawable(R.drawable.card_front)}
        r.addView(card,LinearLayout.LayoutParams(220,320))
        card.scaleX=0f;card.scaleY=0f
        card.animate().scaleX(1f).scaleY(1f).setDuration(500).start()
        r.addView(text(t.meaning,17f))
        r.addView(text("🔮 Tarot adalah hiburan/refleksi, bukan kepastian masa depan.",13f))
        prefs.edit().putLong("last",System.currentTimeMillis()).apply()
        val home=button("KEMBALI KE BERANDA 💗");r.addView(home,LinearLayout.LayoutParams(-1,62))
        home.setOnClickListener{showHome()}
    }

    private fun remaining():Long{
        val last=prefs.getLong("last",0L); return (24*60*60*1000L)-(System.currentTimeMillis()-last)
    }
    private fun format(ms:Long):String{
        val h=TimeUnit.MILLISECONDS.toHours(ms);val m=TimeUnit.MILLISECONDS.toMinutes(ms)%60;val s=TimeUnit.MILLISECONDS.toSeconds(ms)%60
        return String.format("%02d jam %02d menit %02d detik",h,m,s)
    }
}