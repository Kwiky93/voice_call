import java.util.concurrent.BlockingQueue;

/**
 * Created by smirnov.evgeny on 17.03.2017.
 */
public class Sender implements Runnable{
  BlockingQueue<SocketProcessor> q = null;
  final int BYT = 8*1024;

  Sender(BlockingQueue<SocketProcessor> q){
    this.q = q;
  }

  public void run() {
    while (true){
      byte[] dataSend = overlay();
      for (SocketProcessor sp : q) {
//          if(!sp.s.equals(this.s) && q.size() > 1) //Проверка чтобы не посылать самому себе
        sp.send(dataSend);
      }
    }

  }

  private synchronized byte[] overlay(){
    byte[] dataSend = new byte[BYT];
    boolean flag = true;

    for (SocketProcessor sp : q) {
      if (flag) {
        for (int i = 0; i < dataSend.length; i++)
          dataSend[i] = sp.data[i];
        flag = false;
      }

      for (int i = 0; i < dataSend.length; i++)
        dataSend[i] = sp.data[i];
    }
    return dataSend;
  }
}
