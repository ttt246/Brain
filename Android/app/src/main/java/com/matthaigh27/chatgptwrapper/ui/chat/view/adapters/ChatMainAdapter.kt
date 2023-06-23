package com.matthaigh27.chatgptwrapper.ui.chat.view.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.ChatMessageModel
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.SendSmsWidget
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.helpprompt.HelpPromptWidget
import com.matthaigh27.chatgptwrapper.utils.Constants
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper

class ChatMainAdapter(
    context: Context, list: ArrayList<ChatMessageModel>, callbacks: ChatMessageInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MSG_SENT = 0
    private val VIEW_TYPE_MSG_RECEIVED = 1
    private val VIEW_TYPE_CHAT_WIDGET = 2

    private var context: Context
    private var callbacks: ChatMessageInterface
    private var chatMessageList: ArrayList<ChatMessageModel> = ArrayList()

    init {
        this.context = context
        this.chatMessageList = list
        this.callbacks = callbacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)

        return when (viewType) {
            VIEW_TYPE_MSG_SENT -> {
                SentMessageViewHolder(
                    inflater.inflate(
                        R.layout.item_container_sent_message, parent, false
                    )
                )
            }

            VIEW_TYPE_MSG_RECEIVED -> {
                ReceivedMessageViewHolder(
                    inflater.inflate(
                        R.layout.item_container_received_message, parent, false
                    )
                )
            }

            else -> {
                ChatWidgetViewHolder(
                    inflater.inflate(
                        R.layout.item_container_chat_widget, parent, false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return chatMessageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return chatMessageList[position].type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val index = holder.adapterPosition
        val chatMessageModel: ChatMessageModel = chatMessageList[index]
        when (chatMessageModel.type) {
            VIEW_TYPE_MSG_SENT -> {
                setMessageData(holder as SentMessageViewHolder, chatMessageModel)
            }

            VIEW_TYPE_MSG_RECEIVED -> {
                setMessageData(holder as ReceivedMessageViewHolder, chatMessageModel)
            }

            else -> {
                setMessageData(holder as ChatWidgetViewHolder, chatMessageModel)
            }
        }
    }

    private fun setMessageData(holder: SentMessageViewHolder, data: ChatMessageModel) {
        holder.txtMessage.text = data.content
    }

    private fun setMessageData(holder: ReceivedMessageViewHolder, data: ChatMessageModel) {
        if (data.hasImage) {
            data.image?.let { image ->
                val originBitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                val radius = context.resources.getDimensionPixelSize(R.dimen.radius_small)
                val bmp = ImageHelper.getRoundedCornerBitmap(originBitmap, radius)
                holder.imgMessage.visibility = View.VISIBLE
                holder.imgMessage.setImageBitmap(bmp)

                data.content?.let { message ->
                    holder.txtMessage.text = message
                } ?: run {
                    holder.txtMessage.visibility = View.GONE
                }
            }
        } else {
            holder.txtMessage.text = data.content
            holder.imgMessage.visibility = View.GONE
            holder.txtMessage.visibility = View.VISIBLE
        }
    }

    private fun setMessageData(holder: ChatWidgetViewHolder, data: ChatMessageModel) {
        when (data.content) {
            Constants.TYPE_WIDGET_SMS -> {
                val sendSmsWidget = SendSmsWidget(context).apply {
                    this.callback = callbacks
                }
                holder.itemLayout.addView(sendSmsWidget)
                holder.itemLayout.visibility = View.VISIBLE
            }

            Constants.TYPE_WIDGET_HELP_PROMPT -> {
//                val helpPromptWidget = HelpPromptWidget(context)
            }

            Constants.TYPE_WIDGET_SEARCH_CONTACT -> {

            }

            Constants.TYPE_WIDGET_FEEDBACK -> {

            }
        }
    }

    inner class ReceivedMessageViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var txtMessage: TextView
        var imgMessage: ImageView
        var itemLayout: ConstraintLayout

        init {
            txtMessage = itemView.findViewById<View>(R.id.txt_message) as TextView
            imgMessage = itemView.findViewById<View>(R.id.img_message) as ImageView
            itemLayout = itemView.findViewById<View>(R.id.cl_received_message) as ConstraintLayout
        }
    }

    inner class SentMessageViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var txtMessage: TextView
        var imgMessage: ImageView
        var itemLayout: ConstraintLayout

        init {
            txtMessage = itemView.findViewById<View>(R.id.txt_message) as TextView
            imgMessage = itemView.findViewById<View>(R.id.img_message) as ImageView
            itemLayout = itemView.findViewById<View>(R.id.cl_sent_message) as ConstraintLayout
        }
    }

    inner class ChatWidgetViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var itemLayout: FrameLayout

        init {
            itemLayout = itemView.findViewById<View>(R.id.fl_widget_message) as FrameLayout
        }
    }
}