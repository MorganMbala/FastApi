package com.example.frontendjava.api;

import com.example.frontendjava.model.Acteur;
import com.example.frontendjava.model.ActeurUpdate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ActeurApiService {
    @GET("/acteurs")
    Call<List<Acteur>> getActeurs();

    @GET("/acteur/{id}")
    Call<Acteur> getActeur(@Path("id") int id);

    @POST("/acteur/")
    Call<Acteur> createActeur(@Body Acteur acteur);

    @PUT("/acteur/{id}")
    Call<Acteur> updateActeur(@Path("id") int id, @Body ActeurUpdate acteur);

    @DELETE("/acteur/{id}")
    Call<Acteur> deleteActeur(@Path("id") int id);
}
