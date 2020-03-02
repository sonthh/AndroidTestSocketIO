package com.boothgenie.mobile;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private String url = "https://staging-api.boothgenie.com";
    private String[] listeners = new String[]{
            "ASSIGNED_TASK", "COMPLETED_TASK", "DUE_TASK", "ADDED_ROOM", "ADDED_MESSAGE",
            "ADDED_EXPENSE", "ASSIGNED_EVENT", "ADDED_EVENT", "REACH_DUE_TASK",
            "ADDED_TRAVEL", "ASSIGNED_TRAVEL"
    };
    private Socket mSocket;

    {
        try {
            IO.Options mOptions = new IO.Options();
            mOptions.query = "token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZGE3ZTMxNWQwZmQ2MzA1NDQwNTgyOWIiLCJvcmdhbml6YXRpb25JRCI6IjVkYTdlNGFiZDBmZDYzMDU0NDA1ODI5ZCIsImNoYW5nZVBhc3N3b3JkQXQiOjE1ODE5MTA5MzM1MTAsInN0YXR1cyI6IkFDVElWRSIsImlhdCI6MTU4Mjk1MTgzMSwiZXhwIjoxNTk4NTAzODMxfQ.cP7gR4Mj9-7uY9B7ZN_GgppTWqPR-YhiXPu9Pz-Yh8E";
            mSocket = IO.socket(url, mOptions);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final StringBuilder alert = new StringBuilder();
        for (String listener : listeners) {
            mSocket.on(listener, onReceiveData);
            alert.append("ON ").append(listener).append("\n");
        }
        Toast.makeText(MainActivity.this, alert.toString(), Toast.LENGTH_SHORT).show();

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Connected to the " + url, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Disconnect to the " + url, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        mSocket.connect();
    }

    private Emitter.Listener onReceiveData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String json = data.toString();
                    Toast.makeText(MainActivity.this, json, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        StringBuilder alert = new StringBuilder();
        for (String listener : listeners) {
            mSocket.off(listener, onReceiveData);
            alert.append("OFF ").append(listener).append("\n");
        }
        Toast.makeText(this, alert.toString(), Toast.LENGTH_SHORT).show();
    }
}
