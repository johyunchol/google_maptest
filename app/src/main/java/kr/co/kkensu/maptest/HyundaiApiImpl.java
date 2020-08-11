package kr.co.kkensu.maptest;

import android.os.Build;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HyundaiApiImpl implements HyundaiApi {

    private HyundaiApi api;

    public HyundaiApiImpl() {
        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
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
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory)
//                .addInterceptor(logging)
//                .addInterceptor(new LogInterceptor(BuildConfig.IS_DEBUG ? true : false))
//                .addInterceptor(new ItchaApiInterceptor(apiInfo, enableLog, false))
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        try {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Android ");
                            stringBuilder.append(Build.VERSION.RELEASE);
                            stringBuilder.append(" " + Build.MODEL);
                            Request requestWithUserAgent = originalRequest.newBuilder()
                                    .header("User-Agent", stringBuilder.toString())
                                    .build();
                            return chain.proceed(requestWithUserAgent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return chain.proceed(originalRequest);
                    }
                }).build();

        ObjectMapper mapper = JacksonFactory.createMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()));
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .baseUrl("https://prd.kr-ccapi.hyundai.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient)
                .build();

        api = retrofit.create(HyundaiApi.class);
    }

    @NotNull
    @Override
    public Call<GetSearchResponse> search(@NotNull String auth) {
        return api.search("Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaWQiOiI1YThhMjczMWY1ZmYzMjI4MWNjYTJhNTMiLCJ1aWQiOiI3MDA0MWY3NS01NzY4LTQyZTktOGVlZS0wMTlmZGZlYWYwNTkiLCJzaWQiOiJmMjliMzhhYi02MzllLTQ0MDgtOTYyMy0wMmZjZTFkYjIzMTUiLCJleHAiOjE1OTcxMDc0NzksImlhdCI6MTU5NzAyMTA3OSwiaXNzIjoiYmx1ZWxpbmsifQ.APHWqUwzVlz-__wM3JcxYjfVTbJmELl24VUmfysZhESO7en7KctXf90h9ISNRv8EkxPA8KfftVccr-epZcxjtbwzLjEYpvPjziooReS_3Ad4IvfAwgzLTRIy5uI6iGqA_7cG3MRfwsdjD7uBB4NJV6XieWDQeGGH2AxfeoIXZGe2Oczr0qV1DUReGRQdr9_mpsmi5EsrZ2zOnCjv8uiG5uA5RP_kG6xrcYR3pqD1Yu0Mtr6u8-0OsrtwzetU9h4EBwVy1XKKtRTDt1j-uPH7RL-Zb4LM0txBR5WOsGVkYWO2WKQF6u-E50ViC-6dNfG80XkFzxEzGNSXiH8EpzmLdA");
    }
}
