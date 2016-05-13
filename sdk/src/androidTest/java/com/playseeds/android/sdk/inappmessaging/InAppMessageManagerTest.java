package com.playseeds.android.sdk.inappmessaging;

import android.content.Context;
import android.test.AndroidTestCase;

import com.playseeds.android.sdk.DeviceId;
import com.playseeds.android.sdk.Seeds;

import java.util.Date;
import java.util.HashMap;

public class InAppMessageManagerTest extends AndroidTestCase {
    InAppMessageResponse inAppMessageResponse;
    testInAppMessageListener inAppMessageListener;
    HashMap<Long, InAppMessageManager> runningAds;
    Long timeStamp;
    String serverUrl;
    Context context;

    public void setUp() throws Exception {
        timeStamp = new Date().getTime();
        inAppMessageResponse = new InAppMessageResponse();
        inAppMessageListener = new testInAppMessageListener();
        runningAds = new HashMap<>();
        context = getContext();
        serverUrl = "http://devdash.playseeds.com";

        inAppMessageResponse.setTimestamp(timeStamp);
        runningAds.put(timeStamp, InAppMessageManager.sharedInstance());
        InAppMessageManager.sharedInstance().setRunningAds(runningAds);
        InAppMessageManager.sharedInstance().setListener(inAppMessageListener);
        Seeds.sharedInstance().init(context, inAppMessageListener, serverUrl, "12345");
        Seeds.sharedInstance().setMessageVariantName("Test message");
        InAppMessageManager.sharedInstance().init(context, serverUrl, "c30f02a55541cbe362449d29d83d777c125c8dd6", "Nexus-XLR", DeviceId.Type.ADVERTISING_ID);
    }

    public void testNotifyInAppMessageClick() throws Exception {
        InAppMessageManager.notifyInAppMessageClick(inAppMessageResponse);
        synchronized (inAppMessageListener) {
            inAppMessageListener.wait(1000);
        }
        assertTrue(inAppMessageListener.isClicked);
    }

    public void testCloseRunningInAppMessage() throws Exception {
        InAppMessageManager.closeRunningInAppMessage(inAppMessageResponse, false);
        synchronized (inAppMessageListener) {
            inAppMessageListener.wait(1000);
        }
        assertTrue(inAppMessageListener.isClosed);
    }

    public void testRequestInAppMessage_WhenBadRequest() throws Exception {
        InAppMessageManager.sharedInstance().init(context, "http://dev.playseeds.co", "1234", "Nexus-XLR", DeviceId.Type.ADVERTISING_ID);
        InAppMessageManager.sharedInstance().requestInAppMessage();
        synchronized (inAppMessageListener) {
            inAppMessageListener.wait(50000);
        }
        assertTrue(inAppMessageListener.notFound);
    }

    public void testRequestInAppMessage() throws Exception {
        InAppMessageManager.sharedInstance().requestInAppMessage();
        synchronized (inAppMessageListener) {
            inAppMessageListener.wait(10000);
        }
        assertTrue(inAppMessageListener.isLoadSucceeded);
    }

    private class testInAppMessageListener implements InAppMessageListener {
        boolean isClicked = false;
        boolean isClosed = false;
        boolean isLoadSucceeded = false;
        boolean isShown = false;
        boolean notFound = false;

        @Override
        public void inAppMessageClicked() {
            isClicked = true;
        }

        @Override
        public void inAppMessageClosed(InAppMessage inAppmessage, boolean completed) {
            isClosed = true;
        }

        @Override
        public void inAppMessageShown(InAppMessage inAppMessage, boolean succeeded) {
            isShown = true;
        }

        @Override
        public void inAppMessageLoadSucceeded(InAppMessage inAppMessage) {
            isLoadSucceeded = true;
        }

        @Override
        public void noInAppMessageFound() {
            notFound = true;
        }
    }
}
