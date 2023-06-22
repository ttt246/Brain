package com.matthaigh27.chatgptwrapper.ui.chat.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.data.models.ChatMessageModel
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.ui.chat.view.adapters.ChatMainAdapter
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.toolbar.ChatToolsWidget
import com.matthaigh27.chatgptwrapper.ui.chat.viewmodel.ChatViewModel
import com.matthaigh27.chatgptwrapper.utils.Constants
import com.matthaigh27.chatgptwrapper.utils.helpers.NoNewLineInputFilter

class ChatMainFragment : Fragment(), OnClickListener {

    private val TYPE_CHAT_SENT = 0
    private val TYPE_CHAT_RECEIVE = 1
    private val TYPE_CHAT_WIDGET = 2

    lateinit var viewModel: ChatViewModel

    private var loadingRotate: RotateAnimation? = null

    private lateinit var rootView: View
    private var edtMessageInput: EditText? = null

    private var rvChatList: RecyclerView? = null
    private var chatMainAdapter: ChatMainAdapter? = null
    private var chatMessageList: ArrayList<ChatMessageModel> = ArrayList()
    private lateinit var chatMessageInterface: ChatMessageInterface

    private var chatToolsWidget: ChatToolsWidget? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_chat_main, container, false)

        init()

        return rootView
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

    private fun init() {
        initViewModel()
        initLoadingRotate()
        initButtonsClickListener()
        initChatInputListener()
        initChatRecyclerView()
        initChatToolsWidget()

        getAllCommands()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
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

    private fun initChatRecyclerView() {
        initChatInterface()

        rvChatList = rootView.findViewById(R.id.rv_chat_list)
        chatMainAdapter = ChatMainAdapter(
            requireContext(),
            chatMessageList,
            chatMessageInterface
        )

        rvChatList?.adapter = chatMainAdapter
        rvChatList?.layoutManager = LinearLayoutManager(context)
    }

    private fun initChatInterface() {
        chatMessageInterface = object : ChatMessageInterface {
            override fun sentSms(phoneNumber: String, message: String) {
                TODO("Not yet implemented")
            }

            override fun canceledSms() {
                TODO("Not yet implemented")
            }

            override fun sentHelpPrompt(prompt: String) {
                TODO("Not yet implemented")
            }

            override fun canceledHelpPrompt() {
                TODO("Not yet implemented")
            }

            override fun doVoiceCall(phoneNumber: String) {
                TODO("Not yet implemented")
            }

            override fun doVideoCall(phoneNumber: String) {
                TODO("Not yet implemented")
            }

            override fun sendSmsWithPhoneNumber(phoneNumber: String) {
                TODO("Not yet implemented")
            }

            override fun pickImage(isSuccess: Boolean, data: ByteArray?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun initChatToolsWidget() {
        chatToolsWidget = ChatToolsWidget(requireContext(), requireActivity())
        val llToolBar = rootView.findViewById<LinearLayout>(R.id.ll_toolbar)
        llToolBar.addView(chatToolsWidget)
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

    private fun addMessage(type: Int, content: String, data: String? = null) {
        when(type) {
            TYPE_CHAT_SENT -> {
                addChatItemToList(ChatMessageModel(type, content))
                sendNotification(content)
            }
            TYPE_CHAT_RECEIVE -> {
                addChatItemToList(ChatMessageModel(type, content))
            }
            TYPE_CHAT_WIDGET -> {
                val chatModel = ChatMessageModel(type, content)
                chatMessageList.add(chatModel)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addChatItemToList(chatModel: ChatMessageModel){
        edtMessageInput?.setText("")

        chatMessageList.add(chatModel)
        chatMainAdapter?.notifyDataSetChanged()
        rvChatList?.scrollToPosition(chatMessageList.size - 1)
    }

    private fun getAllCommands() {
        viewModel.getAllHelpCommands().observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is ApiResource.Loading -> {
                    showLoading(true)
                }
                is ApiResource.Success -> {
                    showLoading(false)
                }
                is ApiResource.Error -> {
                    showToast(resource.message!!)
                    showLoading(false)
                }
            }
        })
    }

    private fun sendNotification(message: String) {
        viewModel.sendNotification(message).observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is ApiResource.Loading -> {
                    showLoading(true)
                }
                is ApiResource.Success -> {
                    val apiResponse = resource.data
                    val code = apiResponse?.status_code
                    when(apiResponse?.result?.program) {
                        Constants.TYPE_RESPONSE_MESSAGE -> {
                            addMessage(TYPE_CHAT_RECEIVE, apiResponse.result.content.toString())
                        }
                        Constants.TYPE_RESPONSE_BROWSER -> {
                            addMessage(TYPE_CHAT_RECEIVE, apiResponse.result.url)
                        }
                        Constants.TYPE_RESPONSE_ALERT -> {

                        }
                        Constants.TYPE_RESPONSE_CONTACT -> {

                        }
                        Constants.TYPE_RESPONSE_IMAGE -> {

                        }
                        Constants.TYPE_RESPONSE_HELP_COMMAND -> {

                        }
                        Constants.TYPE_RESPONSE_SMS -> {

                        }
                        else -> {

                        }
                    }
                    showLoading(false)
                }
                is ApiResource.Error -> {
                    showToast(resource.message!!)
                    showLoading(false)
                }
            }

        })
    }
}