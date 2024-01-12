package com.fuadhev.myfacededector.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fuadhev.myfacededector.R
import com.fuadhev.myfacededector.common.utils.GenericDiffUtil
import com.fuadhev.myfacededector.data.local.Result
import com.fuadhev.myfacededector.databinding.ItemResultBinding

class ResultAdapter: ListAdapter<Result, ResultAdapter.ViewHolder>(GenericDiffUtil<Result>(
    myItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    myContentsTheSame = { oldItem, newItem -> oldItem == newItem }
)) {


    inner class ViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Result,context: Context) {
            with(binding) {
                txtId.text=item.id.toString()
                txtHeadLeft.text=context.getString(R.string.head_to_left,item.left.toString())
                txtHeadRight.text=context.getString(R.string.head_to_right,item.right.toString())
                txtSmile.text=context.getString(R.string.smile,item.smile.toString())
                txtNeutral.text=context.getString(R.string.neutral,item.neutral.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),holder.itemView.context,)

    }

}