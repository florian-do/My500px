package com.do_f.my500px.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.do_f.my500px.R
import com.do_f.my500px.ui.fragment.ShowcaseFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, ShowcaseFragment.newInstance())
            .commit()
        // You can retrieve the consumer key with BuildConfig.FIVEPX_API_KEY
    }
}
