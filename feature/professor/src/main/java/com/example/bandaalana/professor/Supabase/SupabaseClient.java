package com.example.bandaalana.professor.Supabase;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Cliente de conexão Retrofit configurado para se comunicar com a API REST do Supabase.
public class SupabaseClient {
    private static final String BASE_URL = "https://rnkzqacojksakldibqwn.supabase.co/";

    // Chave pública (anon / publishable key) para acesso às tabelas com segurança RLS
    private static final String SUPABASE_ANON_KEY = "sb_publishable_gt5Hzz2WgCuJ0NolBSpzWw_fqqRnVUY";

    private static Retrofit retrofit = null;

    // Monta o endereço público de um arquivo guardado no Storage do Supabase
    public static String urlPublicaStorage(String bucket, String arquivo) {
        return BASE_URL + "storage/v1/object/public/" + bucket + "/" + arquivo;
    }

    // Retorna a instância Singleton do Retrofit configurada com autenticação e conversor Gson
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Adiciona os headers obrigatórios exigidos pelo Supabase em todas as requisições
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("apikey", SUPABASE_ANON_KEY)
                        .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
