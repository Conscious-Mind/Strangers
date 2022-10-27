package com.davidson.strangers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.davidson.strangers.databinding.StrangerSingleCardBinding
import com.davidson.strangers.domain.StrangerPerson

class RvStrangerViewAdapter :
    ListAdapter<StrangerPerson, RvStrangerViewAdapter.ItemViewHolder>(DiffUtilCallBack()) {

    private var clickListener: ((strangerPerson: StrangerPerson) -> Unit)? = null


    class ItemViewHolder private constructor(private val binding: StrangerSingleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            strangerPerson: StrangerPerson,
            clickListener: ((strangerPerson: StrangerPerson) -> Unit)?
        ) {
            binding.stranger = strangerPerson
            clickListener?.let {
                binding.root.setOnClickListener {
                    clickListener.invoke(strangerPerson)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StrangerSingleCardBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }


    fun setOnclickListenerR(clickListener: ((strangerPerson: StrangerPerson) -> Unit)) {
        this.clickListener = clickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        return holder.bind(getItem(position), clickListener)
    }
}

class DiffUtilCallBack : DiffUtil.ItemCallback<StrangerPerson>() {
    override fun areItemsTheSame(oldItem: StrangerPerson, newItem: StrangerPerson): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StrangerPerson, newItem: StrangerPerson): Boolean {
        return oldItem == newItem
    }
}