package com.example.webdatabaseacess.view;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.webdatabaseacess.R;
import com.example.webdatabaseacess.adapter.ClinicAdapter;
import com.example.webdatabaseacess.dao.ClinicDataBase;
import com.example.webdatabaseacess.model.Clinic;
import com.example.webdatabaseacess.util.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";

    ProgressBar progressBarLoadLog;
    TextView tvTextExibido;
    ListView lv_listClinics;

    //ACESSANDO METODO DO MENU QUANDO AS OPCOES SAO CRIADAS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTextExibido = findViewById(R.id.tv_texto_inicial);
        progressBarLoadLog = findViewById(R.id.pb_loading);
        lv_listClinics = findViewById(R.id.lv_list_clinics);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_web_service_github:
                callWebServiceGitHub();
                break;
            case R.id.menu_web_service_evo:
                callWebServiceEvo();
                break;
            case R.id.menu_db_local_evo:
                callDBLocalEvo();
                break;
            case R.id.menu_clear_db_local_evo:
                callClearDBLocal();
                break;
            case R.id.menu_clear:
                clearText();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void callWebServiceGitHub(){
        Log.d(TAG, "method callWebService");
        URL url = NetworkUtil.buildUrl("stf");
        MinhaAsyncTaskGitHub task = new MinhaAsyncTaskGitHub();

        //if(verificarConexao())
        task.execute(url);
        //else tvTextExibido.setText("Falha na conexão com a internet!");
    }

    public void callWebServiceEvo(){
        URL url = NetworkUtil.buildUrlCredenciadasEvo();
        MinhaAsyncTaskEvo task = new MinhaAsyncTaskEvo();

        task.execute(url);
    }

    public void callDBLocalEvo(){
        Log.d(TAG, "method callDBLocalEvo");
        GetAllFavoritesAsyncTask task = new GetAllFavoritesAsyncTask(this);
        task.execute();
    }

    public void callClearDBLocal(){
        ClearDBLocal task = new ClearDBLocal(this);
        task.execute();
    }

    public void clearText(){
        Log.d(TAG, "method clearText");
        tvTextExibido.setText("");
    }

    class MinhaAsyncTaskGitHub extends AsyncTask<URL, Void, String> {

        String TAG = "MinhaAsyncTask";
        String json = null;

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            Log.d(TAG, "URL utilizada: " + url.toString());
            try {
                json = NetworkUtil.getResponseFromHttpUrl(url);
                Log.d(TAG, "AsyncTask retornou: " + json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        }

        //Metodo antes da execucao do corpo da task
        @Override
        protected void onPreExecute(){
            mostrarLoading();
        }

        //Metodo apos return da task
        @Override
        protected void onPostExecute(String s){
            esconderLoading();
            tvTextExibido.setText(s);
            lv_listClinics.setVisibility(View.GONE);
        }

    }

    class MinhaAsyncTaskEvo extends AsyncTask<URL, Void, List<Clinic>>{

        String TAG = "MinhaAsyncTaskEvo";
        String json = null;

        @Override
        protected List<Clinic> doInBackground(URL... urls) {
            URL url = urls[0];
            Log.d(TAG, "URL utilizada: " + url.toString());
            try {
                json = NetworkUtil.getResponseFromHttpUrl(url);
                Log.d(TAG, "AsyncTask retornou: " + json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TypeToken<List<Clinic>> token = new TypeToken<List<Clinic>>(){};
            List<Clinic> clinics = new Gson().fromJson(json.toString(), token.getType());

            return clinics;
        }

        //Metodo antes da execucao do corpo da task
        @Override
        protected void onPreExecute(){
            mostrarLoading();
        }

        //Metodo apos return da task
        @Override
        protected void onPostExecute(List<Clinic> listByJson){
            esconderLoading();
            tvTextExibido.setVisibility(View.GONE);

            if(listByJson == null) {
                tvTextExibido.setText("Houve um erro");
            }else{

                //Set start status favorite clinic default
                for (Clinic c : listByJson){
                    c.setFavorite(false);
                }

                setClinicsList(listByJson);
            }
        }

    }

    class GetAllFavoritesAsyncTask extends AsyncTask<Void, Void, List<Clinic>>{
        Context context;

        GetAllFavoritesAsyncTask(Context context){
            this.context = context;
        }

        @Override
        protected List<Clinic> doInBackground(Void... voids) {
            return ClinicDataBase.getInstance(context).getDao().getAllClinics();
        }

        @Override
        protected void onPostExecute(List<Clinic> clinics) {
            if (clinics.size() == 0) {
                clearListClinics();
                tvTextExibido.setText(R.string.without_favorite);
            } else {
                tvTextExibido.setText(null);
                listClinicsByDB(clinics);
            }

            super.onPostExecute(clinics);
        }
    }

    class ClearDBLocal extends AsyncTask<Void, Void, List<Clinic>>{
        Context context;

        ClearDBLocal(Context context){
            this.context = context;
        }

        @Override
        protected List<Clinic> doInBackground(Void... voids) {
            ClinicDataBase.getInstance(context).getDao().clearDB();

            return ClinicDataBase.getInstance(context).getDao().getAllClinics();

        }

        @Override
        protected void onPostExecute(List<Clinic> clinics) {

            tvTextExibido.setText(null);
            setClinicsList(clinics);

            super.onPostExecute(clinics);
        }
    }

    public ListView listClinicsByDB(final List<Clinic> a){

        ClinicAdapter adapter = new ClinicAdapter(getApplicationContext(), a);
        lv_listClinics.setAdapter(adapter);

        lv_listClinics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ItemDetailListClinic.class);
                intent.putExtra("clinic", a.get(position));
                startActivity(intent);
            }
        });

        return lv_listClinics;
    }

    public void clearListClinics(){
        lv_listClinics.setAdapter(null);
    }


    public void mostrarLoading(){
        tvTextExibido.setVisibility(View.GONE);
        lv_listClinics.setVisibility(View.GONE);
        progressBarLoadLog.setVisibility(View.VISIBLE);
    }

    public void esconderLoading(){
        lv_listClinics.setVisibility(View.VISIBLE);
        tvTextExibido.setVisibility(View.VISIBLE);
        progressBarLoadLog.setVisibility(View.GONE);
    }

    public ListView setClinicsList(final List<Clinic> listClinics){

        ClinicAdapter adapter = new ClinicAdapter(this, listClinics);
        lv_listClinics.setAdapter(adapter);

        lv_listClinics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(MainActivity.this, ItemDetailListClinic.class);
                //Passa o objeto para outra activity
                mIntent.putExtra("clinic", listClinics.get(position));
                // chama a nova tela
                startActivity(mIntent);
            }
        });

        return lv_listClinics;
    }


    /* Função para verificar existência de conexão com a internet */
    /*public boolean verificarConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conectivtyManager.getActiveNetworkInfo() != null && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }*/
}
