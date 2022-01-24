package com.gm.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.getTextOrThrow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.FeedbackRatingFragment
import com.gm.models.Model
import kotlinx.android.synthetic.main.item_feedback_rating.view.*

class FeedBackListAdapter(private var optionList: ArrayList<Model.FeedBackQuestionList>,var fragment:FeedbackRatingFragment )
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feedback_rating, parent, false))

    }

    companion object {
        var chickBirdRating = 0
        var feedRating = 0
        var medicineRating = 0
        var supportRating = 0
    }

    override fun getItemCount(): Int {
        return optionList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val iconList = holder.itemView.context.resources.obtainTypedArray(R.array.feedBackRatingList)
        val item = optionList.get(position)


       /* <string-array name="feedback_tamil">
        <item>@string/chick_bird</item>
        <item>@string/feed</item>
        <item>@string/medicine</item>
        <item>@string/title_farmer</item>
        </string-array>*/




        val tamilString =ArrayList<String>()
        tamilString.add(fragment?.getResourceString("chick_bird"))
        tamilString.add(fragment?.getResourceString("feed"))
        tamilString.add(fragment?.getResourceString("medicine"))
        tamilString.add(fragment?.getResourceString("title_farmer"))
        holder.itemView.feedbackCategory.setText(tamilString.get(position))
        holder.itemView.feedbackQuestionRecyclerView.visibility = View.GONE
        holder.itemView.rating?.text=fragment.getResourceString("rating")
        holder.itemView.feedbackCategoryImage.setCompoundDrawablesWithIntrinsicBounds(iconList.getDrawable(position), null, null, null)
        holder.itemView.birdRatingBarLayout.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (item.feedBackCategoryId?.toInt() == 1) {
                chickBirdRating = rating.toInt()
            } else if (item.feedBackCategoryId?.toInt() == 2) {
                feedRating = rating.toInt()
            } else if (item.feedBackCategoryId?.toInt() == 3) {
                medicineRating = rating.toInt()
            } else {
                supportRating = rating.toInt()
            }
            holder.itemView.feedbackQuestionRecyclerView.visibility = View.VISIBLE
            holder.itemView.rating.visibility = View.VISIBLE
            val list = ArrayList<Model.QuestionListDetails>()
            run breaker@{
                item.stars?.forEach {
                    if (rating.toInt() == it.star || rating.toDouble() == 0.5) {
                        if (rating.toDouble() == 0.5)
                            item.rating = 1
                        item.rating = rating.toInt()
                        item.stars?.forEach { it.questions?.forEach { it.isSelected=false } }
                        it.questions?.let { it1 -> list.addAll(it1) }
                        return@breaker
                    }
                }
            }

            holder.itemView.feedbackQuestionRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.itemView.feedbackQuestionRecyclerView?.adapter = FeedBackQuestionListAdapter(list)

        }
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.feedbackCategory
            itemView.feedbackCategoryImage
            itemView.feedbackQuestionRecyclerView
            itemView.birdRatingBarLayout
            itemView.rating
        }
    }


}