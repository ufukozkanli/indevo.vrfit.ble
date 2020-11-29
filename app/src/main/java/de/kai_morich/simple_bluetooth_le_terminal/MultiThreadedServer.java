package de.kai_morich.simple_bluetooth_le_terminal;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class MultiThreadedServer implements Runnable {
    private final MultiServerListener listener;

    interface MultiServerListener {
        void onClientConnect(String serverAddress);
    }
    protected int serverPort = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;

    WorkerRunnable client = null;
    static int port=5678;
    public static MultiThreadedServer _current=null;

    public static MultiThreadedServer start(MultiServerListener listener)
    {
        if(_current==null) {
            _current = new MultiThreadedServer(port,listener);
            new Thread(_current).start();
        }
        return _current;
    }
    public static MultiThreadedServer get_current()
    {
        return _current;
    }

    public MultiThreadedServer(int port,MultiServerListener listener) {
        this.serverPort = port;
        this.listener=listener;
    }

    public void write(byte[] data) {
        if (client == null) {
            System.out.println("TCP:No Client connected");
            return;
        }
        client.write(data);
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                String address=clientSocket.getRemoteSocketAddress().toString();
                listener.onClientConnect(address);
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            client = new WorkerRunnable(
                    clientSocket, "Multithreaded Server");
            new Thread(client).start();
        }
        System.out.println("Server Stopped.");
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port ", e);
        }
    }

}