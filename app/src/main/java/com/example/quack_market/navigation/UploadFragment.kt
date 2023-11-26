package com.example.quack_market.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quack_market.R
import com.example.quack_market.databinding.FragmentUploadBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadFragment : Fragment() {
    private lateinit var mBinding: FragmentUploadBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var imageStorageRef: StorageReference
    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val userId = Firebase.auth.currentUser?.uid
        mBinding = FragmentUploadBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        database = Firebase.database.getReference("post")
        storage = Firebase.storage
        imageStorageRef = storage.reference.child("images")

        mBinding.registerButton.setOnClickListener {
            val createdAt = getCurrentDateTime()
            database.child("$userId").child("name").get().addOnSuccessListener { snapshot ->
                val sellerId = snapshot.value.toString()
                val priceText = mBinding.editPrice.text.toString()
                val title = mBinding.editTitle.text.toString()
                val onSale = true
                val description = mBinding.editExplain.text.toString()
                val uid = Firebase.auth.currentUser?.uid

                if (priceText.isNotEmpty() && title.isNotEmpty() && description.isNotEmpty() && selectedImageUri != null) {
                    val price = priceText.toDouble()
                    saveDataToFirebase(
                        database,
                        uid,
                        createdAt,
                        description,
                        onSale,
                        price,
                        sellerId,
                        title
                    )
                    Toast.makeText(context, "게시글 등록 완료", Toast.LENGTH_SHORT).show()
                    resetInputFields()
                } else {
                    Toast.makeText(context, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                // 데이터 가져오기 실패 시 처리할 내용 추가
            }
        }


        mBinding.imageButton.setOnClickListener {
            openGallery()
        }

        return mBinding.root
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            mBinding.imageButton.setImageURI(selectedImageUri)
        }
    }

    private fun saveDataToFirebase(
        database: DatabaseReference,
        uid: String?,
        createdAt: String,
        description: String,
        onSale: Boolean,
        price: Double,
        sellerId: String,
        title: String
    ) {
        val imageRef = imageStorageRef.child("$uid/${System.currentTimeMillis()}.jpg")
        val uploadTask = imageRef.putFile(selectedImageUri!!)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                val postData = HashMap<String, Any>()
                postData["createdAt"] = createdAt
                postData["description"] = description
                postData["imageUrl"] = downloadUrl
                postData["onSale"] = onSale
                postData["price"] = price
                postData["sellerId"] = Firebase.auth.currentUser?.uid.toString()
                postData["title"] = title

                val postKey = database.push().key
                if (postKey != null) {
                    database.child(postKey).setValue(postData)
                }
            } else {
                // 이미지 업로드 실패 시 처리할 내용 추가
            }
        }
    }
    private fun resetInputFields() {
        mBinding.editPrice.text = null
        mBinding.editTitle.text = null
        mBinding.editExplain.text = null
        selectedImageUri = null
        mBinding.imageButton.setImageResource(android.R.drawable.ic_menu_gallery)
    }
}
