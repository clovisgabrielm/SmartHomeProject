package com.example.androidserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import android.os.Handler;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

public class AndroidServer extends Activity {

    private TextView info, message;
    private ServerSocket serverSocket;
    private Handler updateConversationHandler;
    private Thread serverSocketThread = null;
    private static final int PORT = 5700;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_server);
        info = (TextView) findViewById(R.id.info);

        updateConversationHandler = new Handler();

        serverSocketThread = new Thread(new ServerSocketThread());
        serverSocketThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class ServerSocketThread implements Runnable {

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(PORT);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info.setText("Server started...");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();

                    CommunicationThread communicationThread = new CommunicationThread(socket);

                    new Thread(communicationThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;

            try {
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {

            while(!Thread.currentThread().isInterrupted()) {
                try {
                    String read = input.readLine();

                    updateConversationHandler.post(new UpdateUiThread(read));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class UpdateUiThread implements Runnable {

        private String msg;

        public UpdateUiThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            message.setText(message.getText().toString() + "Cliente deseja: " + msg + "\n");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_android_server, menu);
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