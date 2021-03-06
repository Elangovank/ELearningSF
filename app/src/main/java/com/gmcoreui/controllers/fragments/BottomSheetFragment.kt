package com.gmcoreui.controllers.fragments

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gm.R
import kotlinx.android.synthetic.main.default_fragment_bottom_sheet_item.view.*
import kotlinx.android.synthetic.main.default_fragment_item_list_dialog.*




/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    BottomSheetFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [BottomSheetFragment.Listener].
 */
class BottomSheetFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null
    private var dataSource: BottomSheetDataSource? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.default_fragment_item_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, arguments: Bundle?) {
        super.onViewCreated(view, arguments)
        list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        list.adapter = ItemAdapter(arguments?.getInt(ARG_ITEM_COUNT) ?: 1)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
            dataSource = parent as BottomSheetDataSource
        } else {
            mListener = context as Listener
            dataSource = context as BottomSheetDataSource
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onItemClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : androidx.recyclerview.widget.RecyclerView.ViewHolder(inflater.inflate(R.layout.default_fragment_bottom_sheet_item, parent, false)) {

        internal val text: TextView = itemView.text

        init {
            text.setOnClickListener {
                mListener?.let {
                    it.onItemClicked(adapterPosition)
                    dismiss()
                }
            }
        }
    }

    private inner class ItemAdapter internal constructor(private val mItemCount: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            dataSource?.let {
                holder.itemView.text.text = it.getLabel(position)
                if (it.getImageResource(position) != -1) {
                    holder.itemView.image.setImageResource(it.getImageResource(position))
                } else {
                    holder.itemView.image.visibility = View.VISIBLE
                }
            }

        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }

    companion object {
        const val ARG_ITEM_COUNT = "item_count"

        fun newInstance(itemCount: Int): BottomSheetFragment =
                BottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                    }
                }
    }

    interface BottomSheetDataSource {
        fun getImageResource(index: Int): Int
        fun getLabel(index: Int): String
    }

}
