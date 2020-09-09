package com.restbusters.core.testng.listener;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public class InvokedMethodListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod() && ITestResult.FAILURE == result.getStatus()) {
            Throwable throwable = result.getThrowable();
            String originalMessage = throwable.getMessage();
            String newMessage = originalMessage + "\nReproduction Seed: ...";
            try {
                FieldUtils.writeField(throwable, "detailMessage", newMessage, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}