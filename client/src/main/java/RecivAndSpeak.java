import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by smirnov.evgeny on 02.03.2017.
 */
public class RecivAndSpeak implements Runnable {
  private SourceDataLine speakers = null;
  private byte[] data = new byte[1024];
  private AudioFormat format = null;
  private Socket server;

  RecivAndSpeak(AudioFormat format, Socket server){
    this.format = format;
    this.server = server;
    createSpeak();
  }

  private void createSpeak(){
    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
    try {
      speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
      speakers.open(format);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    speakers.start();

    InputStream inSound = null;
    try {
      inSound = server.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
    }

    while (true){
      try {
        inSound.read(data);
        speakers.write(data, 0, data.length);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
