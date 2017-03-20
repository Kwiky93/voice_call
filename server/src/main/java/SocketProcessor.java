import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by smirnov.evgeny on 27.02.2017.
 */
public class SocketProcessor implements Runnable {
  Socket s;         // наш сокет
  InputStream br;   // буферизировнный читатель сокета
  OutputStream bw;  // буферизированный писатель в сокет
  final int BYT = 8*1024;
  byte[] data =  new byte[BYT];
  BlockingQueue<SocketProcessor> q = new LinkedBlockingQueue<SocketProcessor>();

  SocketProcessor(Socket socketParam, BlockingQueue<SocketProcessor> q) throws IOException {
    this.s = socketParam;
    this.br = s.getInputStream();
    this.bw = s.getOutputStream();
    this.q = q;
  }

  public void run() {
    while (!s.isClosed()) { // пока сокет не закрыт...
      Integer line = null;
      try {
        line = br.read(data); // пробуем прочесть.
        /*for (SocketProcessor sp : q) {
//          if(!sp.s.equals(this.s) && q.size() > 1) //Проверка чтобы не посылать самому себе
          sp.send(overlay()*//*data*//*);
        }*/
      } catch (IOException e) {
        close(); // если не получилось - закрываем сокет.
      }
    }
  }

  public synchronized void send(byte[] data) {
    try {
      bw.write(data); // пишем строку
      bw.flush(); // отправляем
    } catch (IOException e) {
      close();
    }
  }

  public synchronized void close() {
    q.remove(this); // убираем из списка
    if (!s.isClosed()) {
      try {
        s.close(); // закрываем
        System.out.println("User exit: " + Thread.currentThread().getName());
      } catch (IOException ignored) {
      }
    }
    System.out.println("Count users: " + q.size());
  }

  private synchronized byte[] overlay(){
    byte[] dataSend = new byte[BYT];
    boolean flag = true;

    for (SocketProcessor sp: q){
      if(flag){
        for(int i = 0; i < dataSend.length; i++)
            dataSend[i] = sp.data[i];
        flag = false;
      }

      for(int i = 0; i < dataSend.length; i++)
        dataSend[i] = sp.data[i];
    }

    /*for (SocketProcessor sp : q) {
//      if(!sp.s.equals(this.s) && q.size() > 1) { //Проверка чтобы не посылать самому себе
        for(int i = 0; i < sp.data.length; i++)
          dataSend[socProcNum + i] = sp.data[i];
//      }
      socProcNum = sp.data.length;
    }*/
    return dataSend;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    close();
  }
}
