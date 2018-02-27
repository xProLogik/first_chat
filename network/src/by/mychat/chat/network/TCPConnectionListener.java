package by.mychat.chat.network;

/**
 * Интерфейс событий
 */
public interface TCPConnectionListener {
  void onConnectionReady(TCPConnection tcpConnection);
  void onReadString(TCPConnection tcpConnection, String value);
  void onDisconnect(TCPConnection tcpConnection);
  void onException(TCPConnection tcpConnection, Exception e);
}
