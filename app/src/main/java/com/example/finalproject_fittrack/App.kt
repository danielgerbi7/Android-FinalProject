package com.example.finalproject_fittrack

import android.app.Application
import android.content.Context
import com.example.finalproject_fittrack.logic.ProfileManager
import com.example.finalproject_fittrack.logic.WorkoutManager

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        fun getAppContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        ProfileManager.init(this)
        //WorkoutManager.init(this)
        ProfileManager.getInstance().checkAndSaveDefaultWorkouts()    }
}
