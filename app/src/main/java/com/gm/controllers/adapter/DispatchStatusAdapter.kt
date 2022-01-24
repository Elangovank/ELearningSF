package com.gm.controllers.adapter


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.TodayMaterialFragment
import com.gm.listener.OnGetCallListener
import com.gm.listener.OnItemClickListener

import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_dispatch_status.view.*



class DispatchStatusAdapter(private var materialArrivalList: ArrayList<Model.MaterialArrivalAdapter>, var listener: OnItemClickListener,var callListener:OnGetCallListener,
                            var context: Context,var fragment: TodayMaterialFragment)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val INCREMENT = 1
    val DECREMENT = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dispatch_status, parent, false))
    }

    override fun getItemCount(): Int {
        return materialArrivalList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = materialArrivalList.get(position)
        holder.itemView.itemsTextView.text=fragment.getResourceString("label_item")
        holder.itemView.phonNumberTextView.text=fragment.getResourceString("driver_no")
        holder.itemView.itemTextView.text=fragment.getResourceString("label_item")
        holder.itemView.estimatedArrivalTextView.text=fragment.getResourceString("label_estimated_arrival")
        holder.itemView.dispatchTextView.text=fragment.getResourceString("label_dispatched_qty")
        holder.itemView.commentEditText.hint=fragment.getResourceString("message_enter_comment")
        holder.itemView.submitTextView.text=fragment.getResourceString("submit")
        holder.itemView.vehicleNumberTextView.text = item.vehicleNumber
        holder.itemView.arrivalTimeTextView.text = DateUtils.toDisplayTimeMaterial(item.arrivalDate)

        var ti=DateUtils.toDisplayTimeMaterialAM(item.arrivalDate)
        if (ti.trim().equals("AM"))
        {
           holder.itemView.timeTextView?.text=" ".plus(fragment?.getResourceString("am"))
        }else{
            holder.itemView.timeTextView?.text=" ".plus(fragment?.getResourceString("pm"))
        }
        holder.itemView.materialNameTextView.text = item.materialName.plus("-").plus(item.item)
        holder.itemView.materialQtyTextView.text = item.dispatchedQuantity.toString().plus(" ").plus(item.UOM)
        item.arrivalQuantity?.value=item.dispatchedQuantity
        holder.itemView.quantityEditText.setText((item.arrivalQuantity?.value ?: 0).toString())

        item.getArrivalQty()?.observeForever {
            holder.itemView.quantityEditText.setText(it.toString())
        }

        /*      holder.itemView.quantityEditText?.onChange {
                  if (it.isEmpty()) {
                      item.arrivalQuantity?.value = 0L
                  } else {
                      item.arrivalQuantity?.value = it.toLong()
                  }
              }*/


        holder.itemView.commentEditText?.setText(item.comments?.value ?: "")


        item.getComments()?.observeForever {
            holder.itemView.commentEditText?.setText(it)
        }
        /*  holder.itemView.commentEditText?.onChange {
              item.comments?.value = it
          }*/

       holder.itemView.phonNumberTextView?.setOnClickListener {
           callListener.onItemSelected(item.phoneNumber)
       }

        holder.itemView.incrementImageView.setOnClickListener {
            if (item.arrivalQuantity?.value != null) {
                item.arrivalQuantity?.value = (holder.itemView.quantityEditText.text.toString().toLong()
                        ?: 0L) + 1L
                holder.itemView.quantityEditText.setSelection(holder.itemView.quantityEditText.text.length)
            } else {
                item.arrivalQuantity?.value = 0L
            }
        }
        holder.itemView.decrementImageView.setOnClickListener {
            holder.itemView.quantityEditText?.tag = false
            item.arrivalQuantity?.value = (holder.itemView.quantityEditText.text.toString().toLong())
            if (item.arrivalQuantity?.value != null && item.arrivalQuantity?.value != 0L) {
                item.arrivalQuantity?.value = (holder.itemView.quantityEditText.text.toString().toLong()
                        ?: 0L) - 1L
                holder.itemView.quantityEditText.setSelection(holder.itemView.quantityEditText.text.length)
            } else {
                item.arrivalQuantity?.value = 0L
            }
        }

        fun EditText.placeCursorToEnd() {
            this.setSelection(this.text.length)
        }

        holder.itemView.submitTextView.setOnClickListener {
            item.comments?.value=holder.itemView.commentEditText.text.toString()
            item.arrivalQuantity?.value = holder.itemView.quantityEditText.text.toString().toLong()
            listener.onItemSelected(item, position)

        }
    }

    private fun EditText.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cb(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                materialArrivalList
            }
        })
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.incrementImageView
            itemView.decrementImageView
            itemView.vehicleNumberTextView
            itemView.arrivalTimeTextView
            itemView.materialNameTextView
            itemView.materialQtyTextView
            itemView.quantityEditText
            itemView.submitTextView
            itemView.phonNumberTextView
            itemView.timeTextView
            itemView.itemsTextView
            itemView.phonNumberTextView
            itemView.estimatedArrivalTextView
            itemView.itemTextView
            itemView.dispatchTextView
            itemView.commentEditText
        }

    }


    private fun updateValue(FLAG: Int, holderView: View, position: Int) {
        if (!holderView.quantityEditText.text.toString().isNullOrEmpty()) {
            var currentValue = holderView.quantityEditText.text.toString().toInt()
            if (FLAG == INCREMENT) {
                holderView.quantityEditText.setText(currentValue.plus(1).toString())
            } else {
                if (currentValue > 0) {
                    holderView.quantityEditText.setText(currentValue.minus(1).toString())
                }
            }
            //  feedBackList[position].count = holderView.quantityEditText.text.toString().toInt()
            // modifiedfeedBackList[position].count = feedBackList[position].count
            holderView.quantityEditText.setSelection(holderView.quantityEditText.text.toString().length)

        } else {
            holderView.quantityEditText.setText(0.toString())
            holderView.quantityEditText.setSelection(1)
        }
    }
}