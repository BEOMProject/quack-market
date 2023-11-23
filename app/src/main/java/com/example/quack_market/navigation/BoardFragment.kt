package com.example.quack_market.navigation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quack_market.MainActivity
import com.example.quack_market.R
import com.example.quack_market.adapter.BoardPostAdapter
import com.example.quack_market.databinding.FragmentBoardBinding
import com.example.quack_market.navigation.DBKey.Companion.DB_POST
import com.example.quack_market.navigation.DBKey.Companion.DB_USERS
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Date

class DBKey {
    companion object {
        const val DB_POST = "post"
        const val DB_USERS = "users"
    }
}

data class PostModel(
    val title: String,
    val imageUrl: String,
    val price: Long,
    val createdAt: String,
    val onSale: Boolean
) {
    constructor() : this("", "", 0, "", true)
}

class BoardFragment : Fragment(R.layout.fragment_board) {
    private lateinit var mBinding: FragmentBoardBinding
    private lateinit var boardPostAdapter: BoardPostAdapter
    private lateinit var boardPostDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private var postList = mutableListOf<PostModel>()

    private lateinit var boardIsSaleTextView: TextView

    private var saleMode = true

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBoardBinding = FragmentBoardBinding.bind(view)
        mBinding = fragmentBoardBinding

        boardPostDB = Firebase.database.reference.child(DB_POST)
        userDB = Firebase.database.reference.child(DB_USERS)
        boardPostAdapter = BoardPostAdapter()

        fragmentBoardBinding.boardPostRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentBoardBinding.boardPostRecyclerView.adapter = boardPostAdapter

        boardPostDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()

                for (data in snapshot.children.reversed()) {
                    val p =  data.getValue(PostModel::class.java)
                    Log.d(TAG, "onChildAdded: $p")

                    if (p != null) {
                        if (saleMode) {
                            postList.add(p)
                            boardPostAdapter.submitList(postList.toList())
                        } else if (p.onSale) {
                            postList.add(p)
                            boardPostAdapter.submitList(postList.toList())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 취소
            }
        })

        boardIsSaleTextView = mBinding.boardIsSaleTextView

        boardIsSaleTextView.setOnClickListener {
            if (saleMode) {
                saleMode = false
                updateData()
                boardIsSaleTextView.text = "판매 완료 물품도 보기"
            }
            else {
                saleMode = true
                updateData()
                boardIsSaleTextView.text = "판매 완료 물품 제외"
            }
        }
    }

    // 데이터 업데이트 메서드 수정
    private fun updateData() {
        postList.clear()

        boardPostDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val p = data.getValue(PostModel::class.java)
                    Log.d(TAG, "onChildAdded: $p")

                    if (p != null) {
                        if (saleMode) {
                            postList.add(p)
                        } else {
                            if (p.onSale) {
                                postList.add(p)
                            }
                        }
                    }
                }

                boardPostAdapter.submitList(postList.toList())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching data", error.toException())
            }
        })
    }


    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        boardPostAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "BoardFragment"
    }
}
