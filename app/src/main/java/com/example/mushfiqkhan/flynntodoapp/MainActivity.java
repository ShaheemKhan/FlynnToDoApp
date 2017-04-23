package com.example.mushfiqkhan.flynntodoapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton newTodo = (FloatingActionButton) findViewById(R.id.newPost);
        newTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Title");
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                AlertDialog.Builder builder1 = builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String textToDO = input.getText().toString();
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("userId", 1);
                            obj.put("id", listItems.size());
                            obj.put("title", textToDO);
                            TextView text = (TextView) findViewById(R.id.textTitle);
                            if (text.getText().equals("Completed Tasks")) {
                                obj.put("completed", true);
                            } else {
                                obj.put("completed", false);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        HttpURLConnection urlConnection;
                        String data = obj.toString();
                        String result = null;
                        try {
                            //Connect
                            urlConnection = (HttpURLConnection) ((new URL("https://jsonplaceholder.typicode.com/todos").openConnection()));
                            urlConnection.setDoOutput(true);
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setRequestProperty("Accept", "application/json");
                            urlConnection.setRequestMethod("POST");
                            urlConnection.connect();

                            //Write
                            OutputStream outputStream = urlConnection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            writer.write(data);
                            writer.close();
                            outputStream.close();

                            //Read
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                            String line = null;
                            StringBuilder sb = new StringBuilder();

                            while ((line = bufferedReader.readLine()) != null) {
                                sb.append(line);
                            }

                            bufferedReader.close();
                            result = sb.toString();

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("hi", result);
                        try {
                            JSONObject res = new JSONObject(result);
                            listItems.add(0, res.getString("title"));
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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

        FloatingActionButton incompletefab = (FloatingActionButton) findViewById(R.id.incompleted);
        incompletefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getList(false);
                TextView text = (TextView) findViewById(R.id.textTitle);
                text.setText("Incomplete Tasks");
            }
        });

        adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, listItems);
        final ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listItems.remove(position);
                adapter.notifyDataSetChanged();
            }
        });


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
