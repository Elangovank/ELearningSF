package com.gm.controllers.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import kotlinx.android.synthetic.main.empty_view.view.*
import kotlinx.android.synthetic.main.progress_view.view.*

/**
 * Created by saravanan on 26/02/19.

YourAdapter must extends PagingAdapter

how to use:

YourAdapter(list)
.setPagingEnabled(true)    ## false for enable pagination (default is false)
.setLoadingListener(object : LoadMoreListener {
override fun loadMoreItems() {
generateData()
}
}).into(recyclerViewId,layoutManager (optional , default LinearLayoutManager))

 */

abstract class PagingAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isEnd = false
    val PROGRESS_VIEW = 0
    val ITEM_VIEW = 1
    val EMPTY_VIEW = 2
    var listener: LoadMoreListener? = null

    override fun getItemViewType(position: Int): Int {
        if (getCount() == 0 && itemCount == 1) {
            return EMPTY_VIEW
        } else if (getCount() == position && !isEnd) {
            return PROGRESS_VIEW
        } else {
            return ITEM_VIEW
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if (p1 == EMPTY_VIEW) {
            return EmptyViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.empty_view, p0, false))
        } else if (p1 == PROGRESS_VIEW) {
            return ProgressViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.progress_view, p0, false))
        } else {
            return onCreateHolder(p0, p1)
        }
    }

    override fun getItemCount(): Int {
        if (getCount() == 0) {
            return 1
        } else {
            if (isEnd) {
                return getCount()
            } else {
                return getCount() + 1
            }
        }
    }

    /*true for enable and false for disable pagination */
    fun setPagingEnabled(status: Boolean): PagingAdapter<VH> {
        isEnd = !status
        return this
    }

    /*call this method to pagination end*/
    fun setReachedEnd() {
        isEnd = true
        Handler().postDelayed({
            if (itemCount > 0)
                this.notifyItemRemoved(itemCount)
            else
                this.notifyDataSetChanged()
        }, 1)
    }

    fun setLoadingListener(tempListener: LoadMoreListener): PagingAdapter<VH> {
        listener = tempListener
        return this
    }

    fun into(recyclerView: RecyclerView?, layoutManager: RecyclerView.LayoutManager? = null) {
        recyclerView?.adapter = this
        if (layoutManager == null) {
            if (recyclerView?.layoutManager == null) {
                recyclerView?.context?.let { context ->
                    recyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        } else {
            recyclerView?.layoutManager = layoutManager
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (p0.itemViewType == EMPTY_VIEW) {
           // p0.itemView.emptyTextView?.text=
            p0.itemView.emptyTextView?.let {
                setEmptyViewText(p0.itemView.emptyTextView)
            }
        } else if (p0.itemViewType == ITEM_VIEW) {
            onBindHolder(p0, p1)
        } else if (p0.itemViewType == PROGRESS_VIEW) {
            if (listener != null) {
                listener?.loadMoreItems()
            }
        }
    }

    abstract fun onCreateHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder
    abstract fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int)
    abstract fun getCount(): Int
    abstract fun setEmptyViewText(textView: TextView)


    class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.loadingProgress
        }
    }

    class EmptyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        init {
            itemView.emptyTextView
        }
    }
}

interface LoadMoreListener {
    fun loadMoreItems()
}