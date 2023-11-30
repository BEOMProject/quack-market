package com.example.quack_market.navigation

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quack_market.ChatRoomActivity
import com.example.quack_market.R
import com.example.quack_market.databinding.FragmentSalespostBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class SalesPostFragment : Fragment(R.layout.fragment_salespost) {

    private var _binding: FragmentSalespostBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_POST_MODEL = "postModel"

        fun newInstance(postModel: PostModel): SalesPostFragment {
            val fragment = SalesPostFragment()
            val args = Bundle()
            args.putParcelable(ARG_POST_MODEL, postModel)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSalespostBinding.bind(view)

        val auth: FirebaseAuth = Firebase.auth
        val currentUser = auth.currentUser

        val postModel: PostModel? = arguments?.getParcelable(ARG_POST_MODEL)

        if (postModel != null) {
            binding.saleName.text = postModel.title

            val decimal = DecimalFormat("#,###")
            binding.priceChange.text = decimal.format(postModel.price).toString()+"원"

            if (postModel.onSale) {
                binding.saleStatus.text = "판매 중"
            }
            else {
                binding.saleStatus.text = "판매 완료"
            }

            binding.saleInfo.text = postModel.description

            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(postModel.createdAt)
            val formatDate = date?.let { SimpleDateFormat("yyyy.MM.dd HH:mm 등록", Locale.getDefault()).format(it) }
            binding.uploadDate.text = formatDate

            if (postModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.imageView2)
                    .load(postModel.imageUrl)
                    .into(binding.imageView2)
            }

            if (currentUser != null) {
                val uid = currentUser.uid
                if (uid == postModel.sellerId) {
                    binding.saleComplete.text = "수정하기"
                }
                else {
                    binding.saleComplete.text = "채팅 보내기"
                    binding.saleComplete.setOnClickListener {
                        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
                        intent.putExtra("sellerUid", postModel.sellerId)
                        intent.putExtra("image",postModel.imageUrl)
                        intent.putExtra("title",postModel.title)
                        intent.putExtra("price",postModel.price)
                        startActivity(intent)
                    }
                }
            } else {
                binding.saleComplete.text = "로그인 필요"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

