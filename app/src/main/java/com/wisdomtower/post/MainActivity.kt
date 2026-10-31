package com.wisdomtower.post

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/** The only activity. Everything on screen lives in PostApp. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostApp()
        }
    }
}
