package com.example.quack_market.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quack_market.MainActivity
import com.example.quack_market.dao.PostModel
import com.example.quack_market.databinding.FragmentSalespostChangeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

class SalesPostChangeFragment(
    private val postModel: PostModel
) : Fragment() {
    private lateinit var mBinding: FragmentSalespostChangeBinding
    private val postDatabase = Firebase.database.getReference("post")

    companion object {
        private const val ARG_POST_MODEL = "postModel"

        fun newInstance(postModel: PostModel): SalesPostChangeFragment {
            val fragment = SalesPostChangeFragment(postModel)
            val args = Bundle()
            args.putParcelable(ARG_POST_MODEL, postModel)
            fragment.arguments = args
            return fragment
        }

        private const val PICK_IMAGE_REQUEST = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSalespostChangeBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        val decimal = DecimalFormat("#,###")
        mBinding.saleName.setText(postModel.title)
        mBinding.textChangePrice.setText(decimal.format(postModel.price))
        mBinding.uploadDate.text = postModel.createdAt
        mBinding.editTextTextMultiLine.setText(postModel.description)
        Glide.with(mBinding.imageView2).load(postModel.imageUrl)
            .into(mBinding.imageView2)
        setStatus(postModel.onSale)


        mBinding.imageView2.setOnClickListener {
            openGallery()
        }

        mBinding.buttonSoldOut.setOnClickListener {
            val updateHash: HashMap<String, Any> = HashMap()
            updateHash["onSale"] = !postModel.onSale
            postDatabase.child(postModel.postId).updateChildren(updateHash).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TOGGLE", "##################SUCCESS#################")
                    mActivity.changeFragment(MyPageFragment())
                }
            }
        }

        mBinding.buttonUpdate.setOnClickListener {
            val updateHash: HashMap<String, Any> = HashMap()
            val priceBeforeChange = mBinding.textChangePrice.text.toString()
            var changePrice: Int
            if(priceBeforeChange.contains(",")){
                changePrice = priceBeforeChange.replace(",", "").toInt()
            } else
                changePrice = priceBeforeChange.toInt()

            updateHash["price"] = changePrice
            updateHash["title"] = mBinding.saleName.text.toString()
            updateHash["description"] = mBinding.editTextTextMultiLine.text.toString()

            postDatabase.child(postModel.postId).updateChildren(updateHash).addOnCompleteListener{
                if (it.isSuccessful){
                    Log.d("UPDATE", "####################SUCCESS########################")
                    mActivity.changeFragment(MyPageFragment())
                }
            }
        }
        return mBinding.root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            val userId = Firebase.auth.currentUser?.uid
            val selectedImageUri: Uri? = data.data
            mBinding.imageView2.setImageURI(selectedImageUri)

            val timeStamp = SimpleDateFormat("yyMMdd_HHmmss").format(Date())
            val imageFileName = "image_$timeStamp.png"
            val imageStorage = Firebase.storage.reference.child("images")
            val storageRef = imageStorage.child("$userId/$imageFileName")
            val updateHash: HashMap<String, Any> = HashMap()
            updateHash["imageUrl"] = selectedImageUri!!.toString()
            storageRef.putFile(selectedImageUri).addOnSuccessListener {
                postDatabase.child(postModel.postId).updateChildren(updateHash).addOnCompleteListener{
                    Log.d("IMAGE_UPLOAD", "#################SUCCESS#################")
                }
            }
        }
    }

    private fun setStatus(status: Boolean) {
        if (!status) {
            mBinding.buttonSoldOut.text = "판매 중"
            mBinding.saleStatus.text = "판매 완료"
        } else {
            mBinding.buttonSoldOut.text = "판매 완료"
            mBinding.saleStatus.text = "판매 중"
        }
    }
}