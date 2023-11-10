package com.example.quack_market.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quack_market.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {
    private lateinit var mBinding: FragmentMessageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMessageBinding.inflate(inflater, container, false)

        return mBinding.root
    }
}