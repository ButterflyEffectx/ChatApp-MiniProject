package com.example.chatover_android

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zegocloud.zimkit.common.ZIMKitRouter
import com.zegocloud.zimkit.common.enums.ZIMKitConversationType
import com.zegocloud.zimkit.services.ZIMKit
import com.zegocloud.zimkit.services.callback.CreateGroupCallback
import com.zegocloud.zimkit.services.model.ZIMKitGroupInfo
import im.zego.zim.entity.ZIMError
import im.zego.zim.entity.ZIMErrorUserInfo
import java.util.ArrayList

class ConversationActivity : AppCompatActivity() {

    lateinit var newChat: Button
    lateinit var groupChat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_conversation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        newChat = findViewById(R.id.new_chat)
        groupChat = findViewById(R.id.group_chat)

        newChat.setOnClickListener {
            val userID = EditText(this)
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.enter_user_id))
            builder.setView(userID)
            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                val userId = userID.text.toString()
                if (userId.isNotEmpty()) {
                    connectToUser(userId, ZIMKitConversationType.ZIMKitConversationTypePeer)
                }
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            builder.create().show()
        }
        groupChat.setOnClickListener {
            val groupName = EditText(this)
            val groupID = EditText(this)
            val userIds = EditText(this)

            val builder = AlertDialog.Builder(this)
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.addView(groupName)
            linearLayout.addView(groupID)
            linearLayout.addView(userIds)
            builder.setTitle(getString(R.string.create_group_chat))
            builder.setView(linearLayout)
            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                val name = groupName.text.toString()
                val id = groupID.text.toString()
                val users = userIds.text.toString().split(",").toList()
                if (name.isNotEmpty() && id.isNotEmpty() && users.isNotEmpty()) {
                    createGroupChat(
                        name,
                        id,
                        users,
                        ZIMKitConversationType.ZIMKitConversationTypeGroup
                    )
                }
            }
            builder.create().show()
        }


    }
    fun connectToUser(userId: String, type: ZIMKitConversationType) {
        ZIMKitRouter.toMessageActivity(this,userId,type)
    }
    fun createGroupChat(
        name: String,
        id: String,
        users: List<String>,
        type: ZIMKitConversationType
    ){
        ZIMKit.createGroup(name, id, users, object : CreateGroupCallback {
            override fun onCreateGroup(
                groupInfo: ZIMKitGroupInfo?,
                inviteUserErrors: ArrayList<ZIMErrorUserInfo>?,
                error: ZIMError?
            ) {
                if (error == null) {
                    ZIMKitRouter.toMessageActivity(this@ConversationActivity, id, type)
                } else {
                    val msg = error.message ?: "Unknown error"
                    runOnUiThread {
                        AlertDialog.Builder(this@ConversationActivity)
                            .setTitle(R.string.error)
                            .setMessage(msg)
                            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }
        })
    }
}