package org.jfritz.fboxlib.internal.helper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jfritz.fboxlib.exceptions.InvalidSessionIdException;
import org.jfritz.fboxlib.exceptions.PageNotFoundException;
import org.jfritz.fboxlib.fritzbox.FritzBoxCommunication;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

public class HttpHelper {

    private final static String USER_AGENT = "JFritz-Client";
    private final static int TIMEOUT_CONNECTION = 5000;
    private final static int TIMEOUT_READ = 120000;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_READ).setConnectTimeout(TIMEOUT_CONNECTION).build();

    private static HttpHelper INSTANCE = new HttpHelper();

    private FritzBoxCommunication fbc = null;

    public static HttpHelper getInstance(final FritzBoxCommunication fbc) {
        INSTANCE.fbc = fbc;
        return INSTANCE;
    }

    private HttpHelper() {
        // private constructor to prevent instantiation
    }

    public String getHttpContentAsString(String url) throws IOException, PageNotFoundException, InvalidSessionIdException {
        String result = "";

        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(requestConfig);
        httpget.addHeader("User-Agent", USER_AGENT);

        try {
            CloseableHttpResponse response = getResponse(url, httpget);
            result = extractEntityToString(response);
        } catch (ClientProtocolException e) {
            result = e.getMessage();
            throw e;
        } catch (IOException e) {
            result = e.getMessage();
            throw e;
        }
        return result;
    }

    private CloseableHttpResponse getResponse(String url, HttpRequestBase httpRequestBase) throws IOException, PageNotFoundException {
        CloseableHttpResponse response = httpClient.execute(httpRequestBase);
        checkFor404(url, response);

        if (response.getStatusLine().getStatusCode() != 401) {
            return response;
        } else {
            return retryWithDigestAuth(url, httpRequestBase, response);
        }
    }

    private void checkFor404(String url, CloseableHttpResponse response)
            throws IOException, PageNotFoundException {
        if (response.getStatusLine().getStatusCode() == 404) {
            response.close();
            throw new PageNotFoundException("404 Not Found: " + url);
        }
    }

    private CloseableHttpResponse retryWithDigestAuth(String url,
                                                      HttpRequestBase httpRequestBase, CloseableHttpResponse response)
            throws IOException {
        response.close();

        HttpClientContext context = HttpClientContext.create();
        addDigestAuth(url, httpRequestBase, context);

        CloseableHttpResponse newResponse = httpClient.execute(httpRequestBase, context);
        if (newResponse.getStatusLine().getStatusCode() == 401) {
            newResponse.close();
            // nothing to do, we are not authorized to get the response
        }
        return newResponse;
    }

    private void addDigestAuth(String urlString, HttpRequestBase httpRequestBase, HttpClientContext context) throws MalformedURLException {
        URL url = new URL(urlString);

        HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(fbc.getUserName(), fbc.getPassword()));
        AuthCache authCache = new BasicAuthCache();
        DigestScheme digestScheme = new DigestScheme();
        digestScheme.overrideParamter("realm", "HTTPS Access");
        digestScheme.overrideParamter("nonce", "682F65CF66A5577A");

        authCache.put(targetHost, digestScheme);
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
    }

    private String extractEntityToString(CloseableHttpResponse response) throws IOException, InvalidSessionIdException {
        String result = "";
        HttpEntity entity = response.getEntity();
        Charset charset = getCharsetFromResponse(entity);

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                result = EntityUtils.toString(entity, charset);
            } finally {
                instream.close();
            }
        }
        response.close();

        checkForInvalidSid(result);
        return result;
    }

    private Vector<String> extractEntityToVector(CloseableHttpResponse response) throws IOException, InvalidSessionIdException {
        Vector<String> result = new Vector<String>();
        HttpEntity entity = response.getEntity();
        Charset charset = getCharsetFromResponse(entity);

        BufferedReader br;
        String line = "";

        if (entity != null) {
            br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
            try {
                while ((line = br.readLine()) != null) {
                    result.add(line);
                }
                // do something useful
            } finally {
                br.close();
            }
        }

        response.close();

        checkForInvalidSid(result);
        return result;
    }

    private Charset getCharsetFromResponse(HttpEntity entity) {
        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        return charset;
    }

    private void checkForInvalidSid(final String result) throws InvalidSessionIdException {
        try {
            JSONObject o = (JSONObject) JSONValue.parse(result);
            if (o != null) {
                String pid = (String) o.get("pid");
                if ("logout".equals(pid)) {
                    throw new InvalidSessionIdException();
                }
            }
        } catch (ClassCastException e) {
            // nothing to do here, just ignore a non JSON response
        }
    }

    private void checkForInvalidSid(final Vector<String> result) throws InvalidSessionIdException {
        checkForInvalidSid(result.toString());
    }

    public String postToHttpAndGetAsString(String url, List<NameValuePair> urlParameters) throws IOException, PageNotFoundException, InvalidSessionIdException {
        String result = "";

        HttpPost httpPost = null;
        try {
            httpPost = preparePostRequest(url, urlParameters);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            throw new PageNotFoundException("unsupported encoding exception");
        }

        try {
            CloseableHttpResponse response = getResponse(url, httpPost);

            checkFor404(url, response);
            result = extractEntityToString(response);
        } catch (ClientProtocolException e) {
            result = e.getMessage();
            throw e;
        } catch (IOException e) {
            result = e.getMessage();
            throw e;
        }
        return result;
    }

    private HttpPost preparePostRequest(String url,
                                        List<NameValuePair> urlParameters)
            throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("User-Agent", USER_AGENT);

        if (urlParameters != null) {
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        }
        return httpPost;
    }

    public Vector<String> postToHttpAndGetAsVector(String url, List<NameValuePair> urlParameters) throws IOException, PageNotFoundException, InvalidSessionIdException {
        Vector<String> result = new Vector<String>();

        HttpPost httpPost = null;
        try {
            httpPost = preparePostRequest(url, urlParameters);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            throw new PageNotFoundException("unsupported encoding exception");
        }

        try {
            CloseableHttpResponse response = getResponse(url, httpPost);

            checkFor404(url, response);
            result = extractEntityToVector(response);
        } catch (ClientProtocolException e) {
            result.add(e.getMessage());
            throw e;
        } catch (IOException e) {
            result.add(e.getMessage());
            throw e;
        }
        return result;
    }

}
