package com.example.web3view;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Math.min;


public class MainActivity extends AppCompatActivity {

    private WebView webView;
    public SofaHostWrapper sofaHostWrapper ;
    private OkHttpClient okHttpClient_get;
    private String final_url;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            //System.out.println("tygavin"+val);
            injectBody(val);
        }
    };

    public void initWebview(){
        System.out.println("in init");

        //无关紧要的一些设置
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//允许使用弹窗
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        //https://danfinlay.github.io/js-eth-personal-sign-examples/
        //https://instadapp.io/dapp
        //webView.setVisibility(View.INVISIBLE);

        //webView.loadUrl("https://instadapp.io/dapp");
        //webView.loadUrl("<script type=\"text/javascript\">web3 = 666</script>");
        sofaHostWrapper = new SofaHostWrapper(this,webView);
        webView.addJavascriptInterface(new SOFAHost(sofaHostWrapper), "SOFAHost");
    }

    private void injectBody(String body){


        String data =  loadInjections();
        String result = injectScript(body,data);

        System.out.println("tydebug:"+result.length());
        //System.out.println("tydebug:"+result);
        //webView.loadUrl(final_url);
        webView.loadDataWithBaseURL(final_url,result,null, null,final_url);
    }

    private String loadInjections(){
        StringBuilder inject_str = new StringBuilder();

        String userAddress = "0x47B9Be7A0FC74Be3fccdECfC6d41d21D24D4a672";
        //String inject_SOFA = "window.SOFA = {config: {netVersion: '" + "1" + "', accounts: ['"+userAddress+"'], rcpUrl: '" + "https://mainnet.infura.io" + "'}}";
        String inject_SOFA = "window.SOFA = {" +
                "config: {netVersion: \"1\", " +
                "accounts: [\"" + userAddress + "\"]," +
                "rcpUrl: \"https://mainnet.infura.io\"}" +
                "};";

        inject_str.append(inject_SOFA).append("\n");

        InputStream stream =getResources().openRawResource(R.raw.sofaweb3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            String strLine = null;
            while((strLine =  reader.readLine()) != null) {
                inject_str.append (strLine).append("\n");

                //System.out.println("fuck!!!!!!!!!!!!!:"+strLine.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "<script type=\"text/javascript\">"+ inject_str.toString() +"</script>";
    }

    private String injectScript(String body,String data){
        //System.out.println("injectScript:"+body);
        String safeBody = body!=null ?body: "";
        int position = getInjectionPosition(safeBody);
        System.out.println("position:"+position);

        String beforeTag = safeBody.substring(0, position);
        String afterTab = safeBody.substring(position);
        System.out.println("tydebug:"+body.length()+" "+beforeTag.length()+" "+data.length()+" "+afterTab.length());
        //System.out.println("script:"+beforeTag+" tydebug "+afterTab);
        String result = beforeTag + data + afterTab;
        //System.out.println("script:"+result);
        return result;

    }

    private int getInjectionPosition(String body){
        int ieDetectTagIndex = body.indexOf("<!--[if",0);
                //body.indexOf("<!--[if", 0, true);
        int scriptTagIndex = body.indexOf("<script", 0);
        //System.out.println("index:"+ieDetectTagIndex+" "+scriptTagIndex);
        return ieDetectTagIndex < 0? scriptTagIndex : min(scriptTagIndex, ieDetectTagIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webview);
        initWebview();
        //OkHttpClient okHttpClient = new OkHttpClient();
        //System.out.println("in");

        OkHttpClient okHttpClient_get = new OkHttpClient();
        // Android 4.0 之后不能在主线程中请求HTTP请求
        //final_url = "http://192.168.0.47:8080";
        //final_url = "https://instadapp.io/dapp";
        final_url = "https://hyperdragons.alfakingdom.com/";
        //final_url = "https://www.baidu.com";
        new Thread(new Runnable(){
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .get()//get请求方式
                        .url(final_url)//网址
                        .build();
                Response response = null;
                try {
                    response = okHttpClient_get.newCall(request).execute();

                    if (response.isSuccessful()) {
                        // 打印数据
                        //System.out.println(response.body().string());
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        String body = response.body().string();
                        data.putString("value",body);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //finalUrl = "file:///android_asset/index.html";
        //webView.loadDataWithBaseURL(finalUrl,data,null,null,finalUrl);

        //webView.loadUrl("file:///android_asset/index.html");
        //webView.loadUrl("https://hyperdragons.alfakingdom.com/");
        //webView.loadUrl("https://cryptocrystal.io");
    }



    private WebViewClient webViewClient=new WebViewClient()
    {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成

            super.onPageFinished(view, url);
            //injectScriptFile(view, "js/test.js");
            //view.evaluateJavascript("javascript:web3=666",null);

            //view.setVisibility(View.VISIBLE);
            System.out.println("fuck in finish");
        }




        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            //progressBar.setVisibility(View.VISIBLE);

            //injectScriptFile(view, "js/test.js");

//

            //System.out.println("fuck in start");
            System.out.println("fuck in start");
            super.onPageStarted(view, url,favicon);


        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
            System.out.println("in this method");
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }
    };

    private void injectScriptFile(WebView view, String scriptFile) {
        InputStream input;
        //String userAddress = "0x726725cBaE07643E813Ec13Ba3e81C49A322cd91";

        String userAddress = "0x47B9Be7A0FC74Be3fccdECfC6d41d21D24D4a672";
        //String inject_SOFA = "window.SOFA = {config: {netVersion: '" + "1" + "', accounts: ['"+userAddress+"'], rcpUrl: '" + "https://mainnet.infura.io" + "'}}";
        String inject_SOFA = "window.SOFA = {" +
                "config: {netVersion: \"1\", " +
                "accounts: [\"" + userAddress + "\"]," +
                "rcpUrl: \"https://mainnet.infura.io\"}" +
                "};";
        byte [] head_SOFA = inject_SOFA.getBytes();


        try {
            input = getAssets().open(scriptFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();


            byte[] result = new byte[head_SOFA.length + buffer.length];
            System.arraycopy(head_SOFA, 0, result, 0, head_SOFA.length);
            System.arraycopy(buffer, 0, result, head_SOFA.length, buffer.length);
            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(result, Base64.NO_WRAP);
            view.evaluateJavascript("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()",null);
            System.out.println("finish injecting");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            //可做可不做的一些拦截，具体可以参考麦子钱包的界面
            //网页的一些原生alert弹出就是通过此方法拦截的
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ty","网页标题:"+title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //injectScriptFile(view, "js/sofaweb3.js");
            System.out.println("tydebug progress:"+newProgress);
        }



        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            System.out.println("markdebug :" + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);

        }
    };
}
