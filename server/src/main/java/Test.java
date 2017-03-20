import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by smirnov.evgeny on 17.03.2017.
 */
public class Test {
  private static AudioFormat format;
  private static TargetDataLine microphone = null;  //Объект микрофона
  private static SourceDataLine speakers = null;
  private static byte[] data = new byte[8*1024];
  private static List<byte[]> list1 = new ArrayList<byte[]>();
  private static List<byte[]> list2 = new ArrayList<byte[]>();
  private static List<byte[]> list3 = new ArrayList<byte[]>();

  public static void main(String[] args){
    format = new AudioFormat(16000, 16, 1, true, false);
    createMic();
    createSpeak();

    /*try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }*/

//    list1 = readFromFile("1_record");
    list1 = pisk((byte) 10000);
    list2 = readFromFile("2_record");
//    filterLowNoise(data);

    System.out.println("Воспроизведение 1 началось");
    speakers.start();
    for(byte[] dat: list1)
      speakers.write(dat, 0, dat.length);
    speakers.stop();
    System.out.println("Воспроизведение 1 закончилось");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Воспроизведение 2 началось");
    speakers.start();
    for(byte[] dat: list2)
      speakers.write(dat, 0, dat.length);
    speakers.stop();
    System.out.println("Воспроизведение 2 закончилось");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    overlay();

    System.out.println("Воспроизведение 3 началось");
    speakers.start();
    for(byte[] dat: list3)
      speakers.write(dat, 0, dat.length);
    speakers.stop();
    System.out.println("Воспроизведение 3 закончилось");

  }
  /*----- ------------------------------------------------------------- -----*/
  //Создание записывающего устройства
  private static void createMic(){
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
    try {
      microphone = (TargetDataLine) AudioSystem.getLine(info);
      microphone.open(format);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }
  //Создание воспроизводящего устройства
  private static void createSpeak(){
    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
    try {
      speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
      speakers.open(format);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }
  //Объединение звуковых потоков
  private static void overlay(){
    byte[] dat1 = new byte[8*1024];
    byte[] dat2;

    for(int i = 0; i < 10; i++) {
//      dat1 = list1.get(i);
      dat2 = list2.get(i);
      for (int j = 0; j < 8 * 1024; j++)
        dat1[j] = (byte) ((/*(dat1[j] + */dat2[j]) / 2);
      list3.add(dat1);
    }
    /*for(int i = 0; i < 10; i++){
      dat1 = list1.get(i);
      dat2 = list2.get(i);
    }*/

  }
  //Фильтр низких шумов
  private static void filterLowNoise(byte[] data){
    if(data == null)
      return;
    for (int i = 0; i < data.length; i++){
      data[i] = (byte) Math.max(data[i], 0);
    }
  }
  //Запись в файл звукового объекта
  private static void writeToFile(List<byte[]> list, String fileName){
    try{
      FileOutputStream fos = new FileOutputStream(fileName);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(list);
      oos.flush();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  //Чтение из файла звукового объекта
  private static List<byte[]> readFromFile(String fileName){
    try {
      FileInputStream fis = new FileInputStream(fileName);
      ObjectInputStream ois = new ObjectInputStream(fis);
      return (List<byte[]>) ois.readObject();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  //Запись с микрофона
  private static void record(){
    System.out.println("Запись началась");
    microphone.start();
    while (list1.size() < 10) {
      data = new byte[8*1024];
      microphone.read(data, 0, data.length);
      list1.add(data);
    }
    microphone.stop();
    System.out.println("Запись закончилась");
  }

  private static List<byte[]> pisk(byte size){
    byte[] dat;
    List<byte[]> list = new ArrayList<byte[]>();

    for(int i = 0; i < 10; i++) {
      dat = new byte[8*1024];
      for (int j = 0; j < 8 * 1024; j++)
        dat[j] = size;
      list.add(dat);
    }
    return list;
  }
}
