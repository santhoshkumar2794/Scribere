package com.zestworks.blogger.ui.create_new

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zestworks.blogger.R
import kotlinx.android.synthetic.free.create_blog_fragment.view.*
import kotlinx.android.synthetic.main.template_holder.view.*

class TemplateChooser(private val templateIDList: Array<Template>, private val templateSelector: TemplateSelector) : RecyclerView.Adapter<TemplateChooser.TemplateHolder>() {

    var currentTemplate: Template? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_holder, parent, false)
        return TemplateHolder(view)
    }

    override fun getItemCount(): Int {
        return templateIDList.size
    }

    override fun onBindViewHolder(holder: TemplateHolder, position: Int) {
        holder.index = position
        holder.itemView.template_view.setBackgroundColor(Color.TRANSPARENT)
        holder.itemView.template_view.setImageResource(templateIDList[position].getID())
        if (currentTemplate != null && currentTemplate == templateIDList[position]) {
            holder.itemView.template_view.setBackgroundResource(R.drawable.template_selected_background)
        }
    }

    inner class TemplateHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var index: Int = 0

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            templateSelector.onTemplateSelected(templateIDList[index])
        }
    }
}