package master.diegosalviano.com.desafio_conductor_android.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import master.diegosalviano.com.desafio_conductor_android.Model.Dados;
import master.diegosalviano.com.desafio_conductor_android.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class IntroActivity extends AppCompatActivity {

    private  boolean error;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        settings = getSharedPreferences("DADOS", MainActivity.MODE_PRIVATE);
        editor = settings.edit();

        carregar();
    }

    private void carregar() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                dadosUsuario();
            }
        }, 2000);
    }

    void dadosUsuario(){
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Dados.API_USUARIO)
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().string());

                        editor.putString("nome", object.getString("name"));
                        editor.putString("cartao", object.getString("cardNumber"));
                        editor.commit();

                        ResponseBody responseBody = response.body();
                        responseBody.close();
                    }else{
                        error = true;
                    }

                    if(!response.isSuccessful()){
                        error = true;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("Timeout", "timeout");
                } catch (JSONException e) {
                    System.out.println("End of content");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if(error){
                    Toast.makeText(IntroActivity.this, "Erro ao obter dados", Toast.LENGTH_LONG).show();
                    carregar();
                }else{
                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        task.execute();
    }
}
