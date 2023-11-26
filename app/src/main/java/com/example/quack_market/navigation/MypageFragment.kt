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
import com.example.quack_market.databinding.FragmentMypageBinding
import com.example.quack_market.dto.Product
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

    private val products: MutableList<Product> = mutableListOf()
    private lateinit var mBinding: FragmentMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myPageAdapter = MypageAdapter(products)
        mBinding = FragmentMypageBinding.inflate(inflater, container, false)

        postDatabase.addValueEventListener(object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                products.clear()
                for (data in snapshot.children){
                    val sellerId = data.child("sellerId").getValue(String::class.java)
                    if(sellerId != userId){
                        continue
                    } else {
                        val product = data.child("title").getValue(String::class.java)
                        val price = data.child("price").getValue(Int::class.java)
                        val date = data.child("createdAt").getValue(String::class.java)
                        val desc = data.child("description").getValue(String::class.java)
                        val imageUrl = data.child("imageUrl").getValue(String::class.java)
                        val onSale = data.child("onSale").getValue(Boolean::class.java)
                        val postId = data.key

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .parse(date!!)
                        val formatDate = dateFormat.let {
                            SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(it!!)
                        }

                        products.add(Product(price?:0, product.toString(), formatDate,
                            desc.toString(), imageUrl.toString(), onSale!!, postId!!))
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
                val product = products[position]
                mActivity.backFragment(
                    SalesPostChangeFragment(product, product.postId))
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