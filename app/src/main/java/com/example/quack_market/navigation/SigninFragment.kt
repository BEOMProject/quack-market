package com.example.quack_market.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quack_market.MainActivity
import com.example.quack_market.databinding.FragmentSigninBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SigninFragment : Fragment() {
    private lateinit var mBinding: FragmentSigninBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSigninBinding.inflate(inflater, container, false)

        mBinding.loginButton.setOnClickListener {
            val userId = mBinding.editIdInLogin.text.toString()
            val password = mBinding.editPasswordInLogin.text.toString()
            doLogin(userId, password)
        }

        mBinding.signUpButton.setOnClickListener {
            register()
        }
        return mBinding.root
    }

    private fun doLogin(userId: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userId, password)
            .addOnCompleteListener(requireActivity()) {
                if (it.isSuccessful) {
                    startActivity(
                        Intent(requireContext(), MainActivity::class.java)
                    )
                    requireActivity().finish()
                } else {
                    Log.w("FragmentSignIn", "signInWithEmail", it.exception)
                    Toast.makeText(requireActivity(), "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun register() {
        val mainActivity = activity as MainActivity
        mainActivity.changeFragment(SignUpFragment())
    }
}