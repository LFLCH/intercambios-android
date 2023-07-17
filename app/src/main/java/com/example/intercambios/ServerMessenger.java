package com.example.intercambios;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

public class ServerMessenger {

    private MainActivity activity;
    private ServerSocket server_socket;

    public ServerMessenger(MainActivity activity){
        this.activity = activity;
    }

    public void startHosting(){
        Toast.makeText(activity,"Communication starting...",Toast.LENGTH_SHORT).show();
        try {
            server_socket = new ServerSocket(getPort());
            Thread connect =  new Thread(new ConnectingThread());
            connect.start();
        } catch (Exception e) { }
    }

    public boolean isHosting(){
        return server_socket!=null && !server_socket.isClosed();
    }

    public void endHosting(){
        try {
            server_socket.close();
            server_socket=null;
            Toast.makeText(activity,"Ended communication",Toast.LENGTH_SHORT).show();
        }catch (Exception e){}
    }

    public String getLocalIpAddress() {
        String ip = "0.0.0.0";

        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (ip.equals("0.0.0.0") && enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Log.d("ServerMessenger",networkInterface.getDisplayName());
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                while (ip.equals("0.0.0.0")&& enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    Log.d("ServerMessenger","inet adress : "+inetAddress.getHostAddress());

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Log.d("ServerMessenger","Error Ip",e);
        }

        return ip;
    }

    public int getPort(){
        return 8080;
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

        public ConnectingThread(){ }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Socket client_socket = server_socket.accept();
                    client_socket.setSoTimeout(2000);
                    Thread clientThread = new Thread(new ClientHandler(client_socket));
                    clientThread.start();
                }
            } catch (Exception e) {
                Log.d("ServerMessenger", "ConnectingThread encountered an error",e);
            }
        }

    }

    private class ClientHandler implements  Runnable{

        private Socket client_socket;

        public ClientHandler(Socket client_socket){
            this.client_socket = client_socket;
        }

        @Override
        public void run(){
            try {
                PrintWriter output = new PrintWriter(client_socket.getOutputStream());
                BufferedReader input = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
                InetAddress clientAddress = client_socket.getInetAddress();
                String clientIP = clientAddress.getHostAddress();

                // Create a separate thread to handle receiving the message
                Thread receivingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String receivedMessage = input.readLine();
                            printMessage(new Message(clientIP,getPort(),receivedMessage));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Create a new thread to send the reply message
                Thread replyThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Send the reply message back to the client
                            String replyMessage = "{\"status\":\"OK\", \"code\":200}";
                            output.print(replyMessage);
                            output.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                receivingThread.start();
                replyThread.start();

                receivingThread.join();
                replyThread.join();

                client_socket.close();
            }catch (Exception e){}
        }

    }
}
