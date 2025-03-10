package com.example.finalproject_fittrack

import android.app.Application
import com.example.finalproject_fittrack.dataBase.ProfileRepository

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        ProfileRepository.init()

    }
}
