package com.margsapp.messageium.VideoCall

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.margsapp.messageium.Model.User
import com.margsapp.messageium.Notifications.*
import com.margsapp.messageium.R
import com.margsapp.messageium.databinding.ActivityPrivacyBinding
import com.margsapp.messageium.databinding.ActivityVideoCallBinding


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoCallBinding



    var username = ""
    var friendsUsername = ""

    var isPeerConnected = false

    var firebaseRef = FirebaseDatabase.getInstance().getReference("Users")

    var tokens = FirebaseDatabase.getInstance().getReference("Tokens")

    var isAudio = true
    var isVideo = true

    var name = ""
    var imageurl = ""

    var apiService: APIService? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        username = FirebaseAuth.getInstance().uid.toString();
        friendsUsername = intent.getStringExtra("userid")!!

        firebaseRef.child(username).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                name = user?.username!!
                imageurl = user.imageUrl!!

            }
        })




        binding.call.setOnClickListener {
            sendCallRequest()
        }





        binding.toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            binding.toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_baseline_mic_24 else R.drawable.ic_baseline_mic_off_24 )
        }

        binding.toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            binding.toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24 )
        }

        setupWebView()
    }



    private fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show()
            return
        }

        firebaseRef.child(friendsUsername).child("videocall").child("incall").setValue(username)
        val query: Query = tokens.orderByKey().equalTo(friendsUsername)
        query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(applicationContext,"Sent Noti", Toast.LENGTH_SHORT).show()

                for (snapshot1 in snapshot.children) {
                    val token = snapshot1.getValue(Token::class.java)
                    val data = VideoCallData(friendsUsername,username, name, imageurl,"true")
                    assert(token != null)
                    val invite = Inviter(data, token!!.token)

                    apiService?.sendRemoteMessage(invite)
                        ?.enqueue(object : Callback<MyResponse?> {
                            override fun onResponse(
                                call: Call<MyResponse?>,
                                response: Response<MyResponse?>
                            )
                            {
                                if (response.code() == 200) {
                                    assert(response.body() != null)
                                    if (response.body()!!.success != 1) {
                                        Toast.makeText(applicationContext,"Failed! Error code 0x08060101", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse?>, t: Throwable) {}
                        })
                }

            }
        })

        firebaseRef.child(friendsUsername).child("videocall").child("isAvailable").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.value.toString() == "true") {
                    listenForConnId()
                }

            }

        })

    }

    private fun listenForConnId() {
        firebaseRef.child(friendsUsername).child("videocall").child("connId").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null)
                    return
                switchToControls()
                callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
            }

        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {

        binding.webView.webChromeClient = object: WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.addJavascriptInterface(JavascriptInterface(this), "Android")

        loadVideoCall()
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        binding.webView.loadUrl(filePath)

        binding.webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    var uniqueId = ""

    private fun initializePeer() {

        uniqueId = getUniqueID()

        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        firebaseRef.child(username).child("videocall").child("incall").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                onCallRequest(snapshot.value as? String)
            }

        })

    }

    private fun onCallRequest(caller: String?) {
        if (caller == null) return

        binding.callLayout.visibility = View.VISIBLE
        binding.incomingCallTxt.text = "$caller is calling..."

        binding.acceptBtn.setOnClickListener {
            firebaseRef.child(username).child("videocall").child("connId").setValue(uniqueId)
            firebaseRef.child(username).child("videocall").child("isAvailable").setValue(true)

            binding.callLayout.visibility = View.GONE
            switchToControls()
        }

        binding.rejectBtn.setOnClickListener {
            firebaseRef.child(username).child("videocall").child("incall").setValue(null)
            binding.callLayout.visibility = View.GONE
        }

    }

    private fun switchToControls() {
        //inputLayout.visibility = View.GONE
        binding.call.visibility = View.INVISIBLE
        binding.callControlLayout.visibility = View.VISIBLE

    }


    private fun getUniqueID(): String {
        return UUID.randomUUID().toString()
    }

    private fun callJavascriptFunction(functionString: String) {
        binding.webView.post { binding.webView.evaluateJavascript(functionString, null) }
    }


    fun onPeerConnected() {
        isPeerConnected = true
    }

    override fun onBackPressed() {

        finish()
    }

    override fun onDestroy() {
        firebaseRef.child(username).child("videocall").setValue(null)
        binding.webView.loadUrl("about:blank")
        super.onDestroy()
    }





}