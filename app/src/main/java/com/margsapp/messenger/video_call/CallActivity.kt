package com.margsapp.messenger.video_call

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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.margsapp.messenger.R
import kotlinx.android.synthetic.main.activity_video_call.*
import java.util.*

class CallActivity : AppCompatActivity() {

    var username = ""
    var friendsUsername = ""

    var isPeerConnected = false

    var firebaseRef = FirebaseDatabase.getInstance().getReference("Users")

    var isAudio = true
    var isVideo = true




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)


        username = FirebaseAuth.getInstance().uid.toString();
        friendsUsername = intent.getStringExtra("userid")!!




        call.setOnClickListener {
            sendCallRequest()
        }





        toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_baseline_mic_24 else R.drawable.ic_baseline_mic_off_24 )
        }

        toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24 )
        }

        setupWebView()
    }



    private fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show()
            return
        }

        firebaseRef.child(friendsUsername).child("videocall").child("incall").setValue(username)
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

        webView.webChromeClient = object: WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(JavascriptInterface(this), "Android")

        loadVideoCall()
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        webView.loadUrl(filePath)

        webView.webViewClient = object: WebViewClient() {
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

        callLayout.visibility = View.VISIBLE
        incomingCallTxt.text = "$caller is calling..."

        acceptBtn.setOnClickListener {
            firebaseRef.child(username).child("videocall").child("connId").setValue(uniqueId)
            firebaseRef.child(username).child("videocall").child("isAvailable").setValue(true)

            callLayout.visibility = View.GONE
            switchToControls()
        }

        rejectBtn.setOnClickListener {
            firebaseRef.child(username).child("videocall").child("incall").setValue(null)
            callLayout.visibility = View.GONE
        }

    }

    private fun switchToControls() {
        //inputLayout.visibility = View.GONE
        call.visibility = View.INVISIBLE
        callControlLayout.visibility = View.VISIBLE

    }


    private fun getUniqueID(): String {
        return UUID.randomUUID().toString()
    }

    private fun callJavascriptFunction(functionString: String) {
        webView.post { webView.evaluateJavascript(functionString, null) }
    }


    fun onPeerConnected() {
        isPeerConnected = true
    }

    override fun onBackPressed() {

        finish()
    }

    override fun onDestroy() {
        firebaseRef.child(username).child("videocall").setValue(null)
        webView.loadUrl("about:blank")
        super.onDestroy()
    }



}