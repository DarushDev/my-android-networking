package com.example.myandroidnetworking;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitAPI {
    //@GET("/repositories") for other Github repositories
    @GET("/users/darushdev/repos")
    Call<ArrayList<Repository>> retrieveRepositories();
}
