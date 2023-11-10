package com.example.quack_market.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quack_market.databinding.FragmentBoardBinding

class BoardFragment : Fragment() {
    private lateinit var mBinding: FragmentBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentBoardBinding.inflate(inflater, container, false)

        return mBinding.root
    }
}