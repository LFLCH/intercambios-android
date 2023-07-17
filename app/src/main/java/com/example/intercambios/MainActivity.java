package com.example.intercambios;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    public TextView server_ip_info;
    public TextView server_port_info;
    private ServerMessenger server;
    public EditText message;

    private ListView conversation_view;

    private ArrayList<Message> conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        server_ip_info = findViewById(R.id.ip_info);
        server_port_info = findViewById(R.id.port_info);

//        findViewById(R.id.addContentButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BottomSheetDialogSelector bottomSheetDialog = new BottomSheetDialogSelector(R.layout.content_selector);
//                bottomSheetDialog.show(getSupportFragmentManager(), "Content Selector");
//            }
//        });

        server = new ServerMessenger(this);
        server.startHosting();
        server_ip_info.setText(server.getLocalIpAddress());
        server_port_info.setText(server.getPort()+"");

        message = findViewById(R.id.message_tosend);


        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(server.isHosting())server.endHosting();
                server.startHosting();
                server_ip_info.setText(server.getLocalIpAddress());
                server_port_info.setText(server.getPort()+"");
            }
        });

        EditText ip_selector = findViewById(R.id.ip_selector);
        EditText port_selector = findViewById(R.id.port_selector);
        Button send_button = findViewById(R.id.send_message);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  text_port = port_selector.getText().toString();
                String ip = ip_selector.getText().toString();
                if(ip.length()==0 || text_port.length()==0){
                    Toast.makeText(MainActivity.this,"Please provide a valid port and ip",Toast.LENGTH_SHORT).show();
                }
                else {
                    int port = Integer.parseInt(text_port);
                    ClientMessenger client = new ClientMessenger(MainActivity.this,ip,port);
                    String msg = message.getText().toString();
                    client.send(msg);
                }
            }
        });

        conversation_view = findViewById(R.id.conversation);
        conversation = new ArrayList<>();

        conversation_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<conversation.size()){
                    Message m =conversation.get(position);
                    ip_selector.setText(m.getSourceIp());
                    port_selector.setText(m.getSourcePort()+"");
                    copyToClipboard(m.getValue());
                    Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied Text", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    public void addMessageToConversation(Message message){
        conversation.add(message);
        ArrayList<String> text_messages = conversation.stream().map(Message::getValue).collect(Collectors.toCollection(ArrayList::new));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,text_messages);
        conversation_view.setAdapter(adapter);
        conversation_view.setSelection(adapter.getCount()-1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.endHosting();
    }


}

