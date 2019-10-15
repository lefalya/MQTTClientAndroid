package com.example.mqttpahoexample;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {
    public String command = "a";
    public MqttAndroidClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final MemoryPersistence memPer = new MemoryPersistence();
        final String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(
                this.getApplicationContext(),
                "tcp://192.168.121.104:1883",
                clientId,
                memPer);


        Button ledSwitch = (Button) findViewById(R.id.ledSwitch);

        ledSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(client, clientId, memPer);
            }
        });



        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            //addToHistory("Connecting to " + serverUri);
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    client.setBufferOpts(disconnectedBufferOptions);
                    try {
                        client.subscribe("test", 0, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.d("dari app","Subscribed!");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.d("dari app","Failed to subscribe");
                            }
                        });

                        // THIS DOES NOT WORK!
                        client.subscribe("test", 0, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                // message Arrived!
                                String val = new String(message.getPayload());
                                Log.d("dri app","Message: " + topic + " : " + val);
                                setTextView(val);
                            }
                        });

                    } catch (MqttException ex){
                        System.err.println("Exception whilst subscribing");
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }

    }

    public void setTextView(String voltage){
        TextView textVoltage = (TextView) findViewById(R.id.textView2);
        try {
            textVoltage .setText(voltage);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendCommand (final MqttAndroidClient client, String clientId, MemoryPersistence memPer){
        Log.i("MSSG", "SMG");
        try {
            client.connect(null, new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken mqttToken) {

                    if(command == "a") command = "b";
                    else command = "a";

                    MqttMessage message = new MqttMessage(command.getBytes());
                    message.setQos(2);
                    message.setRetained(false);

                    try {
                        client.publish("testing", message);
                        Log.i("MSSG", "Message published");

                   

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

