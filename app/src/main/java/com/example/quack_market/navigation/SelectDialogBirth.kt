package com.example.quack_market.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.quack_market.databinding.DialogBirthSelectBinding

class SelectDialogBirth: DialogFragment() {
    private lateinit var buttonClickListener: OnButtonClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mBinding = DialogBirthSelectBinding.inflate(inflater, container, false)

        val pickerYear = mBinding.pickerYear
        pickerYear.minValue = 1900
        pickerYear.maxValue = 2023
        pickerYear.value = 2004

        val pickerMonth = mBinding.pickerMonth
        pickerMonth.minValue = 1
        pickerMonth.maxValue = 12

        val pickerDay = mBinding.pickerDay
        pickerDay.minValue = 1

        pickerMonth.setOnValueChangedListener{ _, _, _ ->
            updatePickerDays(pickerYear, pickerMonth, pickerDay)
        }

        pickerYear.setOnValueChangedListener{ _, _, _ ->
            updatePickerDays(pickerYear, pickerMonth, pickerDay)
        }

        updatePickerDays(pickerYear, pickerMonth, pickerDay)

        mBinding.btnApply.setOnClickListener {
            val year = pickerYear.value
            val month = pickerMonth.value
            val day = pickerDay.value

            buttonClickListener.onBtnApplyClicked(year, month, day)
            dismiss()
        }

        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        return mBinding.root
    }

    private fun updatePickerDays(pickerYear: NumberPicker,
                                 pickerMonth: NumberPicker,
                                 pickerDay: NumberPicker) {
        when(pickerMonth.value){
            1, 3, 5, 7, 8, 10, 12 -> pickerDay.maxValue = 31
            4, 6, 9, 11 -> pickerDay.maxValue = 30
            2 -> {
                if((pickerYear.value %  4) == 0
                    && (pickerYear.value % 100) != 0
                    || (pickerYear.value) % 400 == 0){
                    pickerDay.maxValue = 29
                } else {
                    pickerDay.maxValue = 28
                }
            }
        }
    }

    interface OnButtonClickListener{
        fun onBtnApplyClicked(pickerYear: Int,
                              pickerMonth: Int,
                              pickerDay: Int)
    }

    fun setBtnClickListener(buttonClickListener: OnButtonClickListener){
        this.buttonClickListener = buttonClickListener
    }
}