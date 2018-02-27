package by.mychat.chat.client;

import by.mychat.chat.network.TCPConnection;
import by.mychat.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Реализация клиентской стороны
 */
public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener{

  private static final String IP_ADDRESS = "127.0.0.1";
  private static final int PORT = 8081;
  private static final int WIDTH = 600;
  private static final int HEIGHT = 400;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ClientWindow());
  }
  private final JTextArea log = new JTextArea();
  private final JTextField nickname = new JTextField("Andrey");
  private final JTextField fieldInput = new JTextField();
  private final JScrollPane scrollPane = new JScrollPane(log);
  private TCPConnection tcpConnection;

  private ClientWindow(){
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(WIDTH,HEIGHT);
    setLocationRelativeTo(null);
    setAlwaysOnTop(true);

    log.setEditable(false);
    log.setLineWrap(true);
    add(scrollPane, BorderLayout.CENTER);

    fieldInput.addActionListener(this);
    add(nickname,BorderLayout.NORTH);
    add(fieldInput,BorderLayout.SOUTH);

    setVisible(true);
    try {
      tcpConnection = new TCPConnection(this,IP_ADDRESS,PORT);
    } catch (IOException e) {
      printMessage("Connection exception: " + e);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String msg = fieldInput.getText();
    if (msg.equals("")) return;
    fieldInput.setText(null);
    tcpConnection.sendMessage(nickname.getText() + ": " + msg);
  }

  @Override
  public void onConnectionReady(TCPConnection tcpConnection) {
    printMessage("Connection ready...");
  }

  @Override
  public void onReadString(TCPConnection tcpConnection, String value) {
    printMessage(value);
  }

  @Override
  public void onDisconnect(TCPConnection tcpConnection) {
    printMessage("Connection close");
  }

  @Override
  public void onException(TCPConnection tcpConnection, Exception e) {
    printMessage("Connection exception: " + e);
  }

  private synchronized void printMessage(String msg){
    SwingUtilities.invokeLater(() -> log.append(msg+"\n"));
  }
}
