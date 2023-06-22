package com.matthaigh27.chatgptwrapper.ui.chat.view.widgets.toolbar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.GridLayout
import android.widget.LinearLayout
import com.matthaigh27.chatgptwrapper.R
import com.matthaigh27.chatgptwrapper.ui.chat.view.interfaces.ChatMessageInterface
import com.matthaigh27.chatgptwrapper.ui.setting.SettingActivity
import com.matthaigh27.chatgptwrapper.utils.helpers.ImageHelper
import com.qw.photo.CoCo
import com.qw.photo.callback.CoCoAdapter
import com.qw.photo.callback.CoCoCallBack
import com.qw.photo.constant.Range
import com.qw.photo.pojo.PickResult
import com.qw.photo.pojo.TakeResult
import java.io.FileNotFoundException

class ChatToolsWidget(context: Context, parentActivity: Activity, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs), View.OnClickListener {
    private val TOOL_COUNT = 3

    private val TOOL_CAMERA = "Camera"
    private val TOOL_GALLERY = "Gallery"
    private val TOOL_Setting = "Settomg"

    private val TOOL_ICONS = arrayOf(
        R.drawable.ic_camera, R.drawable.ic_gallery, R.drawable.ic_cog
    )
    private val TOOL_NAMES = arrayOf(
        TOOL_CAMERA, TOOL_GALLERY, TOOL_Setting
    )

    private var context: Context
    private var parentActivity: Activity

    private lateinit var mGlTools: GridLayout
    private var isInitFlag = true

    var callback: ChatMessageInterface? = null

    init {
        this.context = context
        this.parentActivity = parentActivity

        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.widget_chat_tools, this, true)

        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        mGlTools = findViewById(R.id.gl_tools)

        this.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (!isInitFlag) return@addOnLayoutChangeListener
            isInitFlag = false

            val width: Int = this.width

            for (i in 0 until TOOL_COUNT) {
                val toolItem = ChatToolItem(context)
                val itemMargin = context.resources.getDimensionPixelSize(R.dimen.spacing_small)
                toolItem.setTool(TOOL_ICONS[i], width / 4 - itemMargin * 2, TOOL_NAMES[i])
                mGlTools.addView(toolItem)

                toolItem.tag = TOOL_NAMES[i]
                toolItem.setOnClickListener(this@ChatToolsWidget)
            }
        }

        visibility = View.GONE
    }

    override fun onClick(view: View?) {
        when (view?.tag) {
            TOOL_CAMERA -> {
                CoCo.with(parentActivity).take(ImageHelper.createSDCardFile())
                    .start(object : CoCoAdapter<TakeResult>() {
                        override fun onSuccess(data: TakeResult) {
                            val byteArray: ByteArray =
                                ImageHelper.getBytesFromPath(data.savedFile!!.absolutePath)
                            callbackImagePicker(byteArray)
                        }

                        override fun onFailed(exception: Exception) {

                        }
                    })
            }

            TOOL_GALLERY -> {
                CoCo.with(parentActivity).pick().range(Range.PICK_CONTENT)
                    .start(object : CoCoCallBack<PickResult> {

                        override fun onSuccess(data: PickResult) {
                            val byteArray: ByteArray? =
                                ImageHelper.convertImageToByte(data.originUri)
                            callbackImagePicker(byteArray)
                        }

                        override fun onFailed(exception: Exception) {
                            callback?.pickImage(isSuccess = false)
                        }
                    })
            }

            TOOL_Setting -> {
                context.startActivity(Intent(context, SettingActivity::class.java))
            }
        }
    }

    fun callbackImagePicker(byteArray: ByteArray?) {
        try {
            if (byteArray != null) {
                callback?.pickImage(isSuccess = true, byteArray)
            } else {
                callback?.pickImage(isSuccess = false)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            callback?.pickImage(isSuccess = false)
        }
    }


    fun toggle() {
        if (visibility == View.VISIBLE) {
            setVisibility(false)
        } else {
            setVisibility(true)
        }
    }

    private fun setVisibility(isVisible: Boolean) {
        var startAlphaValue = 0f
        var endAlphaValue = 0f

        if (isVisible) {
            visibility = View.VISIBLE
            endAlphaValue = 1f
        } else {
            visibility = View.GONE
            startAlphaValue = 1f
        }

        val anim = AlphaAnimation(startAlphaValue, endAlphaValue).apply {
            duration = 200 // Set the animation duration, e.g., 200ms
            interpolator = AccelerateDecelerateInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    visibility = if (isVisible) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }

        startAnimation(anim)
    }
}