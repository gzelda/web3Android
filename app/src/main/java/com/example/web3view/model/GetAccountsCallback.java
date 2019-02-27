package com.example.web3view.model;

public class GetAccountsCallback {

    private String result;

    public GetAccountsCallback setResult(final String result) {
        this.result = result;
        return this;
    }


    public String toJsonEncodedString() {
        return String.format(
                "{\\\"result\\\":[\\\"%s\\\"]}",
                result
        );
    }
}