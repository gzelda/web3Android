package com.example.web3view.model;

public class ApproveTransactionCallback {

    private boolean result;

    public ApproveTransactionCallback setResult(final boolean result) {
        this.result = result;
        return this;
    }


    public String toJsonEncodedString() {
        return String.format(
                "{\\\"result\\\":%s}",
                String.valueOf(result)
        );
    }
}