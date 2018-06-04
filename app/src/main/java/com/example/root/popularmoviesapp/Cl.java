package com.example.root.popularmoviesapp;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;
public class Cl {
    public interface MovieDatabaseService {
        @GET("movie/popular?")
        Call<ResponseMovie> popularMovies();
        @GET("movie/top_rated?")
        Call<ResponseMovie> topRatedMovies();
    }
    private static Cl instance = null;
    private static final String mBaseURL = "https://api.themoviedb.org/3/";
    public final MovieDatabaseService movieDatabaseService; //STRING TYPE VARIABLE
    private Cl() //PRIVATE CONSTRUCTOR
    {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {

            @Override

            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", BuildConfig.APIKEY)
                        .build();
                Request.Builder requestBuilder = original.newBuilder() //ADD REQUEST HEADER
                        .url(url);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }

        }).build();
        movieDatabaseService = new Retrofit.Builder()
                .baseUrl(mBaseURL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(MovieDatabaseService.class);
    }
    public static Cl getInstance() //CREATE INSTANCE OF SINGLETON CLASS
    {
        if (instance == null)
            instance = new Cl();
        return instance;
    }
    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        }
        return false;
    }
}
