package com.matthaigh27.chatgptwrapper.ui.chat.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.ChatMessageModel
import com.matthaigh27.chatgptwrapper.data.models.HelpCommandModel
import com.matthaigh27.chatgptwrapper.data.models.HelpPromptModel
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.ui.chat.view.adapters.ChatMainAdapter
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.toolbar.ChatToolsWidget
import com.matthaigh27.chatgptwrapper.ui.chat.viewmodel.ChatViewModel
import com.matthaigh27.chatgptwrapper.utils.Constants.ERROR_MSG_NOEXIST_COMMAND
import com.matthaigh27.chatgptwrapper.utils.Constants.HELP_COMMAND_ALL
import com.matthaigh27.chatgptwrapper.utils.Constants.TYPE_RESPONSE_ALERT
import com.matthaigh27.chatgptwrapper.utils.Constants.TYPE_RESPONSE_BROWSER
import com.matthaigh27.chatgptwrapper.utils.Constants.TYPE_RESPONSE_CONTACT
import com.matthaigh27.chatgptwrapper.utils.Constants.TYPE_RESPONSE_IMAGE
import com.matthaigh27.chatgptwrapper.utils.Constants.TYPE_RESPONSE_MESSAGE
import com.matthaigh27.chatgptwrapper.utils.Constants.TYPE_RESPONSE_SMS
import com.matthaigh27.chatgptwrapper.utils.Constants.TYPE_WIDGET_HELP_PROMPT
import com.matthaigh27.chatgptwrapper.utils.Constants.TYPE_WIDGET_SMS
import com.matthaigh27.chatgptwrapper.utils.helpers.Converter.stringToHelpPromptList
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper.getHelpCommandFromStr
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper.isMainHelpCommand
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper.makePromptItemUsage
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper.makePromptUsage
import com.matthaigh27.chatgptwrapper.utils.helpers.ui.NoNewLineInputFilter

class ChatMainFragment : Fragment(), OnClickListener {

    private val TYPE_CHAT_SENT = 0
    private val TYPE_CHAT_RECEIVE = 1
    private val TYPE_CHAT_WIDGET = 2

    private lateinit var rootView: View
    lateinit var viewModel: ChatViewModel

    /** View Components */
    private var loadingRotate: RotateAnimation? = null
    private var edtMessageInput: EditText? = null

    private var rvChatList: RecyclerView? = null
    private var chatMainAdapter: ChatMainAdapter? = null
    private var chatMessageList: ArrayList<ChatMessageModel> = ArrayList()
    private lateinit var chatMessageInterface: ChatMessageInterface

    private var chatToolsWidget: ChatToolsWidget? = null
    private var helpPromptList: ArrayList<HelpPromptModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_chat_main, container, false)
        init()
        return rootView
    }

    private fun init() {
        initViewModel()
        initLoadingRotate()
        initButtonsClickListener()
        initChatInputListener()
        initChatRecyclerView()
        initChatToolsWidget()

        fetchAllCommands()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
    }

    private fun initLoadingRotate() {
        loadingRotate = RotateAnimation(/* fromDegrees = */ 0f, /* toDegrees = */
            360f, /* pivotXType = */
            Animation.RELATIVE_TO_SELF, /* pivotXValue = */
            0.5f, /* pivotYType = */
            Animation.RELATIVE_TO_SELF, /* pivotYValue = */
            0.5f
        ).apply {
            duration = 3000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    private fun initButtonsClickListener() {
        rootView.findViewById<View>(R.id.btn_open_chat_tools).setOnClickListener(this)
        rootView.findViewById<View>(R.id.btn_audio_recognition).setOnClickListener(this)
    }

    private fun initChatInputListener() {
        edtMessageInput = rootView.findViewById(R.id.edt_message)
        edtMessageInput?.filters = edtMessageInput?.filters?.plus(NoNewLineInputFilter())

        edtMessageInput?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                addMessage(TYPE_CHAT_SENT, edtMessageInput?.text.toString())
            }
            return@setOnKeyListener false
        }
    }

    private fun initChatRecyclerView() {
        initChatInterface()

        rvChatList = rootView.findViewById(R.id.rv_chat_list)
        chatMainAdapter = ChatMainAdapter(
            requireContext(), chatMessageList, chatMessageInterface
        )

        rvChatList?.adapter = chatMainAdapter
        rvChatList?.layoutManager = LinearLayoutManager(context)
    }

    private fun initChatToolsWidget() {
        chatToolsWidget = ChatToolsWidget(requireContext(), requireActivity())
        val llToolBar = rootView.findViewById<LinearLayout>(R.id.ll_toolbar)
        llToolBar.addView(chatToolsWidget)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(isLoading: Boolean) {
        val imgLoading = rootView.findViewById<ImageView>(R.id.sp_loading)

        if (isLoading) {
            imgLoading.startAnimation(loadingRotate)
            imgLoading.visibility = View.VISIBLE
        } else {
            imgLoading.clearAnimation()
            imgLoading.visibility = View.GONE
        }
    }

    private fun addMessage(
        type: Int,
        content: String? = null,
        data: JsonElement? = null,
        hasImage: Boolean = false,
        image: ByteArray? = null
    ) {
        addChatItemToList(ChatMessageModel(type, content, data, hasImage, image))
        when (type) {
            TYPE_CHAT_SENT -> {
                if (content!!.isNotEmpty() && content.first() == '/') {
                    openHelpPromptWidget(content)
                    return
                }
                sendNotification(content)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addChatItemToList(chatModel: ChatMessageModel) {
        edtMessageInput?.setText("")

        chatMessageList.add(chatModel)
        chatMainAdapter?.notifyDataSetChanged()
        rvChatList?.scrollToPosition(chatMessageList.size - 1)
    }

    private fun openHelpPromptWidget(message: String) {
        try {
            val command: HelpCommandModel = getHelpCommandFromStr(message)
            if (isMainHelpCommand(command)) {
                when (command.main) {
                    TYPE_WIDGET_SMS -> {
                        addMessage(
                            type = TYPE_CHAT_WIDGET, content = TYPE_WIDGET_SMS
                        )
                    }

                    else -> {
                        helpPromptList?.let { list ->
                            val data = list.filter { model ->
                                model.name == command.main
                            }

                            if(data.isEmpty()) {
                                addMessage(
                                    type = TYPE_CHAT_RECEIVE, content = ERROR_MSG_NOEXIST_COMMAND
                                )
                            } else {
                                addMessage(
                                    type = TYPE_CHAT_WIDGET,
                                    content = TYPE_WIDGET_HELP_PROMPT,
                                    data = data[0].toString() as JsonElement
                                )
                            }
                        }
                    }
                }
            } else {
                if (command.assist == HELP_COMMAND_ALL) {
                    addMessage(
                        type = TYPE_CHAT_RECEIVE, content = makePromptUsage(helpPromptList!!)
                    )
                } else {
                    addMessage(
                        type = TYPE_CHAT_RECEIVE,
                        content = makePromptItemUsage(helpPromptList!!, command.assist!!)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(e.message.toString())
        }
    }


    private fun fetchAllCommands() {
        viewModel.getAllHelpCommands().observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is ApiResource.Loading -> {
                    showLoading(true)
                }

                is ApiResource.Success -> {
                    showLoading(false)
                    resource.data?.let { data ->
                        helpPromptList =
                            stringToHelpPromptList(data.result.content.asJsonArray.toString())
                    }
                }

                is ApiResource.Error -> {
                    showLoading(false)
                    showToast(resource.message!!)
                }
            }
        })
    }

    private fun sendNotification(message: String) {
        viewModel.sendNotification(message).observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is ApiResource.Loading -> {
                    showLoading(true)
                }
                is ApiResource.Success -> {
                    showLoading(false)
                    val apiResponse = resource.data
                    when (apiResponse?.result?.program) {
                        TYPE_RESPONSE_MESSAGE -> {
                            addMessage(TYPE_CHAT_RECEIVE, apiResponse.result.content.toString())
                        }
                        TYPE_RESPONSE_BROWSER -> {
                            fetchResponseBrowser(apiResponse)
                        }
                        TYPE_RESPONSE_ALERT -> {

                        }
                        TYPE_RESPONSE_CONTACT -> {

                        }
                        TYPE_RESPONSE_IMAGE -> {
                            fetchResponseImage(apiResponse)
                        }
                        TYPE_RESPONSE_SMS -> {

                        }
                        else -> {

                        }
                    }
                }
                is ApiResource.Error -> {
                    showToast(resource.message!!)
                    showLoading(false)
                }
            }

        })
    }

    private fun fetchResponseBrowser(apiResponse: ApiResponse) {
        addMessage(TYPE_CHAT_RECEIVE, apiResponse.result.url)
    }

    private fun fetchResponseImage(apiResponse: ApiResponse) {
        val content = apiResponse.result.content.asJsonObject
        viewModel.downloadImageFromFirebase(
            content["image_name"].asString
        ).observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is ApiResource.Loading -> {
                    showLoading(true)
                }

                is ApiResource.Success -> {
                    showLoading(false)
                    addMessage(
                        type = TYPE_CHAT_RECEIVE,
                        content = null,
                        data = null,
                        hasImage = true,
                        image = resource.data
                    )
                }

                is ApiResource.Error -> {
                    showToast(resource.message!!)
                    showLoading(false)
                }
            }
        })
    }


    private fun initChatInterface() {
        chatMessageInterface = object : ChatMessageInterface {
            override fun sentSms(phoneNumber: String, message: String) {
                addMessage(
                    type = TYPE_CHAT_RECEIVE,
                    content = "You sent SMS with belowing content.\n\n " + "To: $phoneNumber\n " + "Message: $message",
                )
            }

            override fun canceledSms() {
                addMessage(
                    type = TYPE_CHAT_RECEIVE,
                    content = "You canceled SMS.",
                )
            }

            override fun sentHelpPrompt(prompt: String) {
            }

            override fun canceledHelpPrompt() {
            }

            override fun doVoiceCall(phoneNumber: String) {
            }

            override fun doVideoCall(phoneNumber: String) {
            }

            override fun sendSmsWithPhoneNumber(phoneNumber: String) {
            }

            override fun pickImage(isSuccess: Boolean, data: ByteArray?) {
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_open_chat_tools -> {
                chatToolsWidget?.toggle()
            }

            R.id.btn_audio_recognition -> {

            }
        }
    }
}