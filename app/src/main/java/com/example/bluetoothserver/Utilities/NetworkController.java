package com.example.bluetoothserver.Utilities;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;


import com.example.bluetoothserver.BuildConfig;
import com.example.bluetoothserver.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkController {

    private static NetworkController singleTonInstance = null;
    private Retrofit mRetrofit;
    private ApiMethods mApiMethods;
    private OkHttpClient httpClientBuilder;

    private NetworkController() {
    }

    public static NetworkController getInstance() {
        if (singleTonInstance == null) {
            singleTonInstance = new NetworkController();
        }
        return singleTonInstance;
    }

    public Retrofit getRetrofit(Context context) {


        mRetrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(getHttpClient().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return mRetrofit;
    }

    //Time out Issue to be Resolved
    private OkHttpClient.Builder getHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return  builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public ApiMethods getApiMethods(Context context) {
        if (mApiMethods == null) {
            mApiMethods = getRetrofit(context).create(ApiMethods.class);
        }
        return mApiMethods;
    }

    public void ShowRestErrorToast(Activity activity, String message, String Message) {

        if (message == null && Message != null) {

            Toast.makeText(activity, Message, Toast.LENGTH_SHORT).show();

        } else if (Message == null && message != null) {

            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

        } else {
            // Message Need to be changed
            Toast.makeText(activity, "Unable to connect .Please try again", Toast.LENGTH_SHORT).show();
        }

    }

    KeyStore readKeyStore(Context context) {
        KeyStore ks = null;
        InputStream in = null;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
//ks = KeyStore.getInstance(KeyStore.getDefaultType());
// get user password and file input stream
            char[] password = "sami!7264@F95".toCharArray();
//            char[] password = "".toCharArray();
            in = context.getResources().openRawResource(R.raw.start_samipharma_com_pk);
            ks.load(in, password);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ks;
    }

    private OkHttpClient getUnsafeOkHttpClient(Context context) {
        try {
// Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            /*CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = context.getResources().openRawResource(R.raw.start_samipharma_com_pk);
            Certificate ca;
            try {
                ca = cf.generateCertificate(cert);
            } finally { cert.close(); }

            // creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
//            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);


            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);*/

            /*
            // creating a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            trustManagerFactory.init(keyStore);*/

            KeyStore keyStore = readKeyStore(context);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            /*TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];*/

// Install the all-trusting trust manager
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, "sami!7264@F95".toCharArray());
            kmf.init(keyStore, "".toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            final SSLContext sslContext = SSLContext.getInstance("SSL");
//added a Key manager authenticate the Client.
//            sslContext.init(keyManagers , null, new java.security.SecureRandom());
            sslContext.init(null , tmf.getTrustManagers(), null);
// Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            getHttpClient().sslSocketFactory(sslSocketFactory, (X509TrustManager) tmf.getTrustManagers()[0])
                    .hostnameVerifier((hostname, session) -> true);
            return getHttpClient().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
