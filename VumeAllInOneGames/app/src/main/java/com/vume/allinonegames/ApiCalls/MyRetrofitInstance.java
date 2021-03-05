package com.vume.allinonegames.ApiCalls;

import com.vume.allinonegames.Util.JoshApplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRetrofitInstance {

    private static Retrofit retrofit;
    private static Retrofit serverretrofitinstance;
    private static Retrofit fantasyleagueretrofitinstance;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(JoshApplication.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getServerBaseUrlRetrofitInstance() {
        if (serverretrofitinstance == null) {
            serverretrofitinstance = new Retrofit.Builder()
                    .baseUrl(JoshApplication.GAME_SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return serverretrofitinstance;
    }

    public static Retrofit getFantasyLeagueBaseUrlRetrofitInstance() {
        if (fantasyleagueretrofitinstance == null) {
            fantasyleagueretrofitinstance = new Retrofit.Builder()
                    .baseUrl(JoshApplication.FANTASYLEAGUE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return fantasyleagueretrofitinstance;
    }
}
