import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by smirnov.evgeny on 17.02.2017.
 */

public class ServerSound implements Runnable{
  private ServerSocket ss;    //Сокет сервера
  private Thread serverThread;//Основной поток сервера
  BlockingQueue<SocketProcessor> q = new LinkedBlockingQueue<SocketProcessor>();//Блокирующая очередь

  //Конструктор класса
  ServerSound(int port) throws IOException {
    ss = new ServerSocket(port);          // создаем сервер-сокет
    System.out.println("ServerSound started: " + Inet4Address.getLocalHost().getHostAddress()); // Вывод сообщения о запуске сервера
  }

  //Основной поток класса
  public void run() {
    serverThread = Thread.currentThread();//Получить осовной поток программы
    new Thread(new Sender(q)).start();
    while (true) {
      Socket s = getNewConn(); // получить новое соединение или фейк-соедиение

      if (serverThread.isInterrupted()) {
        break;
      } else if (s != null) { // "только если коннект успешно создан"...
        try {
          final SocketProcessor processor = new SocketProcessor(s, q); // создаем сокет-процессор
          final Thread thread = new Thread(processor);  //Создаем поток для этого сокет-процессора
          System.out.println("Created thread: " + thread.getName() + " IP: " + s.getInetAddress() + " Socket: " + s);
          thread.setDaemon(true); // ставим ее в демона (чтобы не ожидать ее закрытия)
          thread.start();         // запускаем сокет-процессор
          q.offer(processor);     // добавляем в список активных сокет-процессоров
          System.out.println("Count users: " + q.size());
        }
        catch (IOException ignored) {
          ignored.printStackTrace();
        }
      }
    }
  }
  //Функция получения новых подключений
  private Socket getNewConn() {
    Socket s = null;
    try {
      s = ss.accept();
    } catch (IOException e) {
      shutdownServer(); // если ошибка в момент приема - "гасим" сервер
    }
    return s;
  }
  //Функция синхронизированного выключения сервера
  protected synchronized void shutdownServer() {
    // обрабатываем список рабочих коннектов, закрываем каждый
    for (SocketProcessor s : q) {
      s.close();
    }
    if (!ss.isClosed()) {
      try {
        ss.close();
      } catch (IOException ignored) {
      }
    }
    Thread.currentThread().interrupt();
  }
  //Функция получения количества коннектов
  public int clientsCount(){
    return q.size();
  }
}