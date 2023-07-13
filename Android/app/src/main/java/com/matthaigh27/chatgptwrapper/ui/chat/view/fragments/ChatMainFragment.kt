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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.RisingApplication
import com.matthaigh27.chatgptwrapper.RisingApplication.Companion.appContext
import com.matthaigh27.chatgptwrapper.data.models.chat.ChatMessageModel
import com.matthaigh27.chatgptwrapper.data.models.chat.HelpCommandModel
import com.matthaigh27.chatgptwrapper.data.models.chat.HelpPromptModel
import com.matthaigh27.chatgptwrapper.data.models.chat.MailModel
import com.matthaigh27.chatgptwrapper.data.models.chatwidgetprops.MailsProps
import com.matthaigh27.chatgptwrapper.data.models.chatwidgetprops.ScheduleAlarmProps
import com.matthaigh27.chatgptwrapper.data.models.common.Time
import com.matthaigh27.chatgptwrapper.data.remote.ApiResource
import com.matthaigh27.chatgptwrapper.data.remote.responses.ApiResponse
import com.matthaigh27.chatgptwrapper.data.remote.responses.results.CommonResult
import com.matthaigh27.chatgptwrapper.data.repository.RemoteRepository
import com.matthaigh27.chatgptwrapper.ui.chat.view.adapters.ChatMainAdapter
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.toolbar.ChatToolsWidget
import com.matthaigh27.chatgptwrapper.ui.chat.viewmodel.ChatViewModel
import com.matthaigh27.chatgptwrapper.ui.chat.viewmodel.ChatViewModelFactory
import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants.ERROR_MSG_NOEXIST_COMMAND
import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants.ERROR_MSG_UNKNOWN_ERROR
import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants.HELP_COMMAND_ALL
import com.matthaigh27.chatgptwrapper.utils.constants.CommonConstants.PROPS_WIDGET_DESC
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_HELP_PROMPT
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_MAILS
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_MAIL_READ
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_MAIL_WRITE
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_SCHEDULE_ALARM
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_SEARCH_CONTACT
import com.matthaigh27.chatgptwrapper.utils.constants.TypeChatWidgetConstants.TYPE_WIDGET_SMS
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_ALARM
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_AUTO_TASK
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_BROWSER
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_CONTACT
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_IMAGE
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_MAIL_READ
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_MAIL_SEND
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_MAIL_WRITE
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_MESSAGE
import com.matthaigh27.chatgptwrapper.utils.constants.TypeResponseConstants.TYPE_RESPONSE_SMS
import com.matthaigh27.chatgptwrapper.utils.helpers.Converter
import com.matthaigh27.chatgptwrapper.utils.helpers.Converter.responseToHelpPromptList
import com.matthaigh27.chatgptwrapper.utils.helpers.OnSuccess
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper.getHelpCommandFromStr
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper.isMainHelpCommand
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper.makePromptItemUsage
import com.matthaigh27.chatgptwrapper.utils.helpers.chat.CommandHelper.makePromptUsage
import com.matthaigh27.chatgptwrapper.utils.helpers.ui.NoNewLineInputFilter
import org.json.JSONArray

/**
 * A ChatMainFragment class is a class for chatting with users and show the result that
 * our AI plugin - Brain returned.
 *
 * In this class, almost important features are implemented.
 */
class ChatMainFragment : Fragment(), OnClickListener {

    /**
     * These variables(TYPE_CHAT_XXX) are variables that present type of widget to display on chatting list.
     */
    private val TYPE_CHAT_SENT = 0
    private val TYPE_CHAT_RECEIVE = 1
    private val TYPE_CHAT_WIDGET = 2
    private val TYPE_CHAT_ERROR = 3

    private lateinit var rootView: View
    lateinit var viewModel: ChatViewModel

    private var loadingRotate: RotateAnimation? = null
    private var edtMessageInput: EditText? = null

    /**
     * These variables are for recyclerview that shows chatting list items.
     */
    private var rvChatList: RecyclerView? = null
    private var chatMainAdapter: ChatMainAdapter? = null
    private var chatMessageList: ArrayList<ChatMessageModel> = ArrayList()
    private lateinit var chatMessageInterface: ChatMessageInterface

    private var chatToolsWidget: ChatToolsWidget? = null
    private var helpPromptList: ArrayList<HelpPromptModel>? = null

    /**
     * These variables are used to identify whether users picked an image to send to Brain.
     */
    private var currentSelectedImage: ByteArray? =
        null // bytearray data of a picked image by camera or gallery
    private var currentUploadedImageName: String? =
        null // resource name that is uploaded to firebase storage
    private var isImagePicked: Boolean =
        false // boolean variable that presents whether users picked an image

    private var showLoadingCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_chat_main, container, false)
        init()
        return rootView
    }

    private fun init() {
        /**
         * These are functions to init views
         */
        initViewModel()
        initLoadingRotate()
        initButtonsClickListener()
        initChatInputListener()
        initChatRecyclerView()
        initChatToolsWidget()

        fetchAllCommands()
    }

    /**
     * This function is used to init viewmodel.
     */
    private fun initViewModel() {
        val repository = RemoteRepository(appContext)
        val viewModelFactory = ChatViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
    }

    /**
     * This function is used to init rotate animation for loading spinner.
     */
    private fun initLoadingRotate() {
        loadingRotate = RotateAnimation(/* fromDegrees = */ 0f, /* toDegrees = */
            720f, /* pivotXType = */
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

    /**
     * In this function, click listener is set to buttons on main chat interface.
     */
    private fun initButtonsClickListener() {
        rootView.findViewById<View>(R.id.btn_open_chat_tools).setOnClickListener(this)
        rootView.findViewById<View>(R.id.btn_audio_recognition).setOnClickListener(this)
    }

    /**
     * Send text message to Brain when users finish typing text to EditText component.
     */
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

    /**
     * This function is used to init recyclerview that stores chatting messages.
     */
    private fun initChatRecyclerView() {
        /**
         * Init callback function that binds the event emitted in all chat widgets
         */
        initChatInterface()

        rvChatList = rootView.findViewById(R.id.rv_chat_list)
        chatMainAdapter = ChatMainAdapter(
            requireContext(), chatMessageList, chatMessageInterface
        )

        rvChatList?.adapter = chatMainAdapter
        rvChatList?.layoutManager = LinearLayoutManager(context)
    }

    /**
     * This function is used to init chat tool widget that pops up when users click plus button
     * at the right bottom corner.
     */
    private fun initChatToolsWidget() {
        chatToolsWidget = ChatToolsWidget(requireContext(), requireActivity()).apply {
            this.callback = chatMessageInterface
        }
        val llToolBar = rootView.findViewById<LinearLayout>(R.id.ll_toolbar)
        llToolBar.addView(chatToolsWidget)
    }

    /**
     * This function is used to show loading spinner.
     *
     * If isLoading is true, loading spinner is displayed. But loading spinner might not be visible,
     * even though isLoading is true. Current visible loading spinner count should be 0 to make loading spinner invisible.
     *
     * @param isLoading boolean parameter that presents whether loading spinner is visible
     */
    private fun showLoading(isLoading: Boolean) {
        val imgLoading = rootView.findViewById<ImageView>(R.id.sp_loading)

        if (isLoading) {
            imgLoading.startAnimation(loadingRotate)
            imgLoading.visibility = View.VISIBLE
            edtMessageInput?.isEnabled = false
            showLoadingCount++
        } else {
            showLoadingCount--
            if (showLoadingCount == 0) {
                imgLoading.clearAnimation()
                imgLoading.visibility = View.GONE
                edtMessageInput?.isEnabled = true
            }
        }
    }

    /**
     * This function is used mostly to add messages to chatting list.
     *
     * According to type of message, messages can be common messages or widgets for sending sms, setting an alarm, sending help prompt, etc.
     * Depending on whether an image is contained into message, different network functions are executed.
     * If message start with '/', help prompt widget is displayed.
     *
     * @param type A variable that present type of chat messages
     * @param content Contains content of message to present
     * @param data Contains properties of widget as json string when type is widget.
     * @param hasImage Presents whether an image is contained into message
     * @param image If a message has an image, this variable is used to store the bytearray data of the contained image.
     */
    private fun addMessage(
        type: Int,
        content: String? = null,
        data: JsonElement? = null,
        hasImage: Boolean = false,
        image: ByteArray? = null
    ) {
        when (type) {
            TYPE_CHAT_SENT -> {
                /**
                 * If message starts with '/', help prompt widget is displayed.
                 */
                if (content!!.isNotEmpty() && content.first() == '/') {
                    openHelpPromptWidget(content)
                    return
                }
                if (isImagePicked) {
                    /**
                     * If an image is contained into a message, image relatedness function is executed.
                     */
                    addChatItemToList(
                        ChatMessageModel(
                            type = type,
                            content = content,
                            data = data,
                            hasImage = true,
                            image = currentSelectedImage
                        )
                    )
                    fetchImageRelatedness(currentUploadedImageName!!, content)
                    isImagePicked = false
                } else {
                    /**
                     * If not, send notification function is executed.
                     */
                    addChatItemToList(ChatMessageModel(type, content, data, hasImage, image))
                    sendNotification(content)
                }
            }

            else -> {
                addChatItemToList(ChatMessageModel(type, content, data, hasImage, image))
            }
        }
    }

    /**
     * This function is used to display messages to show errors in more detail to users
     */
    private fun addErrorMessage(
        message: String
    ) {
        addMessage(
            type = TYPE_CHAT_RECEIVE, content = message
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addChatItemToList(chatModel: ChatMessageModel) {
        edtMessageInput?.setText("")

        chatMessageList.add(chatModel)
        chatMainAdapter?.notifyDataSetChanged()
        rvChatList?.scrollToPosition(chatMessageList.size - 1)
    }

    /**
     * This function is used to process ApiResource data from viewmodel.
     *
     * When status is loading or error, default functions are executed. When success, onSuccess
     * lambda function is executed.
     *
     * @param resource A ApiResource that is retrieved from ViewModel
     * @param onSuccess A lambda function to process the resource data
     */
    private fun <T> commonProcessResource(
        resource: ApiResource<T>,
        onSuccess: OnSuccess<ApiResource<T>>
    ) {
        when (resource) {
            is ApiResource.Loading -> {
                showLoading(true)
            }

            is ApiResource.Success -> {
                showLoading(false)
                onSuccess(resource)
            }

            is ApiResource.Error -> {
                showLoading(false)
                resource.message?.let {
                    addErrorMessage(resource.message)
                } ?: run {
                    addErrorMessage(ERROR_MSG_UNKNOWN_ERROR)
                }

            }
        }
    }

    /**
     * Open help prompt widgets when message is valid help command.
     *
     * @param message A String variable that contains message users sent
     * If message is /help, all the description of help commands is displayed,
     * If message is /help prompt name, the description of a given prompt help command is displayed.
     * If message is /prompt name, A help prompt widget about the given name is displayed.
     */
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

                            if (data.isEmpty()) {
                                addMessage(
                                    type = TYPE_CHAT_RECEIVE, content = ERROR_MSG_NOEXIST_COMMAND
                                )
                            } else {
                                val widgetDesc = JsonObject().apply {
                                    this.addProperty(PROPS_WIDGET_DESC, data[0].toString())
                                }
                                addMessage(
                                    type = TYPE_CHAT_WIDGET,
                                    content = TYPE_WIDGET_HELP_PROMPT,
                                    data = widgetDesc
                                )
                            }
                        } ?: run {
                            addErrorMessage("Help commands don't exist.")
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
            addErrorMessage(e.message.toString())
        }
    }


    /**
     * This Network function is used to fetch all help commands
     */
    private fun fetchAllCommands() {
        viewModel.getAllHelpCommands().observe(viewLifecycleOwner) { resource ->
            commonProcessResource(resource) {
                resource.data?.let { data ->
                    helpPromptList = responseToHelpPromptList(data.result.content)
                }
            }
        }
    }

    /**
     * This Network function is used to fetch a similar image to image that is stored with imageName parameter
     * in Firebase storage.
     *
     * @param imageName The image name of a image that a user uploaded to the Firebase
     * @param message The description about the image
     */
    private fun fetchImageRelatedness(imageName: String, message: String) {
        viewModel.getImageRelatedness(imageName, message).observe(viewLifecycleOwner) { resource ->
            commonProcessResource(resource) {
                addMessage(
                    type = TYPE_CHAT_RECEIVE,
                    content = resource.data?.description,
                    data = null,
                    hasImage = true,
                    image = resource.data?.image
                )
            }
        }
    }

    /**
     * This network function is used to retrieve a command analyzed with user's message by Brain
     *
     * @param message A message that users sent
     */
    private fun sendNotification(message: String) {
        viewModel.sendNotification(message).observe(viewLifecycleOwner) { resource ->
            commonProcessResource(resource) {
                val apiResponse = resource.data
                when (apiResponse?.result?.program) {
                    TYPE_RESPONSE_MESSAGE -> addMessage(
                        TYPE_CHAT_RECEIVE,
                        apiResponse.result.content.asString
                    )

                    TYPE_RESPONSE_SMS -> addMessage(TYPE_CHAT_WIDGET, TYPE_WIDGET_SMS)
                    TYPE_RESPONSE_BROWSER -> fetchResponseBrowser(apiResponse)
                    TYPE_RESPONSE_CONTACT -> fetchResponseContact(apiResponse)
                    TYPE_RESPONSE_IMAGE -> fetchResponseImage(apiResponse)
                    TYPE_RESPONSE_ALARM -> fetchResponseAlarm(apiResponse)
                    TYPE_RESPONSE_MAIL_READ -> fetchResponseMail(TYPE_RESPONSE_MAIL_READ)
                    TYPE_RESPONSE_MAIL_WRITE -> fetchResponseMail(TYPE_RESPONSE_MAIL_WRITE)
                    TYPE_RESPONSE_MAIL_SEND -> fetchResponseMail(TYPE_RESPONSE_MAIL_SEND)
                    TYPE_RESPONSE_AUTO_TASK -> fetchResponseAutoTask(apiResponse)
                    else -> addMessage(TYPE_CHAT_RECEIVE, apiResponse!!.result.toString())
                }
            }
        }
    }

    /**
     * This function is used to process data from Brain that is for browser.
     */
    private fun fetchResponseBrowser(apiResponse: ApiResponse<CommonResult>) {
        addMessage(TYPE_CHAT_RECEIVE, apiResponse.result.content.asString)
    }

    /**
     * This function is used to process data from Brain that is for image.
     */
    private fun fetchResponseImage(apiResponse: ApiResponse<CommonResult>) {
        val content = apiResponse.result.content.asJsonObject
        viewModel.downloadImageFromFirebase(content["image_name"].asString)
            .observe(viewLifecycleOwner) { resource ->
                commonProcessResource(resource) {
                    addMessage(
                        type = TYPE_CHAT_RECEIVE,
                        content = null,
                        data = null,
                        hasImage = true,
                        image = resource.data
                    )
                }
            }
    }

    /**
     * This function is used to process data from Brain that is for contact.
     */
    private fun fetchResponseContact(apiResponse: ApiResponse<CommonResult>) {
        val contactIds = JSONArray(apiResponse.result.content.asString.replace("'", "\""))
        if (contactIds.length() > 0) {
            addMessage(
                type = TYPE_CHAT_WIDGET,
                content = TYPE_WIDGET_SEARCH_CONTACT,
                data = apiResponse.result.content
            )
        } else {
            addMessage(
                type = TYPE_CHAT_RECEIVE,
                content = "Contacts that you are looking for don't exist.",
            )
        }
    }

    /**
     * This function is used to process data from Brain that is for alarm.
     */
    private fun fetchResponseAlarm(apiResponse: ApiResponse<CommonResult>) {
        val time: Time =
            Converter.stringToTime(apiResponse.result.content.asJsonObject.get("time").asString)
        val label = apiResponse.result.content.asJsonObject.get("label").asString
        val props = ScheduleAlarmProps(time, label)
        val widgetDesc = JsonObject().apply {
            this.addProperty(PROPS_WIDGET_DESC, props.toString())
        }
        addMessage(TYPE_CHAT_WIDGET, TYPE_WIDGET_SCHEDULE_ALARM, widgetDesc)
    }

    /**
     * This function is used to process data from Brain that is for mail.
     */
    private fun fetchResponseMail(type: String) {
        when (type) {
            TYPE_RESPONSE_MAIL_READ -> addMessage(TYPE_CHAT_WIDGET, TYPE_WIDGET_MAIL_READ)
            TYPE_RESPONSE_MAIL_SEND -> addMessage(TYPE_CHAT_WIDGET, TYPE_WIDGET_MAIL_WRITE)
        }
    }

    /**
     * This function is implemented to fetch the AutoTask data from the Firebase Realtime Database.
     * If the 'result' property is present, its value will be displayed.
     * if not, then the value of the 'thoughts' property will be exhibited.
     */
    private fun fetchResponseAutoTask(apiResponse: ApiResponse<CommonResult>) {
        val referenceUrl = apiResponse.result.content.asString
        viewModel.getAutoTaskRealtimeData(
            referenceUrl
        ).observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is ApiResource.Loading -> {
                    showLoading(true)
                    resource.data?.let { data ->
                        if (data.result == null) {
                            data.thoughts?.let { thoughts ->
                                addMessage(
                                    type = TYPE_CHAT_RECEIVE, content = thoughts.speak
                                )
                            }
                        } else {
                            addMessage(
                                type = TYPE_CHAT_RECEIVE, content = data.result
                            )
                        }
                    }
                }

                is ApiResource.Success -> {
                    showLoading(false)
                    addMessage(TYPE_CHAT_RECEIVE, "Task is finished.")
                }

                is ApiResource.Error -> {
                    showLoading(false)
                    resource.message?.let {
                        addErrorMessage(resource.message)
                    } ?: run {
                        addErrorMessage(ERROR_MSG_UNKNOWN_ERROR)
                    }
                }
            }
        }
    }

    /**
     * This interface is a callback function that retrieves the results of chat widgets.
     */
    private fun initChatInterface() {
        chatMessageInterface = object : ChatMessageInterface {
            /**
             * When a user sends sms using SMS Widget, this function is called.
             */
            override fun sentSms(phoneNumber: String, message: String) {
                addMessage(
                    type = TYPE_CHAT_RECEIVE,
                    content = "You sent SMS with belong content.\n\n To: $phoneNumber\n Message: $message",
                )
            }

            /**
             * When a user cancels sms using SMS Widget, this function is called.
             */
            override fun canceledSms() {
                addMessage(
                    type = TYPE_CHAT_RECEIVE,
                    content = "You canceled SMS.",
                )
            }

            /**
             * When a user sends help prompt using help Prompt Widget, this function is called.
             */
            override fun sentHelpPrompt(prompt: String) {
                addMessage(
                    type = TYPE_CHAT_SENT, content = prompt
                )
            }

            /**
             * When a user cancels help prompt using help prompt Widget, this function is called.
             */
            override fun canceledHelpPrompt() {
                addMessage(
                    type = TYPE_CHAT_RECEIVE, content = "You canceled Help prompt."
                )
            }

            /**
             * When a user makes a voice call after searching for contacts, this function is called.
             */
            override fun doVoiceCall(phoneNumber: String) {
                addMessage(
                    type = TYPE_CHAT_RECEIVE, content = "You made a voice call to $phoneNumber"
                )
            }

            /**
             * When a user makes a video call after searching for contacts, this function is called.
             */
            override fun doVideoCall(phoneNumber: String) {
                addMessage(
                    type = TYPE_CHAT_RECEIVE, content = "You made a video call to $phoneNumber"
                )
            }

            /**
             * When a user sends sms with phone number after searching for contacts,
             * this function is called.
             */
            override fun sendSmsWithPhoneNumber(phoneNumber: String) {
                val widgetDesc = JsonObject().apply {
                    this.addProperty(PROPS_WIDGET_DESC, phoneNumber)
                }
                addMessage(
                    type = TYPE_CHAT_WIDGET, content = TYPE_WIDGET_SMS, data = widgetDesc
                )
            }

            /**
             * When a user picks an image to send to Brain to search or compare similarity,
             * this function is called.
             */
            override fun pickImage(isSuccess: Boolean, data: ByteArray?) {
                if (!isSuccess) addErrorMessage("Fail to pick image")

                viewModel.uploadImageToFirebase(data!!).observe(viewLifecycleOwner) { resource ->
                    commonProcessResource(resource) {
                        currentSelectedImage = data
                        currentUploadedImageName = resource.data
                        isImagePicked = true
                    }
                }
            }

            /**
             * When a user sets an alarm by using alarm widget, this function is called.
             */
            override fun setAlarm(hours: Int, minutes: Int, label: String) {
                addMessage(
                    type = TYPE_CHAT_RECEIVE,
                    content = "You set an alarm for $hours:$minutes with the label($label)"
                )
            }

            /**
             * When a user cancels an alarm by using alarm widget, this function is called.
             */
            override fun cancelAlarm() {
                addMessage(
                    type = TYPE_CHAT_RECEIVE, content = "You canceled setting an alarm."
                )
            }

            /**
             * When a user reads mail by using read mail widget, this function is called.
             */
            override fun readMail(from: String, password: String, imap_folder: String) {
                viewModel.readMails(from, password, imap_folder)
                    .observe(viewLifecycleOwner) { resource ->
                        commonProcessResource(resource) {
                            val props = MailsProps(resource.data!!)
                            val widgetDesc = JsonObject().apply {
                                this.addProperty(PROPS_WIDGET_DESC, props.toString())
                            }
                            addMessage(
                                type = TYPE_CHAT_WIDGET,
                                content = TYPE_WIDGET_MAILS,
                                data = widgetDesc
                            )
                        }
                    }
            }

            /**
             * When a user sends mail by using compose mail widget, this function is called.
             */
            override fun sendMail(
                from: String,
                password: String,
                to: String,
                subject: String,
                body: String,
                isInbox: Boolean,
                filename: String,
                fileContent: String
            ) {

                viewModel.sendMail(
                    sender = from,
                    pwd = password,
                    to = to,
                    subject = subject,
                    body = body,
                    to_send = isInbox,
                    filename = filename,
                    file_content = fileContent
                ).observe(viewLifecycleOwner) { resource -> commonProcessResource(resource) {} }
            }

            override fun readMailInDetail(mail: MailModel) {

            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_open_chat_tools -> {
                chatToolsWidget?.toggle()
            }

            R.id.btn_audio_recognition -> {
                //Here are some codes.
            }
        }
    }

    override fun onResume() {
        super.onResume()

        /**
         * when main fragment resumes, search for changed images and contacts in mobile and send the changed data
         * to Brain to train.
         */
        viewModel.trainContacts()
            .observe(viewLifecycleOwner) { resource -> commonProcessResource(resource) {} }
        viewModel.trainImages()
            .observe(viewLifecycleOwner) { resource -> commonProcessResource(resource) {} }
    }
}