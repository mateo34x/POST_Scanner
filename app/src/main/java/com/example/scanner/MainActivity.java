package com.example.scanner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText ip;
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = findViewById(R.id.ip);
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
                Toast.makeText(MainActivity.this, "ip: "+ip.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void scanCode()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            System.out.println(result.getContents());
            sendMessageToServer(result.getContents());
            Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
            scanCode();
        }
    });

    private void sendMessageToServer(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ip.getText().toString(), 8080);
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(message.getBytes());
                    outputStream.flush();
                    socket.close();
                } catch (IOException e) {
                    Log.e("TAG", "Error al enviar mensaje al servidor: " + e.getMessage());
                }
            }
        }).start();
    }
}