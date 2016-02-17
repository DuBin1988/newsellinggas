package com.tencent.common;

import com.tencent.service.IServiceRequest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 14:36
 */
public class HttpsRequest implements IServiceRequest{

    public interface ResultListener {


        public void onConnectionPoolTimeoutError();

    }

    private static Log log = new Log(LoggerFactory.getLogger(HttpsRequest.class));

    //��ʾ�������Ƿ��Ѿ����˳�ʼ������
    private boolean hasInit = false;

    //���ӳ�ʱʱ�䣬Ĭ��10��
    private int socketTimeout = 10000;

    //���䳬ʱʱ�䣬Ĭ��30��
    private int connectTimeout = 30000;

    //������������
    private RequestConfig requestConfig;

    //HTTP������
    private CloseableHttpClient httpClient;

    public HttpsRequest() throws UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
        init();
    }

    private void init() throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(Configure.getCertLocalPath()));//���ر��ص�֤�����https���ܴ���
        try {
            keyStore.load(instream, Configure.getCertPassword().toCharArray());//����֤������
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            instream.close();
        }

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, Configure.getCertPassword().toCharArray())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

        httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();

        //����Ĭ�ϳ�ʱ���Ƴ�ʼ��requestConfig
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();

        hasInit = true;
    }

    /**
     * ͨ��Https��API post xml����
     *
     * @param url    API��ַ
     * @param xmlObj Ҫ�ύ��XML���ݶ���
     * @return API�ذ���ʵ������
     * @throws IOException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */

    public String sendPost(String url, Object xmlObj) throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {

        if (!hasInit) {
            init();
        }

        String result = null;

        HttpPost httpPost = new HttpPost(url);

        //���XStream�Գ���˫�»��ߵ�bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));

        //��Ҫ�ύ��API�����ݶ���ת����XML��ʽ����Post��API
        String postDataXML = xStreamForRequestPostData.toXML(xmlObj);

        Util.log("API��POST��ȥ�������ǣ�");
        Util.log(postDataXML);

        //��ָ��ʹ��UTF-8���룬����API������XML�����Ĳ��ܱ��ɹ�ʶ��
        StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);

        //����������������
        httpPost.setConfig(requestConfig);

        Util.log("executing request" + httpPost.getRequestLine());

        try {
            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, "UTF-8");

        } catch (ConnectionPoolTimeoutException e) {
            log.e("http get throw ConnectionPoolTimeoutException(wait time out)");

        } catch (ConnectTimeoutException e) {
            log.e("http get throw ConnectTimeoutException");

        } catch (SocketTimeoutException e) {
            log.e("http get throw SocketTimeoutException");

        } catch (Exception e) {
            log.e("http get throw Exception");

        } finally {
            httpPost.abort();
        }

        return result;
    }

    /**
     * �������ӳ�ʱʱ��
     *
     * @param socketTimeout ����ʱ����Ĭ��10��
     */
    public void setSocketTimeout(int socketTimeout) {
        socketTimeout = socketTimeout;
        resetRequestConfig();
    }

    /**
     * ���ô��䳬ʱʱ��
     *
     * @param connectTimeout ����ʱ����Ĭ��30��
     */
    public void setConnectTimeout(int connectTimeout) {
        connectTimeout = connectTimeout;
        resetRequestConfig();
    }

    private void resetRequestConfig(){
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
    }

    /**
     * �����̻��Լ������߼������ӵ�����������
     *
     * @param requestConfig ����HttpsRequest������������
     */
    public void setRequestConfig(RequestConfig requestConfig) {
        requestConfig = requestConfig;
    }
}
