package com.example.quack_market.navigation

import android.os.Bundle
import android.util.Log
import android.view.View
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
    val createdAt: String
) {
    constructor() : this("", "", 0, "")
}

class BoardFragment : Fragment(R.layout.fragment_board) {
    private lateinit var mBinding: FragmentBoardBinding
    private lateinit var boardPostAdapter: BoardPostAdapter
    private lateinit var boardPostDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private val postList = mutableListOf<PostModel>()

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val postModel = snapshot.getValue(PostModel::class.java)
            postModel?.let {
                Log.d(TAG, "onChildAdded: $postModel")

                if (!postList.contains(it)) {
                    postList.add(it)
                    boardPostAdapter.submitList(postList.toList())
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            // 변경 사항이 있는 경우 처리
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            // 삭제된 경우 처리
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            // 이동된 경우 처리
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e(TAG, "값을 읽어오지 못했습니다.", error.toException())
        }
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

        boardPostDB.addChildEventListener(listener)
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        boardPostAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        boardPostDB.removeEventListener(listener)
    }

    companion object {
        private const val TAG = "BoardFragment"
    }
}
