/*
 *		Copyright 2015 MobFox
 *		Licensed under the Apache License, Version 2.0 (the "License");
 *		you may not use this file except in compliance with the License.
 *		You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *		Unless required by applicable law or agreed to in writing, software
 *		distributed under the License is distributed on an "AS IS" BASIS,
 *		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *		See the License for the specific language governing permissions and
 *		limitations under the License.
 *
 */

package ly.count.android.sdk.inappmessaging;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;

public abstract class RequestAd<T> {

	InputStream is;

	public T sendRequest(AdRequest request)
			throws RequestException {
		if (is == null) {
			Log.d("Parse Real");
			String url = request.toString();
			Log.d("Ad RequestPerform HTTP Get Url: " + url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setSoTimeout(client.getParams(),
					Const.SOCKET_TIMEOUT);
			HttpConnectionParams.setConnectionTimeout(client.getParams(),
					Const.CONNECTION_TIMEOUT);
			HttpProtocolParams.setUserAgent(client.getParams(),
					request.getUserAgent());
			HttpGet get = new HttpGet(url);
			get.setHeader("User-Agent", System.getProperty("http.agent"));
			HttpResponse response;
			try {
				response = client.execute(get);
				int responseCode = response.getStatusLine().getStatusCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					return parse(response.getEntity().getContent(), response.getAllHeaders()/*, request.isVideoRequest()*/);
				} else {
					throw new RequestException("Server Error. Response code:"
							+ responseCode);
				}
			} catch (RequestException e) {
				throw e;
			} catch (ClientProtocolException e) {
				throw new RequestException("Error in HTTP request", e);
			} catch (IOException e) {
				throw new RequestException("Error in HTTP request", e);
			} catch (Throwable t) {
				throw new RequestException("Error in HTTP request", t);
			}
		} else {
			Log.d("Parse Injected");
			return parseTestString();
		}
	}

	abstract T parseTestString() throws RequestException;

	abstract T parse(InputStream inputStream, Header[] headers/*, boolean isVideo*/) throws RequestException;

}
