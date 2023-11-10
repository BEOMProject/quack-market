package com.example.quack_market.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quack_market.databinding.FragmentUploadBinding

class UploadFragment : Fragment() {
    private lateinit var mBinding: FragmentUploadBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentUploadBinding.inflate(inflater, container, false)

        return mBinding.root
    }
}