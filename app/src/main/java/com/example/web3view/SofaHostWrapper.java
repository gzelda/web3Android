package com.example.web3view;

import android.os.Build;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.web3view.model.GetAccountsCallback;
import com.example.web3view.utils.HttpRequest;

public class SofaHostWrapper {
    private WebView webView;
    private AppCompatActivity activity;

    public SofaHostWrapper(final AppCompatActivity activity, final WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    private void doCallback(final String id, final String encodedCallback) {
        if (activity == null) return;
        final String methodCall = String.format("SOFA.callback(\"%s\",\"%s\")", id, encodedCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //webView.evaluateJavascript(methodCall, null);
            webView.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("tydebug in java webview");
                    webView.evaluateJavascript("javascript:" + methodCall, null);

                }
            });
        } else {
            //webView.loadUrl("javascript:" + methodCall);
            webView.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("tydebug in java webview");
                    webView.loadUrl("javascript:" + methodCall);
                }
            });

        }
    };

        private void executeJavascriptMethod (String methodCall){

        }

        public void getAccountsWrapper (String id){
            System.out.println("in getAccountsWrapper()!!!!!!!:" + id);
            String paymentAddress = "0x47B9Be7A0FC74Be3fccdECfC6d41d21D24D4a672";
            final GetAccountsCallback callback = new GetAccountsCallback().setResult(paymentAddress);
            doCallback(id, callback.toJsonEncodedString());
        }

        public void approveTransactionWrapper () {

        }

        public void publishTransactionWrapper () {

        }

        public void signPersonalMessageWrapper (String id,String msg) {
            String path = "http://127.0.0.1:4000/eth/web3/signPersonalMessage";
            //参数为UID，后续前端不需要输入参数，只需要传cookie即可
            String sr = HttpRequest.sendPost(path,"Message="+msg);
            System.out.println("POST response:" + sr);


        }

        public void signTransactionWrapper () {

        }

        public void signMessageWrapper () {

        }

}
