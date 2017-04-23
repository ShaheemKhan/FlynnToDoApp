package com.example.mushfiqkhan.flynntodoapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.R.id.content;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    int clickCounter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.newPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItems.add("Clicked : "+clickCounter++);
                adapter.notifyDataSetChanged();
            }
        });
        FloatingActionButton completedfab = (FloatingActionButton) findViewById(R.id.completed);
        completedfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getList(true);
                TextView text = (TextView) findViewById(R.id.textTitle);
                text.setText("Completed Tasks");
            }
        });

        FloatingActionButton incompletedfab = (FloatingActionButton) findViewById(R.id.incompleted);
        incompletedfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getList(false);
                TextView text = (TextView) findViewById(R.id.textTitle);
                text.setText("Incompleted Tasks");
            }
        });

        adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, listItems);
        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        getList(true);
    }

    private void getList(final Boolean completed) {
        StrictMode.ThreadPolicy tp =  StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        try  {
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL("http://jsonplaceholder.typicode.com/todos");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int responseCode = urlConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    String server_response = readStream(urlConnection.getInputStream());
                    JSONArray jsonArray = new JSONArray(server_response);
                    listItems.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject explrObject = jsonArray.getJSONObject(i);
                        if (completed) {
                            if (explrObject.getBoolean("completed"))
                                listItems.add(explrObject.getString("title"));
                        } else {
                            if (!(explrObject.getBoolean("completed")))
                                listItems.add(explrObject.getString("title"));
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
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
}
