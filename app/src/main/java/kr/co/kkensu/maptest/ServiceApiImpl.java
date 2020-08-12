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

public class ServiceApiImpl implements ServiceApi {

    private ServiceApi api;

    public ServiceApiImpl() {
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
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
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
                .baseUrl("https://api-trd.itcha.co.kr:8280")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient)
                .build();

        api = retrofit.create(ServiceApi.class);
    }

    @NotNull
    @Override
    public Call<GetSearchResponse> getVehicleStatus(@NotNull String userId, @NotNull String vehicleId) {
        return api.getVehicleStatus(userId, vehicleId);
    }

    @NotNull
    @Override
    public Call<GetSearchResponse> getVehicleStatus2(@NotNull String userId, @NotNull String vehicleId) {
        return api.getVehicleStatus2(userId, vehicleId);
    }

    @NotNull
    @Override
    public Call<GetSearchResponse> getParkLocation(@NotNull String userId, @NotNull String vehicleId) {
        return api.getParkLocation(userId, vehicleId);
    }
}
