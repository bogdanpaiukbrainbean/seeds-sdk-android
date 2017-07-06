# Seeds Android SDK
[View Seeds' documentation](https://developers.playseeds.com/docs/android-sdk-setup.html)


[Seeds](http://www.playseeds.com) makes your users more likely to spend money on your digital product through social good. The SDK implements this with an interstitial (popup) system, event tracking analytics, and a recommendation algorithm.

## The following platforms are now available:
- [Unity SDK](https://github.com/therealseeds/seeds-sdk-unity)
- [iOS](https://github.com/therealseeds/seeds-sdk-ios)
- [Android](https://github.com/therealseeds/seeds-sdk-android)
- [API](https://github.com/therealseeds/seeds-public-api)

## Pull requests welcome
We're built on open source and welcome bug fixes and other contributions.

## Download

Gradle:
```groovy
repositories {
  jcenter()
}

dependencies {
  compile 'com.playseeds.android-sdk:0.5.0'
}
```
Or Maven:
```xml
<dependency>
  <groupId>com.playseeds</groupId>
  <artifactId>android-sdk</artifactId>
  <version>0.4.3</version>
  <type>pom</type>
</dependency>
```
Seeds requires Android version 4.1 or higher.

## ProGuard

The Seeds SDK does not currently support ProGuard. If you are using ProGuard please add the following options to your ProGuard Rules:
```
-keep com.playseeds.**
```

## Usage
The Seeds SDK functionality is divided into two parts: [Interstitials](#interstitials_header) , that represent all functionality that is connected to the content Seeds shows in-app, and [Events](#events_header) that represent logging analytics data.

### Initialization
**Seeds must be initialized on the very beginning of the app start.**
If you have an Application subclass, then do the following:
```java
public class YourApplication extends Application {

	@Override
    public void onCreate() {
		...
		Seeds.init(this, YOU_APP_KEY);
	}

}
```
Otherwise,  if you don't have the Application subclass, do the following in your Main activity:
```java
public class YourMainActivity extends Activity {

	@Override
    public void onCreate() {
		...
		Seeds.init(this, YOU_APP_KEY);
	}

}
```
You can find your APP_KEY  in the [Seeds' Dashboard](https://developers.playseeds.com/index.html).

### <a name="interstitials_header"></a>Interstitials

#### General rules:
* InterstitialId is the id of the interstitial, that can be found in your dashboard under the **Campaign Name** section.
* Context is the desription of the place, where you are showing the interstitial in human-readable manner. Please use short, but understandable descriptions (e.g. "level_1", "pause", "app_start").
#### 1) Pre-load the intesrtitial:
**Please note, that every interstitial must be previously loaded before your will attempt to show it.**
You can pre-load interstitial from the any place of your app, but we suggest to pre-load all the interstitials, that you're going to show from your Main Activity. There are two ways to pre-load the interstitial, one if you have the localized price and another if not:
```java
public class YourMainActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		...
		//Interstitials with default price
		Seeds.interstitials().fetch(YOUR_INTERSTITIAL_ID, null);
		Seeds.interstitials().fetch(YOUR_INTERSTITIAL_ID_2, null);

		//Interstitials with localized price
		Seeds.interstitials().fetch(YOUR_INTERSTITIAL_ID_3, null, "YOUR_LOCALIZED_PRICE");
		Seeds.interstitials().fetch(YOUR_INTERSTITIAL_ID_4, null, "YOUR_LOCALIZED_PRICE");
	}

}
```
#### <a name="interstitials_listener_usage">2) Set the listener:
To receive the callbacks about events from the SDK, such as notifications about clicks, dismiss, errors, loading, please use the implementation of the [InterstitialListener](#interstitials_listener), or InterstitialListenerAdapter (overriding only the methods you need). In general, you can set the listener in the any place you want, but we suggest, as there can be only one listener, to set it in the place, that will be active at the time of showing interstitial. For example:
```java
public class SomeActivity extends Activity implements InterstitialListener {
	
	@Override
    protected void onResume() {
		...
		Seeds.interstitials().setListener(this);
	}

	@Override
    protected void onResume() {
		...
		Seeds.interstitials().setListener(null); //Used to clear the listener
	}

	@Override
    public void onLoaded(SeedsInterstitial seedsInterstitial) {
   		//Called when the interstitial was loaded
    }

    @Override
    public void onClick(SeedsInterstitial seedsInterstitial) {
		//Called when the interstitial "buy" button was clicked
    }

    @Override
    public void onShown(SeedsInterstitial seedsInterstitial) {
		//Called when the interstitial was successfully shown
    }

    @Override
    public void onDismissed(SeedsInterstitial seedsInterstitial) {
		//Called when the user presses "close" or "back" button
    }

    @Override
    public void onError(String interstitialId, Exception exception) {
	    //Called when any some error occures
    }
}
```
#### 3) Show the Interstitial:
To show the interstitial please do the following:


- At first, check if the interstitial is loaded. **Please note, that onError() method of the listener will be called also if the interstitial wasn't previously fetched (loaded)**.
- If the interstitial is loaded - show it.

Sample usage:
```java
public class SomeActivity extends Activity {

	public void someMethod(){
		...
		if (Seeds.interstitials().isLoaded(YOUR_INTERSTITIAL_ID)){

			Seeds.interstitials().show(YOUR_INTERSTITIAL_ID, YOUR_INTERSTITIAL_CONTEXT);
		}	
	}

}
```
#### <a name="interstitials_listener"></a>IterstitialsListener
The interface contains five methods for treating different scenarios connected to the usage of Seeds.
Instructions, how to operate with this listener, is [available above](#interstitials_listener_usage)
```java
void onLoaded(SeedsInterstitial seedsInterstitial); //Called when the interstitial was loaded
void onClick(SeedsInterstitial seedsInterstitial); //Called when the interstitial "buy" button was clicked
void onShown(SeedsInterstitial seedsInterstitial); //Called when the interstitial was successfully shown
void onDismissed(SeedsInterstitial seedsInterstitial); //Called when the user presses "close" or "back" button
void onError(String interstitialId, Exception exception); //Called when any some error occures
```


#### <a name="seeds_interstitial"></a>SeedsInterstitial
This is the object-wrapper for sharing info about interstitials. As of the most recent release, it contains:
* InterstitialId;
* Context;

### <a name="events_header"></a>Events

An event is the generalized mechanism for tracking all user actions taken in-app. **Seeds.Events** use two approaches for logging data: direct logging for purchases made, and an object-based approach for tracking all other data. Use **logSeedsIAPPayment()** or **logIAPPayment()** after any successful purchase, and **logUser()** with the provided wrapper to empower Seeds to make the targeted recommendations that will best convert your non-payers into paying customers. There is also an additional way to log your app's custom data, **logEvent()**, which uses the object-based approach with custom attributes.

#### After successful in-app purchase:
**This method should be called after any successful purchase in the app to help the to Seeds track the purchases and generate useful tips, statistic and issue correct invoices.**
Depending of the type of purchase (whether it was with Seeds-promoted purchase or usual one), notify the SDK about it:
```java
public class SomeActivity extends Activity {

	public void someMethod(){
		...
		//Successful purchase was made above
		Seeds.events().logSeedsIAPPayment(YOUR_PURCHASE_KEY, YOUR_PURCHASE_PRICE, YOUR_TRANSACTION_ID); //If there was a Seeds-promoted purchase
		Seeds.events().logSeedsIAPPayment(YOUR_PURCHASE_KEY, YOUR_PURCHASE_PRICE, YOUR_TRANSACTION_ID); //If there was ususal purchase.
	}

}
```

#### After generating user data:
- Send the user info. This is recommended, because your users info are helping Seeds to provide you the best possible strategy for advertising users.
**UserInfo is the predefined object, that provide all supported parameters**:
```java
public class SomeActivity extends Activity {

	public void someMethod(){
		...
		//User info is available here
		UserInfo userInfo = new UserInfo();
		userInfo.setName("John");
		userInfo.setEmail("example_email@example.com");
		//Any other parameters that are available goes here
		...
		Seeds.events().logUser(userInfo);
	}

}
```
- Track common events using the predefined **Event** class or creating the subclass of it and using **putAttribute(String key, String value)**:
```java
Seeds.events().logEvent(Event event);
```

## License

    Copyright (c) 2012, 2013 Countly

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
