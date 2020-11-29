package de.kai_morich.simple_bluetooth_le_terminal;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    TextView txt1,txtSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createT();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();
        else
            onBackStackChanged();

    }

    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount()>0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void createT()
    {
        //VARIABLES
        txt1 = (TextView)findViewById(R.id.txt1);
        txtSettings = (TextView)findViewById(R.id.txtSettings);
        //SERVER
        String ipAddress=getIP();
        txtSettings.setText(ipAddress+":"+MultiThreadedServer.port);
        try {
            txt1.setText("Starting");
            createServer();
            txt1.setText("Started");
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();

            txt1.setText("ERROR:"+sStackTrace);

        }
    }
    public void createServer()  throws IOException {
        MultiThreadedServer.start(new MultiThreadedServer.MultiServerListener() {
            @Override
            public void onClientConnect(String serverAddress) {
                runOnUiThread(() -> {
                    txt1.setText("CONNECTED:"+serverAddress);
                });
            }
        });
    }
    public String getIP()
    {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);
        return ipAddress;
    }
}
