package com.margsapp.messenger.VideoCall

import android.webkit.JavascriptInterface

class JavascriptInterface(val callActivity: CallActivity) {
    @JavascriptInterface
    public fun onPeerConnected() {
        callActivity.onPeerConnected()
    }
}