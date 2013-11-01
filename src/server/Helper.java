package src.server;

import java.io.*;
import java.net.*;

public class Helper {
  private static ServerSocket serverSocket=null;
  
  public static void main(String[] args){
    try{
      serverSocket=new ServerSocket(4444);
      while(true){
        Socket socket=serverSocket.accept();
        new HelperWorker(socket);
      }
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
}