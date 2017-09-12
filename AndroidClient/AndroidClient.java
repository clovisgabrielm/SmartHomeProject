package com.example.androidclient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class AndroidClient extends Activity {

    private EditText edtIp, edtPort;
    private TextView edtMsgConnected;
    private Button btnConnect;
    private Switch sLuzCozinha, sLuzSalaJantar, sLuzQuarto, sLuzBanheiro;

    private Socket clientSocket;
    private ObjectInputStream ois = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_client);

        edtIp = (EditText) findViewById(R.id.ip);
        edtPort = (EditText) findViewById(R.id.port);
        btnConnect = (Button) findViewById(R.id.connect);
        edtMsgConnected = (TextView) findViewById(R.id.msgConnected);

        edtMsgConnected.setText("Please type IP and Port to connect.");

        // Switches

        sLuzSalaJantar = (Switch) findViewById(R.id.luz_sala_jantar);
        sLuzBanheiro = (Switch) findViewById(R.id.luz_banheiro);
        sLuzCozinha = (Switch) findViewById(R.id.luz_cozinha);
        sLuzQuarto = (Switch) findViewById(R.id.luz_quarto);

        sLuzQuarto.setVisibility(View.INVISIBLE);
        sLuzCozinha.setVisibility(View.INVISIBLE);
        sLuzBanheiro.setVisibility(View.INVISIBLE);
        sLuzSalaJantar.setVisibility(View.INVISIBLE);

        sLuzSalaJantar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String str;

                if(isChecked) {
                    str = "Ligar luz da sala";
                } else {
                    str = "Desligar luz da sala";
                }


                ObjectOutputStream oos = null;
                try {
                    oos = new ObjectOutputStream(clientSocket.getOutputStream());
                    oos.writeObject(str);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    //ois = new ObjectInputStream(clientSocket.getInputStream());
                    //String message = (String) ois.readObject();
                    //System.out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtMsgConnected.setText("Trying to connect to server...");

                new Thread(new ClientThread()).start();
            }
        };

        btnConnect.setOnClickListener(buttonConnectOnClickListener);
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {

                clientSocket = new Socket(edtIp.getText().toString(),
                        Integer.parseInt(edtPort.getText().toString()));



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        edtMsgConnected.setText("Client is now connected.");

                        sLuzSalaJantar.setVisibility(View.VISIBLE);
                        sLuzCozinha.setVisibility(View.VISIBLE);
                        sLuzQuarto.setVisibility(View.VISIBLE);
                        sLuzBanheiro.setVisibility(View.VISIBLE);
                    }
                });



            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_android_client, menu);
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
