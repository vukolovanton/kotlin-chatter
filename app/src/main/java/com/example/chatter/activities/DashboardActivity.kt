package com.example.chatter.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.example.chatter.R
import com.example.chatter.activities.adapters.SectionPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        title = "Dashboard";

        //Реализуем табы
        var tabLayout: TabLayout? = null
        var viewPager: ViewPager? = null
        tabLayout = findViewById(R.id.dashTabLayout)
        viewPager = findViewById(R.id.dashViewPagerId)
        tabLayout.addTab(tabLayout.newTab().setText("All Users"))
        tabLayout.addTab(tabLayout.newTab().setText("Nothing"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.setTabTextColors(Color.DKGRAY, Color.WHITE)
        val adapter = SectionPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
        })
        if (intent.extras != null) {
            var username = intent.extras!!.get("name")
        }

    }
    //Создаем пункты меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    //Настраиваем пункты меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item != null) {
            if (item.itemId == R.id.logoutId) {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            if (item.itemId == R.id.settingsId) {
                //Открыть настройки
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return true
    }
}
