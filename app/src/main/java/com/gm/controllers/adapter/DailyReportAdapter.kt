package com.gm.controllers.adapter


import android.R.attr.maxLength
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.DailyReportFragment
import com.gm.controllers.fragments.DailyReportFragment.Companion.modifiedfeedBackList
import com.gm.listener.OnItemClickListener
import com.gm.listener.OnItemSelectedListenerFeedback
import com.gm.models.Model
import com.gmcoreui.controllers.BaseActivity
import com.gmcoreui.controllers.ui.GMSpinner
import kotlinx.android.synthetic.main.item_dailyreport_list.view.*
import kotlinx.android.synthetic.main.item_dailyreport_list.view.feedbackItemIcon
import kotlinx.android.synthetic.main.layout_feedconsumption.view.*


class DailyReportAdapter(var feedBackList: ArrayList<Model.FeedBack>,
                         private var onItemClickListener: OnItemClickListener, private var context: Context, private var reasonResponse: Model.DailyReportDetails, private var listener: OnItemSelectedListenerFeedback,
                         var fragment: DailyReportFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val INCREMENT = 1
    val DECREMENT = 2
    var reasonsList = ArrayList<Model.Reasons>()
    var feedConsumptionSelectedReason: Long? = 0
    var submitValue = false
    var numberList = ArrayList<Double>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dailyreport_list, parent, false))
    }

    override fun getItemCount(): Int {
        return feedBackList.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        reasonsList.clear()
        numberList.clear()
        for (j in 0..100) {
            var num = j.toDouble()
            for (i in 0..4) {
                num = num.plus(0.25)
                numberList.add(num)
            }
        }
        var reason = Model.Reasons()
        reason.text = "Select the reason"
        reason.value = 0
        reasonsList.add(reason)
        reasonResponse.reasons?.let { reasonsList.addAll(it) }
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            val spanned = (dest ?: "").toString()
            if (dest.isNotEmpty() && source.isNotEmpty()) {
                val cursorPosition = holder.itemView.quantityEditText.selectionStart
                val data = (spanned.substring(0, cursorPosition) + source + spanned.substring(cursorPosition)).toDoubleOrNull()
                        ?: 0.0

            }
            return@InputFilter null
        }

        if (feedBackList[position].isNeedToValidate!!) {
            holder.itemView.quantityEditText?.setEnabled(false);
            holder.itemView.quantityEditText.setTextColor(context?.getColor(R.color.black))
        }
        reasonResponse?.reasons?.let { loadSpinnerData(it, holder.itemView.spinner) }
        holder.itemView.feedbackTypetextView.text = feedBackList[position].feedName
        holder.itemView.feedbackQuantityTypetextView.text = feedBackList[position].feedBackCount
        // var reasonResponse
        if (feedBackList[position].increamentValue == reasonResponse?.dailySupport?.toleranceIncrementDecrementValue) {
            holder.itemView.quantityEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            holder.itemView.quantityEditText.filters = arrayOf(InputFilter.LengthFilter(7), filter)
            if (feedBackList.get(position)?.isMorality!!) {
                holder.itemView.quantityEditText.setText("" + feedBackList[position].count?.toInt())
            } else {
                holder.itemView.quantityEditText.setText("" + feedBackList[position].count)
            }
        } else {
            holder.itemView.quantityEditText.inputType = InputType.TYPE_CLASS_NUMBER
            if (feedBackList[position].feedName == fragment.getResourceString("feed_consumption")) {
                val maxLength = 4
                holder.itemView.quantityEditText?.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
            }
            if (feedBackList.get(position)?.isMorality!!) {
                holder.itemView.quantityEditText.setText("" + feedBackList[position].count?.toInt())
            } else {
                holder.itemView.quantityEditText.setText("" + feedBackList[position].count)
            }
            if (feedBackList[position].feedName == fragment.getResourceString("feed_consumption"))
            {
                holder.itemView.quantityEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4))
                if(DailyReportFragment.feedConsumptionUnits==1)
                {
                    holder.itemView.quantityEditText.setText("" + feedBackList[position].count?.toInt())
                }
            }
            else
                holder.itemView.quantityEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))


        }

        holder.itemView.feedbackItemIcon.setImageResource(feedBackList[position].iconDrawableId)
        listener.onItemSelected(holder.itemView.spinner, 0, 0)
        if (feedBackList.get(position).disableThefield == false) {
            holder.itemView.quantityEditText?.setFocusable(false)
            holder.itemView.quantityEditText?.setEnabled(false);
        }
        holder.itemView.incrementImageView.setOnClickListener {
            if (reasonResponse?.isFeedStandardAvailable == true) {
                if (feedBackList.get(position).disableThefield == true)
                    updateValue(INCREMENT, holder.itemView, position)
            }

        }
        holder.itemView.decrementImageView.setOnClickListener {
            if (reasonResponse?.isFeedStandardAvailable == true) {
                if (feedBackList.get(position).disableThefield == true)
                    updateValue(DECREMENT, holder.itemView, position)
            }
        }


        holder.itemView.quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (feedBackList[position].feedName == fragment.getResourceString("feed_consumption") && s.isNotEmpty()) {
                    var inputValue=0
                    if(DailyReportFragment.feedConsumptionUnits!=1)
                    {
                        if (s.toString().toDouble() <= feedBackList[position].maxValue?.let { reasonResponse.feedKg?.plus(it) }!! && s.toString().toDouble() >= feedBackList[position].minValue?.let { reasonResponse.feedKg?.minus(it) }!!)
                            Log.d("isToleranceAllowed", "toleranceLimit accepted")
                        else
                        {
                            holder.itemView.quantityEditText.setError(fragment.getResourceString("error_message_tolerance_limit") +" "+feedBackList[position].minValue?.let { reasonResponse.feedKg?.minus(it) }!! +" - "+feedBackList[position].maxValue?.let { reasonResponse.feedKg?.plus(it) }!!)
                        }
                    }
                    else
                    {
                        if(s.toString().contains("."))
                            inputValue=s.toString().split(".").get(0).toInt()
                        else
                            inputValue=s.toString().toInt()
                        if (inputValue <= feedBackList[position].maxValue?.let { reasonResponse.feedKg?.plus(it) }!! && inputValue >= feedBackList[position].minValue?.let { reasonResponse.feedKg?.minus(it) }!!)
                            Log.d("isToleranceAllowed", "toleranceLimit accepted")
                        else
                        {
                            holder.itemView.quantityEditText.setError(fragment.getResourceString("error_message_tolerance_limit") +feedBackList[position].minValue?.let { reasonResponse.feedKg?.minus(it) }!! +" - "+feedBackList[position].maxValue?.let { reasonResponse.feedKg?.plus(it) }!!)
                        }
                    }
                }
                if (!feedBackList.get(position)?.isMorality!!) {
                    val input = s.toString()
                    if (input.contains(".") && s.get(s.length - 1) !== '.') {
                        if (input.indexOf(".") + 3 <= input.length - 1) {
                            val formatted = input.substring(0, input.indexOf(".") + 3)
                            holder.itemView.quantityEditText.setText(formatted)

                            holder.itemView.quantityEditText.setSelection(formatted.length)
                        }
                    } else if (input.contains(",") && s.get(s.length - 1) !== ',') {
                        if (input.indexOf(",") + 3 <= input.length - 1) {
                            val formatted = input.substring(0, input.indexOf(",") + 3)
                            holder.itemView.quantityEditText.setText(formatted)
                            holder.itemView.quantityEditText.setSelection(formatted.length)
                        }
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    feedBackList[position].count = holder.itemView.quantityEditText.text.toString().toDoubleOrNull()
                            ?: 0.0
                    modifiedfeedBackList[position].count = feedBackList[position].count
                } else {
                    feedBackList[position].count = 0.0
                    modifiedfeedBackList[position].count = feedBackList[position].count
                }
                if (feedBackList[position].isNeedToValidate ?: false &&
                        reasonResponse.feedKg != holder.itemView.quantityEditText.text.toString().toDoubleOrNull()) {
                    holder.itemView.feedConsumptionResonLayout.visibility = View.VISIBLE
                } else {
                    holder.itemView.feedConsumptionResonLayout.visibility = View.GONE
                }
            }
        })
        if (reasonResponse?.isFeedStandardAvailable == false) {
            holder.itemView.quantityEditText?.setFocusable(false)
            holder.itemView.quantityEditText?.setEnabled(false);
        } else {
            holder.itemView.quantityEditText?.setFocusable(true)
            holder.itemView.quantityEditText?.setEnabled(true);
        }
    }

    fun setErrorMessage(editText: EditText, message: String) {
        editText.error = message
        editText.requestFocus()
    }


    private fun updateValue(FLAG: Int, holderView: View, position: Int) {
        var MAX_INC_VALUE = 999
        var MAX_INC_DEC_VALUE = 999.5

        if (!holderView.quantityEditText.text.isNullOrEmpty()) {
            val currentValue = holderView.quantityEditText.text.toString().toDoubleOrNull()
                    ?: 0.0
            if (FLAG == INCREMENT) {
                if (feedBackList[position].feedName == fragment.getResourceString("feed_consumption")) {
                    MAX_INC_DEC_VALUE = 9999.5
                    MAX_INC_VALUE = 9999
                }
                if (feedBackList[position].increamentValue == reasonResponse?.dailySupport?.toleranceIncrementDecrementValue) {
                    if (currentValue < MAX_INC_DEC_VALUE) {
                        if (currentValue < reasonResponse.feedKg?.let { feedBackList[position].maxValue }?.plus(reasonResponse?.feedKg
                                        ?: 0.0)!! && feedBackList[position].isNeedToValidate!!) {
                            holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.plus(it).toString() })
                        } else if (!feedBackList[position].isNeedToValidate!!) {
                            if (feedBackList.get(position)?.isMorality!!) {
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.toInt()?.let { currentValue.toInt().plus(it).toString() })
                            } else {
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.plus(it).toString() })
                            }
                        }

                    }
                } else {
                    if (currentValue < MAX_INC_VALUE) {
                        if (currentValue < reasonResponse.feedKg?.let { feedBackList[position].maxValue }?.plus(reasonResponse?.feedKg
                                        ?: 0.0)!! && feedBackList[position].isNeedToValidate!!) {
                            if(DailyReportFragment.feedConsumptionUnits==1)
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.plus(it).toInt().toString() })
                            else
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.plus(it).toString() })
                         //   holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.plus(it).toString() })
                        } else if (!feedBackList[position].isNeedToValidate!!) {
                            if (feedBackList.get(position)?.isMorality!!) {
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.toInt()?.let { currentValue.toInt().plus(it).toString() })
                            } else {
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.plus(it).toString() })
                            }
                        }
                    }
                }
            } else {
                if (currentValue > 0) {
                    if (feedBackList[position].increamentValue == reasonResponse?.dailySupport?.toleranceIncrementDecrementValue) {
                        if (currentValue > feedBackList[position].minValue?.let { reasonResponse?.feedKg?.minus(it) }!! && feedBackList[position].isNeedToValidate!!) {
                            if(DailyReportFragment.feedConsumptionUnits==1)
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.minus(it).toInt().toString() })
                            else
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.minus(it).toString() })
                        } else if (!feedBackList[position].isNeedToValidate!!) {
                            if (feedBackList.get(position)?.isMorality!!) {
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.toInt()?.let { currentValue.toInt().minus(it).toString() })
                            } else {
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.minus(it).toString() })
                            }

                        }

                    } else {
                        if (currentValue > feedBackList[position].minValue?.let { reasonResponse?.feedKg?.minus(it) }!! && feedBackList[position].isNeedToValidate!!) {
                            if(DailyReportFragment.feedConsumptionUnits==1)
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.minus(it).toInt().toString() })
                            else
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.let { currentValue.minus(it).toString() })
                        } else if (!feedBackList[position].isNeedToValidate!!) {
                            if (feedBackList.get(position)?.isMorality!!) {
                                holderView.quantityEditText.setText(feedBackList[position].increamentValue?.toInt()?.let { currentValue.toInt().minus(it).toString() })
                            } else {
                                holderView.quantityEditText.setText(
                                        feedBackList[position].increamentValue?.let { currentValue.minus(it).toString() }
                                )
                            }
                        }
                    }
                }
            }
            feedBackList[position].count = holderView.quantityEditText.text.toString().toDoubleOrNull()
            modifiedfeedBackList[position].count = feedBackList[position].count
            holderView.quantityEditText.setSelection(holderView.quantityEditText.text.toString().length)

        } else {
            holderView.quantityEditText.setText(0.toString())
            holderView.quantityEditText.setSelection(1)
        }
    }


    fun loadSpinnerData(spinnerArray: java.util.ArrayList<Model.Reasons>, spinnerName: GMSpinner?) {
        val str = ArrayList<String>()
        str.add(fragment.getResourceString("message_select_reason"))
        spinnerArray.forEach {
            str.add(it.text ?: "")
        }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, str)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerName?.setAdapter(adapter)
        spinnerName?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                feedConsumptionSelectedReason = reasonsList.get(position).value
                onItemClickListener.onItemSelected(feedConsumptionSelectedReason, position)

            }

        })

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.feedbackItemIcon
            itemView.feedbackTypetextView
            itemView.feedbackQuantityTypetextView
            itemView.incrementImageView
            itemView.decrementImageView
            itemView.quantityEditText
            itemView.feedConsumptionResonLayout
            itemView.spinner
        }

    }


}