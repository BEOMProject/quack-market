package com.example.quack_market.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quack_market.MainActivity
import com.example.quack_market.R
import com.example.quack_market.databinding.FragmentMypageBinding
import com.example.quack_market.databinding.FragmentSigninBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MypageFragment : Fragment() {
    private lateinit var mBinding: FragmentMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMypageBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        val database = Firebase.database.getReference("users")
        val userId = Firebase.auth.currentUser?.uid

        database.child("$userId").child("name").get().addOnSuccessListener {
            mBinding.textUserName.text = it.value.toString()
        }
        mBinding.button.setOnClickListener{
            Firebase.auth.signOut()
            mActivity.hideBnvMain(true)
            mActivity.changeFragment(SigninFragment())
        }

        return mBinding.root
    }
}