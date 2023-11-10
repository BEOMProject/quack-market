package com.example.quack_market

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.quack_market.databinding.ActivityMainBinding
import com.example.quack_market.navigation.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser == null) {
            hideBnvMain(true)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_main, SigninFragment())
                commit()
            }
        } else {
            hideBnvMain(false)
            initNavigationBar()
        }
    }

    fun initNavigationBar() {
        binding.bnvMain.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.boardItem -> {
                        changeFragment(BoardFragment())
                    }

                    R.id.messageItem -> {
                        changeFragment(MessageFragment())
                    }

                    R.id.mypageItem -> {
                        changeFragment(MypageFragment())
                    }

                    R.id.uploadItem -> {
                        changeFragment(UploadFragment())
                    }
                }
                true
            }
            selectedItemId = R.id.boardItem
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameMain.id, fragment).commit()
    }

    fun hideBnvMain(state: Boolean) {
        if (state)
            binding.bnvMain.visibility = View.GONE
        else
            binding.bnvMain.visibility = View.VISIBLE
    }
}