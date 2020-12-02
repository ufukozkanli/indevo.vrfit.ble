package de.kai_morich.simple_bluetooth_le_terminal;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 */
public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;
    InputStream input = null;
    OutputStream output = null;
    byte[] data = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void write(byte[] data) {
        setData(data);
    }

    public byte[] consumeData() {
        synchronized (this) {
            byte[] dt =this.data;
            this.data=null;
            return dt;
        }
    }

    public void setData(byte[] data) {
        synchronized (this) {
            this.data = data;
        }
    }

    public void run() {
        try {
            input = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
            Log.e("WORKERRUNNABLE", "clientConnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            byte[] dt = consumeData();
            if (dt == null) {
                continue;
            }
            try {
                output.write(dt);
                output.flush();
                Log.e("WORKERRUNNABLE", "WROTE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}