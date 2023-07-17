package com.example.intercambios;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMessenger {

    private MainActivity activity;

    // ip and port of the server where the message has to be sent
    private String ip;
    private int port;

    public ClientMessenger(MainActivity activity, String ip, int port){
        this.activity = activity;
        this.ip = ip;
        this.port = port;
    }

    public void send(String message){
        Log.d("ClientMessenger","Sending messsage");
        Thread connecting = new Thread(new ConnectingThread(message));
        connecting.start();
    }

    public int getPort(){
        return port;
    }

    public String getIp(){
        return ip;
    }

    private void printMessage(Message message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.addMessageToConversation(message);
            }
        });
    }

    private class ConnectingThread implements Runnable{

        private String message;
        public ConnectingThread(String message){
            this.message = message;

        }

        @Override
        public void run() {
            Log.d("ClientMessenger","Running...");
            Socket clientSocket = null;
            try {
                clientSocket = new Socket(getIp(), getPort());

            } catch (IOException e) {
                Log.d("ClientMessenger","Failed to connect to the server. Make sure the server is running and the IP address is correct.",e);
            }
            try {
                // Create a socket object
                Log.d("ClientMessenger","Connected to server: " + getIp());

                // Send a message to the server
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream());
                output.println(message);
                output.flush();
                Log.d("ClientMessenger","Data has been sent");

                // Receive the response from the server
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String response = input.readLine();
                Log.d("ClientMessenger","Received response: " + response);

                JSONObject replyJSON = new JSONObject(response);
                String status = replyJSON.getString("status");
                int code = replyJSON.getInt("code");
                if (status.equals("OK") && code == 200) {
                       printMessage(new Message(getIp(),getPort(),message));
                }
                else {
                    Log.d("ClientMessenger","Wrong error code or code from the server answer");
                }
                // Close the socket connection
                clientSocket.close();
            } catch (Exception e) {
                Log.d("ClientMessenger","Failed to connect to the server. Make sure the server is running and the IP address is correct.",e);
            }
        }
    }

}
