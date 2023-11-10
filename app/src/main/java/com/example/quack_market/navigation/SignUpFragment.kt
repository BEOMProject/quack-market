package com.example.quack_market.navigation

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quack_market.MainActivity
import com.example.quack_market.R
import com.example.quack_market.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpFragment: Fragment() {
    private lateinit var mBinding: FragmentSignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val database = Firebase.database.getReference("users")
        mBinding = FragmentSignupBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        val mainActivity = activity as MainActivity
        mBinding.quit.setOnClickListener{
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_main, SigninFragment())
                .commit()
        }

        mBinding.signUp.setOnClickListener {
            val email = mBinding.editEmail.text.toString()
            val name = mBinding.editName.text.toString()
            val password = mBinding.editPassword.text.toString()
            val passwordCheck = mBinding.editPasswordCheck.text.toString()

            when(checkForRegister(email, name, password, passwordCheck)){
                1 -> Toast.makeText(requireActivity(), "이름을 입력하세요."
                    , Toast.LENGTH_SHORT).show()
                2 -> mBinding.duplicationError.visibility = View.VISIBLE
                3 -> mBinding.lengthError.visibility = View.VISIBLE
                4 -> mBinding.matchPasswordError.visibility = View.VISIBLE
                5 -> {
                    auth.createUserWithEmailAndPassword(mBinding.editEmail.text.toString()
                        , mBinding.editPassword.text.toString())
                        .addOnCompleteListener(requireActivity()){
                            if (it.isSuccessful){
                                Toast.makeText(requireActivity(), "회원가입 성공 !"
                                    , Toast.LENGTH_SHORT).show()
                                val uid = Firebase.auth.currentUser?.uid
                                writeNewUser(database, email, uid, name)
                                mainActivity.changeFragment(SigninFragment())
                            } else
                                Toast.makeText(requireActivity(), "회원가입 실패 !"
                                    , Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
        return mBinding.root
    }

    private fun checkForRegister(userId: String, name: String,
                                 password: String, passwordCheck: String): Int{
        if (!checkName(name)) return 1
        if (!checkEmail(userId)) return 2
        if (!checkPassword(password)) return 3
        if (!checkPasswordAgain(passwordCheck, password)) return 4
        return 5
    }

    private fun checkEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun checkName(name: String): Boolean{
        return name.isNotEmpty()
    }
    private fun checkPassword(password: String): Boolean{
        return password.length > 5
    }
    private fun checkPasswordAgain(passwordCheck: String, password: String): Boolean{
        return passwordCheck == password
    }

    private fun writeNewUser(database: DatabaseReference, email: String
                             , uid: String?, name: String){
        database.child("$uid").child("name").setValue(name)
        database.child("$uid").child("email").setValue(email)
    }
}