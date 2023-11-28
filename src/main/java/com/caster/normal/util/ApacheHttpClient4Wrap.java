package com.caster.normal.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ApacheHttpClient4Wrap {

    /**
     * log
     */
    protected static Logger logger = LoggerFactory.getLogger("restAPI");

    private static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    static {
        synchronized (logger) {
            SSLContext sslcontext = SSLContexts.createSystemDefault();
            // Create a registry of custom connection socket factories for supported
            // protocol schemes.
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();

            // Use custom DNS resolver to override the system DNS resolution.
            DnsResolver dnsResolver = new SystemDefaultDnsResolver();
            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, dnsResolver);

        }
    }

    /**
     * time out
     */
    private static int timeOutSec4Post = 15;

    /**
     * 自定义配置器
     */
    private RequestConfig.Builder custom;

    /**
     * 自定义配置
     */
    private RequestConfig requestConfig;

    /**
     * 建立连线器
     */
    private HttpClientBuilder builder;

    /**
     * 连线
     */
    private CloseableHttpClient client;

    /**
     * HTTP POST method.
     */
    private HttpPost post;

    /**
     * HTTP Get method.
     */
    private HttpGet httpGet;

    /**
     * 预设编码
     */
    private String defaultCharset = "utf-8";

    private String url;



    public ApacheHttpClient4Wrap(){
        builder = HttpClientBuilder.create();
        client = builder.build();
    }

    public String executeHttpGet(String url, Map<String, String> headers) throws IOException {
        String result = "";
        Objects.requireNonNull(url, "url不得為空");
        this.custom = RequestConfig.custom();
		/*if(isUsedProxy && thisTimeUsedProxy){//若有設定檔設定使用proxy並且這次連線也有指定要使用proxy，才使用proxy
			boolean isExclude = false;
			for(String excludeUrl : excludeDomain.keySet()){
				if(url.startsWith(excludeUrl)){
					isExclude = true;
				}
			}
			if(isExclude){//若在排除名單內就不使用proxy
				logger.debug(url+" 列於排除名單內，因此排除使用proxy!!!");
			}else{
				HttpHost proxy = new HttpHost(proxyIp, proxyPort, "http");
				this.custom.setProxy(proxy);
				logger.debug(url+" 本次連線會使用proxy!!!");
			}

		}*/
        this.custom.setSocketTimeout(timeOutSec4Post * 1000); // 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
        this.custom.setConnectTimeout(timeOutSec4Post * 1000); // 设置连接超时时间，单位毫秒。
        this.custom.setConnectionRequestTimeout(timeOutSec4Post * 1000);
        this.requestConfig = custom.build(); // 取得設定
        httpGet = new HttpGet(url);
        httpGet.setConfig(this.requestConfig);

        if (Objects.nonNull(headers)){
            headers.forEach((key, value)->{
                httpGet.setHeader(key, value);
            });
        }

        HttpResponse response = client.execute(httpGet);
        if (Objects.nonNull(response)) {
            HttpEntity resEntity = response.getEntity();
            if (Objects.nonNull(resEntity)) {
                result = EntityUtils.toString(resEntity, defaultCharset);
            }
        }

        return result;
    }



    public ApacheHttpClient4Wrap(String url) throws Exception {
        this(url, "", false);
    }

    /**
     * 使用HttpClient建立連線，並且指定是否要使用代理
     *
     * @param url
     * @param thisTimeUsedProxy 這次建立的client物件連線是否要使用proxy，設定檔也需要開啟才會使用proxy
     * @throws Exception
     */
    public ApacheHttpClient4Wrap(String url, boolean thisTimeUsedProxy) throws Exception {
        this(url, "", thisTimeUsedProxy);
    }

    public ApacheHttpClient4Wrap(String url, String defaultCharset) throws Exception {
        this(url, defaultCharset, true);
    }

    /**
     * @param url
     * @param defaultCharset    指定編碼，预设是编码(utf-8)
     * @param thisTimeUsedProxy 這次建立的client物件連線是否要使用proxy
     * @throws Exception
     */
    public ApacheHttpClient4Wrap(String url, String defaultCharset, boolean thisTimeUsedProxy) throws Exception {
        //暫時改成每次建立ApacheHttpClient4Wrap物件時都抓取需要proxy資料與需要排除資訊
        boolean isUsedProxy = false;//設定檔指定是否開啟proxy


        //////////////////////////////////////////////////////////////////////////////
        if (!url.startsWith("http")) {
            throw new HttpException(" url start with http or https !!!");
        }

        if (StringUtils.isNotBlank(defaultCharset)) {
            this.defaultCharset = defaultCharset;
        }

        this.custom = RequestConfig.custom();
		/*if(isUsedProxy && thisTimeUsedProxy){//若有設定檔設定使用proxy並且這次連線也有指定要使用proxy，才使用proxy
			boolean isExclude = false;
			for(String excludeUrl : excludeDomain.keySet()){
				if(url.startsWith(excludeUrl)){
					isExclude = true;
				}
			}
			if(isExclude){//若在排除名單內就不使用proxy
				logger.debug(url+" 列於排除名單內，因此排除使用proxy!!!");
			}else{
				HttpHost proxy = new HttpHost(proxyIp, proxyPort, "http");
				this.custom.setProxy(proxy);
				logger.debug(url+" 本次連線會使用proxy!!!");
			}

		}*/
        this.custom.setSocketTimeout(timeOutSec4Post * 1000); // 请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
        this.custom.setConnectTimeout(timeOutSec4Post * 1000); // 设置连接超时时间，单位毫秒。
        this.custom.setConnectionRequestTimeout(timeOutSec4Post * 1000);

        this.requestConfig = custom.build(); // 取得設定

        builder = HttpClientBuilder.create();
        // builder.disableRedirectHandling();

        if (url.startsWith("https")) {
            logger.debug("[LOG] is https ");

            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true)
                    .build();

            builder.setSSLContext(sslContext);
            builder.setSSLHostnameVerifier(new NoopHostnameVerifier());

        } else {
            logger.debug("[LOG] is http ");
        }

        //改成在post或get時才設定，不能設定在builder.setDefaultRequestConfig中，會變CloseableHttpClient都是吃到proxy的設定
        //builder.setDefaultRequestConfig(requestConfig);
        builder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        builder.setConnectionManagerShared(true);

        // TODO check how to use it
        // builder.setConnectionManager(connManager);

        client = builder.build();

        post = new HttpPost(url);
        post.setConfig(requestConfig);
    }

    /**
     * @param url    ("http://192.168.0.21/myPayCenter/admin/login.jsp")
     * @param header http headers
     * @throws Exception
     */
    public ApacheHttpClient4Wrap(String url, Map<String, String> header) throws Exception {
        this(url, header, "");

    }

    /**
     * @param url    ("http://192.168.0.21/myPayCenter/admin/login.jsp")
     * @param header http headers
     * @throws Exception
     */
    public ApacheHttpClient4Wrap(String url, Map<String, String> header, String defaultCharset)
            throws Exception {
        this(url, defaultCharset, true);
        setHeader(header);
        /*if (Objects.nonNull(header)) {
            Iterator<Map.Entry<String, String>> it = header.entrySet()
                                                           .iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                String key = pair.getKey();
                String value = pair.getValue();
                post.setHeader(key, value);
            }
        }*/

    }

    public void setHeader(Map<String, String> header){
        if (Objects.nonNull(header)) {
            Iterator<Map.Entry<String, String>> it = header.entrySet()
                                                           .iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                String key = pair.getKey();
                String value = pair.getValue();
                post.setHeader(key, value);
            }
        }
    }

    /**
     * execute than return apache HttpResponse
     *
     * @return
     * @throws Exception
     */
    public HttpResponse execute2HttpResponse() throws Exception {
        HttpResponse response = client.execute(post);
        return response;
    }

    /**
     * execute than return http body string
     *
     * @return
     * @throws Exception
     */
    public String execute2ResultString() throws Exception {
        return execute2ResultString(defaultCharset);
    }

    /**
     * execute than return http body string
     *
     * @param charset
     * @return
     * @throws Exception
     */
    private String execute2ResultString(String charset) throws Exception {
        String result = null;
        HttpResponse response = client.execute(post);
        if (Objects.nonNull(response)) {
            HttpEntity resEntity = response.getEntity();
            if (Objects.nonNull(resEntity)) {
                result = EntityUtils.toString(resEntity, charset);
            }
        }
        return result;
    }

    /**
     * add json to body
     *
     * @param json
     * @return
     * @throws Exception
     */
    public ApacheHttpClient4Wrap addJsonObject2Body(JSONObject json) throws Exception {
        return addJsonObject2Body(json, defaultCharset);
    }

    /**
     * add json to body
     *
     * @param json
     * @param charset
     * @return
     * @throws Exception
     */
    public ApacheHttpClient4Wrap addJsonObject2Body(JSONObject json, String charset) throws Exception {
        post.setHeader("Content-Type", "application/json;charset=" + charset);
        StringEntity entity = new StringEntity(json.toString(), charset);
        post.setEntity(entity);
        return this;
    }

    /**
     * add string to body
     *
     * @param httpBody
     * @return
     * @throws Exception
     */
    public ApacheHttpClient4Wrap addString2Body(String httpBody) throws Exception {
        return this.addString2Body(httpBody, defaultCharset);
    }

    /**
     * add string to body
     *
     * @param httpBody
     * @return
     * @throws Exception
     */
    public ApacheHttpClient4Wrap addString2Body(String httpBody, ContentType contentType) throws Exception {
        StringEntity entity = new StringEntity(httpBody, contentType);
        post.setEntity(entity);
        return this;
    }

    /**
     * @param httpBody
     * @param charset  (GBK,UTF-8)
     * @return
     * @throws Exception
     */
    private ApacheHttpClient4Wrap addString2Body(String httpBody, String charset) throws Exception {
        //post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
        StringEntity entity = new StringEntity(httpBody, charset);
        post.setEntity(entity);
        return this;
    }

    /**
     * add file to body
     *
     * @param file
     * @return
     * @throws Exception
     */
    public ApacheHttpClient4Wrap addFile2Body(File file) throws Exception {
        HttpEntity entity = new FileEntity(file, ContentType.create("application/java-archive"));
        post.setEntity(entity);
        return this;
    }

    /**
     * add http post form to body
     * (此方法會將所有傳入參數進行url encode，因此不要自行encode)
     *
     * @param nameValuePair
     * @return
     * @throws Exception
     */
    public ApacheHttpClient4Wrap addNameValuePair(Map<String, String> nameValuePair) throws Exception {
        return addNameValuePair(nameValuePair, defaultCharset);
    }



    /**
     * add http post form to body
     *
     * @param nameValuePair
     * @param charset
     * @return
     * @throws Exception
     */
    private ApacheHttpClient4Wrap   addNameValuePair(Map<String, String> nameValuePair, String charset)
            throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (Objects.nonNull(nameValuePair)) {
            Iterator<Map.Entry<String, String>> it = nameValuePair.entrySet()
                                                                  .iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                String key = pair.getKey();
                String value = pair.getValue();
                nvps.add(new BasicNameValuePair(key, value));
            }
        }
        post.setEntity(new UrlEncodedFormEntity(nvps, charset));
        return this;
    }

    public static void main(String[] args) throws Exception {
        String result;
        result = new ApacheHttpClient4Wrap("http://pay.cdq88.vip:3280/initOrder?merNo=dnaq6k@163.com").execute2ResultString();

        //result = new ApacheHttpClient4Wrap("https://pay.cnbitepay.com/pay").execute2ResultString();


    }
}
