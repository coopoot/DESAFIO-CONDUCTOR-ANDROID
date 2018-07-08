package master.diegosalviano.com.desafio_conductor_android.View;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import master.diegosalviano.com.desafio_conductor_android.Controller.Extrato;
import master.diegosalviano.com.desafio_conductor_android.Model.Dados;
import master.diegosalviano.com.desafio_conductor_android.Model.ExtratoAdapter;
import master.diegosalviano.com.desafio_conductor_android.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private SharedPreferences settings;
    private ProgressBar progressLoad;
    private ListView listView;
    private ExtratoAdapter adapter;
    private ArrayList<Extrato> arrayList;
    private Calendar calendar;
    private ArrayList<String> labels;
    private ArrayList<BarEntry> grupo;
    private ArrayList<BarDataSet> dataSets;

    private RelativeLayout relativeCartao;
    private RelativeLayout relativeGrafico;

    private String saldo;

    private boolean error;

    private TextView tvDisponivel;
    private TextView tvGasto;
    private TextView tvNumero;

    private EditText edMes;
    private EditText edAno;

    private int day;
    private int month;
    private int year;
    private int oldPage = 1;
    private int page = 1;

    private double totalGasto;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_card:
                    Log.i("LOG", "cartao");
                    relativeCartao.setVisibility(View.VISIBLE);
                    relativeGrafico.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_graficos:
                    Log.i("LOG", "grafico");
                    relativeCartao.setVisibility(View.GONE);
                    relativeGrafico.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationButton);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        progressLoad = (ProgressBar) findViewById(R.id.progressLoad);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        listView = (ListView) findViewById(R.id.listview);

        relativeCartao = (RelativeLayout) findViewById(R.id.relativeCartao);
        relativeGrafico = (RelativeLayout) findViewById(R.id.relativeGrafico);

        tvDisponivel = (TextView) findViewById(R.id.tvDisponivel);
        tvGasto = (TextView) findViewById(R.id.tvGasto);
        tvNumero = (TextView) findViewById(R.id.tvNumero);


        edMes = (EditText) findViewById(R.id.edMes);
        edAno = (EditText) findViewById(R.id.edAno);

        arrayList = new ArrayList<>();
        dataSets = new ArrayList<>();
        labels = new ArrayList<>();
        grupo = new ArrayList<>();

        edMes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, MainActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        settings = getSharedPreferences("DADOS", MainActivity.MODE_PRIVATE);

        calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        year = calendar.get(Calendar.YEAR);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Extrato");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        TextView tvNome = (TextView) header.findViewById(R.id.tvNome);
        tvNome.setText(settings.getString("nome", ""));
        String numeroCartao = settings.getString("cartao", "");
        tvNumero.setText("xxxx xxxx xxxx " + numeroCartao.substring(numeroCartao.length() - 4));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    if (oldPage != page) {
                        extrato(String.valueOf(month + 1), String.valueOf(year));
                    } else {
                        Toast.makeText(MainActivity.this, "Não temos mais paginas para exibir", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });

        saldo();
    }

    void saldo() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                exibirProgress(true);
                error = false;
            }

            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Dados.API_SALDO)
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().string());
                        saldo = object.getString("balance");
                        ResponseBody responseBody = response.body();
                        responseBody.close();
                    } else {
                        error = true;
                    }

                    if (!response.isSuccessful()) {
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
                if (!error) {
                    tvDisponivel.setText(Dados.valor(saldo));
                    extrato(String.valueOf(month + 1), String.valueOf(year));
                } else {
                    tvDisponivel.setText("R$ 0");
                    exibirProgress(true);
                    Toast.makeText(MainActivity.this, "Não conseguimos atualizar os dados", Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.execute();
    }

    void extrato(final String mes, final String ano) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                oldPage = page;
                if (arrayList.size() > 0) {
                    arrayList.clear();
                    labels.clear();
                    grupo.clear();
                }
                totalGasto = 0;
                error = false;
            }

            @Override
            protected Void doInBackground(Integer... integers) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(Dados.API_EXTRATO + "month=" + mes + "&year=" + ano + "&page=" + page)
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.code() == 200) {

                        JSONObject jsonObject = (JSONObject) new JSONTokener(response.body().string()).nextValue();

                        JSONArray jsonArray = jsonObject.getJSONArray("purchases");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            arrayList.add(new Extrato(object.getString("date"), object.getString("store"), object.getString("value")));
                            totalGasto += Double.parseDouble(object.getString("value"));

                            grupo.add(new BarEntry(Float.parseFloat(object.getString("value")), i));
                            labels.add(Dados.data(object.getString("date")));
                        }

                        page = Integer.parseInt(jsonObject.getString("lastPage"));

                        ResponseBody responseBody = response.body();
                        responseBody.close();
                    } else {
                        error = true;
                    }

                    if (!response.isSuccessful()) {
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
                exibirProgress(false);
                adapter = new ExtratoAdapter(getBaseContext(), R.layout.lista_extrato, arrayList);
                listView.setAdapter(adapter);
                if (error) {
                    Toast.makeText(MainActivity.this, "Erro ao obter dados", Toast.LENGTH_SHORT).show();
                } else {
                    grafico();
                    tvGasto.setText(Dados.valor(String.valueOf(totalGasto)));
                }
            }
        };

        task.execute();
    }


    private void grafico() {

        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        BarDataSet barDataSet1 = new BarDataSet(grupo, "Gastos");

        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
        if (dataSets.size() > 0) {
            dataSets.clear();
        }
        dataSets.add(barDataSet1);

        BarData data = new BarData(labels, dataSets);
        barChart.animateY(3000);
        barChart.setData(data);
    }

    private void exibirProgress(boolean exibir) {
        progressLoad.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
        edMes.setText(String.valueOf(mes + 1));
        edAno.setText(String.valueOf(ano));
        page = 1;

        day = dia;
        month = mes;
        year = ano;

        Log.i("LOG", String.valueOf(dia));
        Log.i("LOG", String.valueOf(mes));
        Log.i("LOG", String.valueOf(ano));
        exibirProgress(true);
        extrato(String.valueOf(mes + 1), String.valueOf(ano));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sair) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
