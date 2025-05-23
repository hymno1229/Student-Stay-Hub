package com.example.messmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        val usersIcon = findViewById<ImageView>(R.id.usersIcon)
        val manageIcon = findViewById<ImageView>(R.id.manageIcon)
        val settingsIcon = findViewById<ImageView>(R.id.settingsIcon)

        val home = findViewById<LinearLayout>(R.id.homelayout)
        val users = findViewById<LinearLayout>(R.id.usersLayout)
        val manage = findViewById<LinearLayout>(R.id.managelayout)
        val settings = findViewById<LinearLayout>(R.id.settingsLayout)

        val homeTxt = findViewById<TextView>(R.id.homeLabel)
        val usersTxt = findViewById<TextView>(R.id.usersLabel)
        val manageTxt = findViewById<TextView>(R.id.manageLabel)
        val settingsTxt = findViewById<TextView>(R.id.settingsLabel)

        home.setOnClickListener {
            val fragment = HomeFragment()
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
            fragmentTransaction.commit()

            home.setBackgroundColor(resources.getColor(R.color.primaryColor))
            users.setBackgroundColor(resources.getColor(R.color.white))
            manage.setBackgroundColor(resources.getColor(R.color.white))
            settings.setBackgroundColor(resources.getColor(R.color.white))

            homeIcon.setImageResource(R.drawable.home_white)
            usersIcon.setImageResource(R.drawable.users_black)
            manageIcon.setImageResource(R.drawable.manage_black)
            settingsIcon.setImageResource(R.drawable.settings_black)

            homeTxt.setTextColor(resources.getColor(R.color.white))
            usersTxt.setTextColor(resources.getColor(R.color.black))
            manageTxt.setTextColor(resources.getColor(R.color.black))
            settingsTxt.setTextColor(resources.getColor(R.color.black))

        }
        users.setOnClickListener {

            val fragment = FragmentUsers()
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
            fragmentTransaction.commit()

            home.setBackgroundColor(resources.getColor(R.color.white))
            users.setBackgroundColor(resources.getColor(R.color.primaryColor))
            manage.setBackgroundColor(resources.getColor(R.color.white))
            settings.setBackgroundColor(resources.getColor(R.color.white))

            homeIcon.setImageResource(R.drawable.home_black)
            usersIcon.setImageResource(R.drawable.users_white)
            manageIcon.setImageResource(R.drawable.manage_black)
            settingsIcon.setImageResource(R.drawable.settings_black)

            homeTxt.setTextColor(resources.getColor(R.color.black))
            usersTxt.setTextColor(resources.getColor(R.color.white))
            manageTxt.setTextColor(resources.getColor(R.color.black))
            settingsTxt.setTextColor(resources.getColor(R.color.black))
        }
        manage.setOnClickListener {

            val fragment = FragmentManage()
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
            fragmentTransaction.commit()

            home.setBackgroundColor(resources.getColor(R.color.white))
            users.setBackgroundColor(resources.getColor(R.color.white))
            manage.setBackgroundColor(resources.getColor(R.color.primaryColor))
            settings.setBackgroundColor(resources.getColor(R.color.white))

            homeIcon.setImageResource(R.drawable.home_black)
            usersIcon.setImageResource(R.drawable.users_black)
            manageIcon.setImageResource(R.drawable.manage_white)
            settingsIcon.setImageResource(R.drawable.settings_black)

            homeTxt.setTextColor(resources.getColor(R.color.black))
            usersTxt.setTextColor(resources.getColor(R.color.black))
            manageTxt.setTextColor(resources.getColor(R.color.white))
            settingsTxt.setTextColor(resources.getColor(R.color.black))
        }
        settings.setOnClickListener {

            val fragment = FragmentSettings()
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
            fragmentTransaction.commit()

            home.setBackgroundColor(resources.getColor(R.color.white))
            users.setBackgroundColor(resources.getColor(R.color.white))
            manage.setBackgroundColor(resources.getColor(R.color.white))
            settings.setBackgroundColor(resources.getColor(R.color.primaryColor))

            homeIcon.setImageResource(R.drawable.home_black)
            usersIcon.setImageResource(R.drawable.users_black)
            manageIcon.setImageResource(R.drawable.manage_black)
            settingsIcon.setImageResource(R.drawable.settings_white)

            homeTxt.setTextColor(resources.getColor(R.color.black))
            usersTxt.setTextColor(resources.getColor(R.color.black))
            manageTxt.setTextColor(resources.getColor(R.color.black))
            settingsTxt.setTextColor(resources.getColor(R.color.white))
        }
    }
}