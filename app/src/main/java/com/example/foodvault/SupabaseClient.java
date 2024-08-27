package com.example.foodvault;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * SupabaseClient class to create a Retrofit client
 * Essentially, this class is a singleton class that creates a Retrofit client
 * If one is already created, it will return the existing one
 */
public class SupabaseClient {
    private static Retrofit retrofit = null;
    private static String baseUrl = "https://rucdnepecnqmfmyyymgt.supabase.co"; //"https://hddpqiabofrxxyptexff.supabase.co"; //add your url
    private static String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ1Y2RuZXBlY25xbWZteXl5bWd0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjM0MDYwMzksImV4cCI6MjAzODk4MjAzOX0.Bau0CHxYNgngjKF6QBIhRPk805kEirJvrEM7lwjvK1U";
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("apikey", apiKey)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .build();
                return chain.proceed(request);
            }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
