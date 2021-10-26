package com.yourapp.stackoverflow.listAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yourapp.stackoverflow.databinding.QuestionCardViewBinding
import com.yourapp.stackoverflow.model.QuestionCardModel
import java.text.SimpleDateFormat
import java.util.*

class QuestionsCustomAdapter(
    private val context : Context?,
    private val questions : List<QuestionCardModel>,
    private val onItemClicked : (position : Int) -> Unit
) : RecyclerView.Adapter<QuestionsCustomAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = QuestionCardViewBinding.inflate(view, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.titleTextView.text = questions[position].title
        holder.nameTextView.text = questions[position].name
        holder.dateTextView.text = "Posted on :"+ getDateTime(questions[position].date)

        if (context != null) {
            Glide.with(context)
                .load(questions[position].imgUrl)
                .circleCrop()
                .into(holder.ownerImageView)
        }
    }

    private fun getDateTime(l : Long): String? {
        try {
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val netDate = Date(l *1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    override fun getItemCount() = questions.size


    inner class MyViewHolder(private val binding : QuestionCardViewBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener{
        val nameTextView : TextView = binding.ownerName
        val titleTextView : TextView = binding.questionTitle
        val dateTextView : TextView = binding.postedDate
        val ownerImageView : ImageView = binding.ownerImage

        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            onItemClicked(adapterPosition)
        }

    }
}