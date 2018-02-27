package by.mychat.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Реализует TCP соеденения
 */
public class TCPConnection {
  private final Socket socket;
  private final TCPConnectionListener eventListener;
  private final Thread rxThread;
  private final BufferedReader in;
  private final BufferedWriter out;

  public TCPConnection(TCPConnectionListener eventListener,String ipAddress, int port) throws IOException {
    this(eventListener, new Socket(ipAddress,port));
  }

  public TCPConnection(TCPConnectionListener eventListener,Socket socket) throws IOException {
    this.socket = socket;
    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
    this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
    this.eventListener = eventListener;
    this.rxThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          eventListener.onConnectionReady(TCPConnection.this);
          while (!rxThread.isInterrupted()) {
            eventListener.onReadString(TCPConnection.this, in.readLine());
          }
        } catch (IOException e) {
          eventListener.onException(TCPConnection.this, e);
        } finally {
          eventListener.onDisconnect(TCPConnection.this);
        }
      }
    });
    this.rxThread.start();

  }

  public synchronized void sendMessage(String value) {
    try {
      out.write(value + "\r\n");
      out.flush();
    } catch (IOException e) {
      eventListener.onException(this, e);
      disconnect();
    }
  }

  public synchronized void disconnect() {
    rxThread.interrupt();
    try {
      socket.close();
    } catch (IOException e) {
      eventListener.onException(this, e);
    }
  }

  @Override
  public String toString() {
    return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
  }
}
