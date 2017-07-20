package com.example.myandroidnetworking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.R.string.ok;

public class MainActivity extends AppCompatActivity implements DownloadCompleteListener {

    ListFragment mListFragment;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            if (isWifiConnected()) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Please wait...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

                startDownload();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("No Internet Connection")
                        .setMessage("Looks like your internet connection is off. Please turn it on and try again!")
                        .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); // 1
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); // 2
        return networkInfo != null && networkInfo.isConnected(); // 3
    }

    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) && networkInfo.isConnected();
    }

    private void showListFragment(ArrayList<Repository> repositories) {
        mListFragment = ListFragment.newInstance(repositories);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mListFragment).commit();
    }

    private void startDownload() {
        //use https://api.github.com/repositories for Github repositories URL

        // Download without using external libraries
        //new DownloadRepoTask(this).execute("https://api.github.com/users/darushdev/repos");

        // Download using OkHttp library
        //requestUsingOkHttp("https://api.github.com/users/darushdev/repos");

        // Download using Volley library
        requestUsingVolley("https://api.github.com/users/darushdev/repos");

    }

    @Override
    public void downloadComplete(ArrayList<Repository> repositories) {
        showListFragment(repositories);
        if (mProgressDialog != null) {
            mProgressDialog.hide();
        }
    }

    private void requestUsingOkHttp(String url) {
        OkHttpClient client = new OkHttpClient(); //STEP 1
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build(); //STEP 2

        client.newCall(request).enqueue(new okhttp3.Callback() { //STEP 3

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string(); //STEP 4

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            downloadComplete(Util.retrieveRepositoriesFromResponse(result)); //STEP 5
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void requestUsingVolley(String url) {

        RequestQueue queue = Volley.newRequestQueue(this); //STEP 1

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() { //STEP 2

                    @Override
                    public void onResponse(String response) {
                        try {
                            downloadComplete(Util.retrieveRepositoriesFromResponse(response)); // STEP 3
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest); // STEP 4

    }

}
