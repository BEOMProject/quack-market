package com.example.quack_market.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quack_market.R
import com.example.quack_market.adapter.BoardPostAdapter
import com.example.quack_market.databinding.FragmentBoardBinding
import com.example.quack_market.navigation.DBKey.Companion.DB_POST
import com.example.quack_market.navigation.DBKey.Companion.DB_USERS
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class DBKey {
    companion object {
        const val DB_POST = "post"
        const val DB_USERS = "users"
    }
}

@SuppressLint("ParcelCreator")
data class PostModel(
    val title: String,
    val imageUrl: String,
    val price: Long,
    val createdAt: String,
    val description: String,
    val sellerId: String,
    val onSale: Boolean
) : Parcelable {
    constructor() : this("", "", 0, "", "", "",true)

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(imageUrl)
        dest.writeLong(price)
        dest.writeString(createdAt)
        dest.writeString(description)
        dest.writeString(sellerId)
        dest.writeByte(if (onSale) 1 else 0)
    }
}

class BoardFragment : Fragment(R.layout.fragment_board) {
    private lateinit var mBinding: FragmentBoardBinding
    private lateinit var boardPostAdapter: BoardPostAdapter
    private lateinit var boardPostDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private var postList = mutableListOf<PostModel>()

    private lateinit var boardIsSaleTextView: TextView

    private val userId = com.google.firebase.ktx.Firebase.auth.currentUser?.uid
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
        boardPostAdapter = BoardPostAdapter(this, object : BoardPostAdapter.OnPostItemClickListener {
            override fun onPostItemClick(postModel: PostModel) {
                showSalesPostFragment(postModel)
            }
        })

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

    private fun updateData() {
        postList.clear()

        boardPostDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children.reversed()) {
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

    private fun showSalesPostFragment(postModel: PostModel) {
        val salesPostFragment = SalesPostFragment.newInstance(postModel)

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, salesPostFragment)
        transaction.addToBackStack(null)
        transaction.commit()
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
