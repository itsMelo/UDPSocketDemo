package melo.com.udpsocketdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CLIENT_PORT = 8000;
    private static final int SERVER_PORT = 3000;
    private byte bufClient[] = new byte[1024];
    private byte bufServer[] = new byte[1024];
    private static final int BUF_LENGTH = 1024;

    private DatagramSocket client;
    private DatagramPacket dpClientSend;
    private DatagramPacket dpClientReceive;
    private DatagramSocket server;
    private DatagramPacket dpServerReceive;

    private TextView tvClient;
    private TextView tvServer;
    private Thread threadServer;
    private Thread threadClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvClient = (TextView) findViewById(R.id.tv_client);
        tvServer = (TextView) findViewById(R.id.tv_server);
        Button btSend = (Button) findViewById(R.id.bt_send);
        btSend.setOnClickListener(this);

        createServer();
        createClient();
    }

    /**
     * 创建客户端
     */
    private void createClient() {
        try {
            //创建客户端，并且指定端口号，在此端口号侦听信息。
            client = new DatagramSocket(CLIENT_PORT);

            dpClientReceive = new DatagramPacket(bufClient, BUF_LENGTH);

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将中文字符转码发送
     *
     * @param strSend
     */
    private byte[] createSendData(String strSend) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(baos);
        try {
            dataStream.writeUTF(strSend);
            dataStream.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 将中文解码接收
     *
     * @param dp
     */
    private String createReceiveData(DatagramPacket dp) {
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(dp.getData(),
                dp.getOffset(), dp.getLength()));
        try {
            final String msg = stream.readUTF();
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void starClientThread() {
        //创建用来发送的 DatagramPacket 数据报，其中应该包含要发送的信息，以及本地地址，目标端口号。
        threadClient = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] sendData = createSendData("客户端发来了一个消息");
                    InetAddress clientAddress = InetAddress.getLocalHost();
                    //创建用来发送的 DatagramPacket 数据报，其中应该包含要发送的信息，以及本地地址，目标端口号。
                    dpClientSend = new DatagramPacket(sendData, sendData.length, clientAddress, SERVER_PORT);
                    client.send(dpClientSend);

                    while (true) {
                        client.receive(dpClientReceive);
                        final String receiveData = createReceiveData(dpClientReceive);
                        tvClient.post(new Runnable() {
                            @Override
                            public void run() {
                                tvClient.setText(receiveData);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadClient.start();
    }

    /**
     *
     */
    private void createServer() {
        try {
            server = new DatagramSocket(SERVER_PORT);
            dpServerReceive = new DatagramPacket(bufServer, BUF_LENGTH);
            startServerThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void startServerThread() {
        threadServer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        server.receive(dpServerReceive);
                        final String receiveData = createReceiveData(dpServerReceive);
                        tvServer.post(new Runnable() {
                            @Override
                            public void run() {
                                tvServer.setText(receiveData);
                            }
                        });

                        byte[] sendData = createSendData("已经收到客户端的消息");
                        DatagramPacket dpServerSend = new DatagramPacket(sendData, sendData.length, dpServerReceive.getAddress(), dpServerReceive.getPort());
                        server.send(dpServerSend);

                        dpServerReceive.setLength(BUF_LENGTH);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        threadServer.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                starClientThread();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        threadClient.interrupt();
        threadServer.interrupt();

        client.close();
        server.close();
    }
}
