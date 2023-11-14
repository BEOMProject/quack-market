package com.example.quack_market.navigation

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quack_market.MainActivity
import com.example.quack_market.adapter.MypageAdapter
import com.example.quack_market.adapter.Product
import com.example.quack_market.databinding.FragmentMypageBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MypageFragment : Fragment() {
    private lateinit var mBinding: FragmentMypageBinding
    private val products = mutableListOf(
        Product(3000, "name")
        , Product(300, "name2")
        , Product(30000, "name3")
        , Product(20000, "name4")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMypageBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        val database = Firebase.database.getReference("users")
        val userId = Firebase.auth.currentUser?.uid
        val itemDecoration = RecyclerItemDecoration()

        database.child("$userId").child("name").get().addOnSuccessListener {
            mBinding.textUserName.text = it.value.toString()
        }
        mBinding.buttonSignOut.setOnClickListener{
            Firebase.auth.signOut()
            mActivity.hideBnvMain(true)
            mActivity.changeFragment(SigninFragment())
        }

        mBinding.recyclerviewMypage.setHasFixedSize(true)
        mBinding.recyclerviewMypage.layoutManager = LinearLayoutManager(activity
            , RecyclerView.HORIZONTAL, false)
        mBinding.recyclerviewMypage.adapter = MypageAdapter(activity as MainActivity, products)
        mBinding.recyclerviewMypage.addItemDecoration(itemDecoration)

        return mBinding.root
    }
    inner class RecyclerItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.right = 20
            outRect.left = 20
        }
    }
}