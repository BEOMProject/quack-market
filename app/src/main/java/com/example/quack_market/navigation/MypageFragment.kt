package com.example.quack_market.navigation

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quack_market.MainActivity
import com.example.quack_market.adapter.MypageAdapter
import com.example.quack_market.dao.PostModel
import com.example.quack_market.databinding.FragmentMypageBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class MyPageFragment : Fragment() {
    private val userId = Firebase.auth.currentUser?.uid
    private val postDatabase = Firebase.database.getReference("post")

    private val postList: MutableList<PostModel> = mutableListOf()
    private lateinit var mBinding: FragmentMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myPageAdapter = MypageAdapter(postList)
        mBinding = FragmentMypageBinding.inflate(inflater, container, false)

        postDatabase.addValueEventListener(object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (data in snapshot.children){
                    val sellerId = data.child("sellerId").getValue(String::class.java)
                    if(sellerId != userId){
                        continue
                    } else {
                        val postId = data.key
                        val title = data.child("title").getValue(String::class.java)
                        val imageUrl = data.child("imageUrl").getValue(String::class.java)
                        val price = data.child("price").getValue(Int::class.java)
                        val createAt = data.child("createdAt").getValue(String::class.java)
                        val description = data.child("description").getValue(String::class.java)
                        val sellerId = data.child("sellerId").getValue(String::class.java)
                        val onSale = data.child("onSale").getValue(Boolean::class.java)

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .parse(createAt!!)
                        val formatDate = dateFormat.let {
                            SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(it!!)
                        }

                        postList.add(PostModel(postId.toString(), title.toString(), imageUrl.toString(),
                            price!!.toLong(), formatDate.toString(), description.toString(), sellerId.toString(), onSale!!))
                    }
                }
                myPageAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "데이터 로딩 실패"
                    , Toast.LENGTH_SHORT).show()
            }
        })

        val mActivity = activity as MainActivity
        val database = Firebase.database.getReference("users")
        val userId = Firebase.auth.currentUser?.uid
        val itemDecoration = RecyclerItemDecoration(20)

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
        mBinding.recyclerviewMypage.adapter = myPageAdapter
        mBinding.recyclerviewMypage.addItemDecoration(itemDecoration)

        myPageAdapter.setItemClickListener(object : MypageAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val postList = postList[position]
                mActivity.backFragment(
                    SalesPostChangeFragment(postList))
            }
        })
        return mBinding.root
    }

    inner class RecyclerItemDecoration(val width: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.right = width
            outRect.left = width
        }
    }
}