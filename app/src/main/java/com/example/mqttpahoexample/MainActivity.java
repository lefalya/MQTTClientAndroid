package com.example.mqttpahoexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MemoryPersistence memPer = new MemoryPersistence();
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(
                this.getApplicationContext(), "tcp://192.168.121.133:1883", clientId, memPer);



        try {
            client.connect(null, new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken mqttToken) {

                    MqttMessage message = new MqttMessage("Hello, I am Android Mqtt Client.".getBytes());
                    message.setQos(2);
                    message.setRetained(false);

                    try {
                        client.publish("testing", message);
                        Log.i("MSSG", "Message published");

                        client.disconnect();
                        Log.i("MSSG", "client disconnected");

                    } catch (MqttPersistenceException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    } catch (MqttException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken arg0, Throwable arg1) {
                    // TODO Auto-generated method stub
                    Log.i("MSSG", "Client connection failed: "+arg1.getMessage());

                }
            });
        }
        catch (MqttException e){
            e.printStackTrace();
        }
    }


}

