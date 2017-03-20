import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by smirnov.evgeny on 07.03.2017.
 */
public class ServerMain {
  public static void main(String[] args){
    ServerSound server = null;

    try {
      server = new ServerSound(45000);
      Thread t = new Thread(server);

      t.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
    String commandLine = null;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    end:
    while (true){
      try {
        commandLine = br.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (commandLine.equals("shutdownServer")) {
        server.shutdownServer();
        System.out.println("Server stoped");
      } else if (commandLine.equals("exit")) {
        break end;
      } else if (commandLine.equals("connection")) {
        System.out.println("Connections: " + server.clientsCount());

      }
    }
    System.out.println("Program closed");
  }
}
