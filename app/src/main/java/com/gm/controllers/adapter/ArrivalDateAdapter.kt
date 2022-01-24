package com.gm.controllers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.TodayMaterialFragment
import com.gm.listener.OnGetCallListener
import com.gm.listener.OnItemClickListener
import com.gm.listener.OnItemSelectedListener
import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_text.view.*
import kotlinx.android.synthetic.main.layout_today_material_arraival.*
import kotlinx.android.synthetic.main.layout_today_material_arraival.view.*

class ArrivalDateAdapter(private var optionList: ArrayList<Model.MaterialArrivalPending>, var context: Context,
                         var listener: OnItemClickListener,var callListener: OnGetCallListener,var fragment:TodayMaterialFragment)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var materialAdapter = ArrayList<Model.MaterialArrivalAdapter>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_today_material_arraival, parent, false))
    }

    override fun getItemCount(): Int {
        return optionList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item=optionList.get(position)
        holder.itemView.arrivalStatusTextView.text=fragment.getResourceString("label_in_transit")
       holder.itemView.arrivalDateTextView.text=DateUtils.toDisplayDate(item.key)
        item?.value?.let { initAdapter(it,holder.itemView.dispatchStatusRecyclerView) }
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.arrivalDateTextView
            itemView.dispatchStatusRecyclerView
            itemView.arrivalStatusTextView
        }
    }

    fun initAdapter(materialList: ArrayList<Model.MaterialArrival>,dispatchStatusRecyclerView:RecyclerView) {
        materialAdapter.clear()
        materialList.forEach { data ->
            val matModel = Model.MaterialArrivalAdapter().apply {
                materialArrivalId = data.materialArrivalId
                vehicleNumber = data.vehicleNumber
                arrivalDate = data.arrivalDate
                expectedTime = data.expectedTime
                UOM = data.UOM
                arrivalStatus = data.arrivalStatus
                materialName = data.materialName
                arrivalQuantity = MutableLiveData()
                dispatchedQuantity = data.dispatchedQuantity
                phoneNumber=data.phoneNumber
                item=data.item
                comments = MutableLiveData()

            }
            matModel.arrivalQuantity?.value = (data.arrivalQuantity ?: 0L)
            materialAdapter.add(matModel)
        }

        dispatchStatusRecyclerView?.adapter = DispatchStatusAdapter(materialAdapter,listener,callListener,context,fragment)
          /*  override fun onItemSelected(item: Any?, selectedIndex: Int) {
                if (item is Model.MaterialArrivalAdapter) {
                    if (item.arrivalQuantity?.value != 0.toLong()) {
                        val list = Model.DispatchArrivalDetailsList()
                        list.materialArrivalId = item.materialArrivalId
                        list.arrivalQuantity = item.arrivalQuantity?.value
                        list.comments = item.comments?.value
                        dispatchFragment.submitTodayMaterialUpdate(list)
                    } else {
                        showSnackBar("Enter the Arrival Quentity")
                    }

                }


            }*/




}
}