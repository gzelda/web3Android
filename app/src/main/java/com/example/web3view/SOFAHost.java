package com.example.web3view;

import android.webkit.JavascriptInterface;

public class SOFAHost {

    public SofaHostWrapper sofaWrapper;

    public SOFAHost(SofaHostWrapper sofaWrapper) {
        this.sofaWrapper = sofaWrapper;
    }

    @JavascriptInterface
    public void getAccounts(final String id) {

        System.out.println("in!!!!!!!:"+id);
        sofaWrapper.getAccountsWrapper(id);
    }
    @JavascriptInterface
    public void approveTransaction(final String id, final String unsignedTransaction) {
        System.out.println("in!!!!!!!:"+id+" "+unsignedTransaction);
    }

    @JavascriptInterface
    public void signTransaction(final String id, final String unsignedTransaction) {
        System.out.println("in sign!!!!!!!:"+id+" "+unsignedTransaction);
    }

    @JavascriptInterface
    public void publishTransaction(final String id, final String signedTransaction) {
        System.out.println("in publish!!!!!!!:"+id+" "+signedTransaction);
    }

    @JavascriptInterface
    public void signPersonalMessage(final String id, final String msgParams) {
        System.out.println("in signPersonal!!!!!!!:"+id+" "+msgParams);
        sofaWrapper.signPersonalMessageWrapper(id,msgParams);
    }

    @JavascriptInterface
    public void signMessage(final String id, final String from, final String data) {
        System.out.println("in signMessage!!!!!!!:"+id+" "+from+" "+data);
    }
}
