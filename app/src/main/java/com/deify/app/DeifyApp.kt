package com.deify.app

import android.app.Application

class DeifyApp : Application() {
    val database by lazy { com.deify.app.data.local.AppDatabase.build(this) }
}
