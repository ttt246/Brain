package com.matthaigh27.chatgptwrapper.ui.chat.view.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.chat.ChatMessageModel
import com.matthaigh27.chatgptwrapper.data.models.chat.HelpPromptModel
import com.matthaigh27.chatgptwrapper.data.models.chatwidgetprops.MailsProps
import com.matthaigh27.chatgptwrapper.data.models.chatwidgetprops.ScheduleAlarmProps
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.OnHideListener
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.SendSmsWidget
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.alarm.ScheduleAlarmWidget
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.contact.ContactWidget
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.helpprompt.HelpPromptWidget
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.mail.ComposeMailWidget
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.mail.MailWidget
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.chatwidget.mail.ReadMailWidget
import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants.PROPS_WIDGET_DESC
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_HELP_PROMPT
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_MAILS
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_MAIL_READ
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_MAIL_WRITE
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_SCHEDULE_ALARM
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_SEARCH_CONTACT
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_SMS
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ContactHelper.getContactModelById
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ContactHelper.getContacts
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.ImageHelper
import org.json.JSONArray

/**
 * This adapter class is used to display a list of chat messages on a recycler view.
 */
class ChatMainAdapter(
    context: Context, list: ArrayList<ChatMessageModel>, callbacks: ChatMessageInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * These variables are used to presents the type of messages
     */
    private val VIEW_TYPE_MSG_SENT = 0
    private val VIEW_TYPE_MSG_RECEIVED = 1
    private val VIEW_TYPE_CHAT_WIDGET = 2
    private val VIEW_TYPE_CHAT_ERROR = 3

    private var context: Context
    private var chatMessageList: ArrayList<ChatMessageModel> = ArrayList()

    /**
     * This is a callback that retrieves result from chat widgets.
     */
    private var callbacks: ChatMessageInterface

    init {
        this.context = context
        this.chatMessageList = list
        this.callbacks = callbacks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)

        return when (viewType) {
            VIEW_TYPE_MSG_SENT -> {
                MessageViewHolder(
                    inflater.inflate(
                        R.layout.item_container_sent_message, parent, false
                    )
                )
            }

            VIEW_TYPE_MSG_RECEIVED -> {
                MessageViewHolder(
                    inflater.inflate(
                        R.layout.item_container_received_message, parent, false
                    )
                )
            }

            VIEW_TYPE_CHAT_ERROR -> {
                MessageViewHolder(
                    inflater.inflate(
                        R.layout.item_container_error_message, parent, false
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
            VIEW_TYPE_CHAT_WIDGET -> {
                setMessageData(holder as ChatWidgetViewHolder, chatMessageModel)
            }

            else -> {
                setMessageData(holder as MessageViewHolder, chatMessageModel)
            }
        }
    }

    /**
     * This function is used to set data for common messages.
     */
    private fun setMessageData(holder: MessageViewHolder, data: ChatMessageModel) {
        /**
         * If an image is included into a message, the image is displayed by below code.
         */
        if (data.hasImage) {
            data.image?.let { image ->
                val originBitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                val radius = context.resources.getDimensionPixelSize(R.dimen.radius_small)
                val bmp = ImageHelper.getRoundedCornerBitmap(originBitmap, radius)
                holder.imgMessage.visibility = View.VISIBLE
                holder.imgMessage.setImageBitmap(bmp)

                if (data.content != "" && data.content != null) {
                    holder.txtMessage.text = data.content
                } else {
                    holder.txtMessage.visibility = View.GONE
                }
            }
        } else {
            holder.txtMessage.text = data.content
            holder.imgMessage.visibility = View.GONE
            data.content?.let {
                holder.txtMessage.visibility = View.VISIBLE
            } ?: run {
                holder.txtMessage.visibility = View.GONE
            }
        }
    }

    /**
     * This function is used to display chat widgets on a recycler view.
     */
    private fun setMessageData(holder: ChatWidgetViewHolder, data: ChatMessageModel) {
        holder.itemLayout.visibility = View.VISIBLE
        holder.llHorizontalScroll.removeAllViews()
        holder.itemLayout.removeAllViews()
        val index = holder.adapterPosition

        /**
         * Depending on type of widget, the proper widget is displayed.
         */
        when (data.content) {
            TYPE_WIDGET_SMS -> {
                val sendSmsWidget = SendSmsWidget(context).apply {
                    this.callback = callbacks
                    this.hideListener = object : OnHideListener {
                        override fun hide() {
                            holder.itemLayout.visibility = View.GONE
                            chatMessageList.removeAt(index)
                            notifyItemRemoved(index)
                        }
                    }
                }

                if (data.data != null) {
                    val widgetDesc = data.data.asJsonObject[PROPS_WIDGET_DESC].asString
                    if (widgetDesc.isNotEmpty()) {
                        sendSmsWidget.setPhoneNumber(widgetDesc)
                    }
                }

                holder.itemLayout.addView(sendSmsWidget)
            }

            TYPE_WIDGET_HELP_PROMPT -> {
                val widgetDesc = data.data!!.asJsonObject[PROPS_WIDGET_DESC].asString
                val helpPromptWidget =
                    HelpPromptWidget(context, HelpPromptModel.init(widgetDesc)).apply {
                        this.callback = callbacks
                        this.hideListener = object : OnHideListener {
                            override fun hide() {
                                holder.itemLayout.visibility = View.GONE
                                chatMessageList.removeAt(index)
                                notifyItemRemoved(index)
                            }
                        }
                    }
                holder.itemLayout.addView(helpPromptWidget)
            }

            TYPE_WIDGET_SEARCH_CONTACT -> {
                holder.llHorizontalScroll.visibility = View.VISIBLE

                val contacts = getContacts(context)

                val contactIds = JSONArray(data.data!!.asString.replace("'", "\""))
                for (i in 0 until contactIds.length()) {
                    val contactId = contactIds[i].toString()
                    val contact = getContactModelById(contactId, contacts)

                    val contactWidget = ContactWidget(context, contact).apply {
                        this.callback = callbacks
                    }
                    holder.llHorizontalScroll.addView(contactWidget)
                }
            }

            TYPE_WIDGET_SCHEDULE_ALARM -> {
                var props = ScheduleAlarmProps()
                data.data?.run {
                    val widgetDesc = data.data.asJsonObject[PROPS_WIDGET_DESC].asString
                    props = ScheduleAlarmProps.init(widgetDesc)
                }
                val scheduleAlarmWidget =
                    ScheduleAlarmWidget(context, props.time, props.label, props.repeat).apply {
                        this.callback = callbacks
                        this.hideListener = object : OnHideListener {
                            override fun hide() {
                                holder.itemLayout.visibility = View.GONE
                                chatMessageList.removeAt(index)
                                notifyItemRemoved(index)
                            }
                        }
                    }
                holder.itemLayout.addView(scheduleAlarmWidget)
            }

            TYPE_WIDGET_MAILS -> {
                holder.llHorizontalScroll.visibility = View.VISIBLE
                val props = data.data?.run {
                    val widgetDesc = data.data.asJsonObject[PROPS_WIDGET_DESC].asString
                    MailsProps.init(widgetDesc)
                }

                props?.mails?.forEach { mail ->
                    val mailWidget = MailWidget(context, mail).apply {
                        this.callback = callbacks
                    }
                    holder.llHorizontalScroll.addView(mailWidget)
                }
            }

            TYPE_WIDGET_MAIL_READ -> {
                val readMailWidget = ReadMailWidget(context).apply {
                    this.callback = callbacks
                    this.hideListener = object : OnHideListener {
                        override fun hide() {
                            holder.itemLayout.visibility = View.GONE
                            chatMessageList.removeAt(index)
                            notifyItemRemoved(index)
                        }
                    }
                }
                holder.itemLayout.addView(readMailWidget)
            }

            TYPE_WIDGET_MAIL_WRITE -> {
                val composeMailWidget = ComposeMailWidget(context).apply {
                    this.callback = callbacks
                    this.hideListener = object : OnHideListener {
                        override fun hide() {
                            holder.itemLayout.visibility = View.GONE
                            chatMessageList.removeAt(index)
                            notifyItemRemoved(index)
                        }
                    }
                }
                holder.itemLayout.addView(composeMailWidget)
            }

            else -> {
                holder.itemLayout.visibility = View.GONE
            }
        }
    }

    /**
     * ViewHolder for common messages with message and image
     */
    inner class MessageViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var txtMessage: TextView
        var imgMessage: ImageView
        var itemLayout: ConstraintLayout

        init {
            txtMessage = itemView.findViewById<View>(R.id.txt_message) as TextView
            imgMessage = itemView.findViewById<View>(R.id.img_message) as ImageView
            itemLayout = itemView.findViewById<View>(R.id.cl_message) as ConstraintLayout
        }
    }

    /**
     * ViewHolder for chat widgets
     */
    inner class ChatWidgetViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var itemLayout: FrameLayout
        var llHorizontalScroll: LinearLayout

        init {
            itemLayout = itemView.findViewById<View>(R.id.fl_widget_message) as FrameLayout
            llHorizontalScroll =
                itemView.findViewById<View>(R.id.ll_horizontal_scroll) as LinearLayout
        }
    }
}