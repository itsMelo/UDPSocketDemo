package melo.com.udpsocketdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import melo.com.udpsocketdemo.socket.UDPSocket;

/**
 * Created by melo on 2017/9/20.
 */

public class UDPSocketActivity extends AppCompatActivity {

    private UDPSocket socket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        final EditText etMessage = (EditText) findViewById(R.id.et_message);
        Button btSend = (Button) findViewById(R.id.bt_send);


        socket = new UDPSocket(this);
        socket.startUDPSocket();


        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.sendMessage(etMessage.getText().toString());
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.stopUDPSocket();
    }
}
