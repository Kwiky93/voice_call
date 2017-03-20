import javax.sound.sampled.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by smirnov.evgeny on 02.03.2017.
 */
public class RecAndSend implements Runnable {
  private TargetDataLine microphone = null;  //Объект микрофона
  private byte[] data = new byte[8*1024];
  private AudioFormat format;
  private Socket server;

  RecAndSend(AudioFormat format, Socket server){
    this.format = format;
//    this.data = data;
    this.server = server;
    createMic();
  }

  private void createMic(){
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    try {
      microphone = (TargetDataLine) AudioSystem.getLine(info);
      microphone.open(format);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    microphone.start();
    OutputStream outSound = null;
    try {
      outSound = server.getOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true){
      try {
        microphone.read(data, 0, data.length);
        outSound.write(data);
        outSound.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
