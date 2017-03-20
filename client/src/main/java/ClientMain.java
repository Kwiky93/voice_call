import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by smirnov.evgeny on 07.03.2017.
 */
public class ClientMain {
  public static void main(String[] args) throws IOException {
    System.out.println("Welcome to Client side");
    Socket fromserver = null;

    if (args.length == 0) {
      System.out.println("use: client hostname");
      System.exit(-1);
    }

    System.out.println("Connecting to... " + args[0]);
    fromserver = new Socket(args[0], 45000);

    AudioFormat format = new AudioFormat(16000.0f, 8, 1, true, false); //Формат аудио

    Thread mic = new Thread(new RecAndSend(format, fromserver));
    mic.start();

    Thread reciv = new Thread(new RecivAndSpeak(format, fromserver));
    reciv.start();
  }
}//End class ClientMain