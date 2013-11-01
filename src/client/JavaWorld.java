//KLIJENTSKA STRANA APLIKACIJE
package src.client;

import src.client.hero.*;
import src.client.ship.*;
import src.client.universe.*;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.Iterator;

public class JavaWorld {
   //komunikacija sa Helper
  private static Socket clientSocket=null;
  private static BufferedReader in=null;
  private static PrintWriter out=null;
  

 //attr koji se odnose na simulaciju!
  private volatile static boolean end;
  
  private String winnersToString(int arg){
    String winners=""; 
    for(Ship tmp:(Universe.getUniverse()).getShips())
    {
      switch(arg){
        case 1: 
          if(tmp instanceof KlingonShip || tmp instanceof VulcanShip){
            winners+="\n"+tmp.getName();
            if(tmp.isDestroyed()) 
              winners+=" (destroyed)";
            winners+="\n"+tmp.crewToString();
          }
          break;
        case 2:
          if(tmp instanceof RomulanShip || tmp instanceof BorgShip){
            winners+="\n"+tmp.getName();
            if(tmp.isDestroyed()) 
              winners+=" (destroyed)";
            winners+="\n"+tmp.crewToString();
        }
        break;
     }
    }
    return winners;
  }

//NAPOMENA: Nema potrebe za sinhronizacijom - niti koriste razlicite monitore! Razlicite instance JavaWorld!
  
  public void announceWinners(){
  String announce="\nWar has ended.\nWinners are:\t";
    if((BorgShip.getCounter()==0)&&(RomulanShip.getCounter()==0)) 
  {
    System.out.println(announce+"Klingons and Vulcans!");
    System.out.println(winnersToString(1));
    end=true;
  }
    else if((KlingonShip.getCounter()==0)&&(VulcanShip.getCounter()==0))
    {
      System.out.println(announce+"Romulans and Borgs!");
      end=true;
      System.out.println(winnersToString(2));
    }
  }
  
  
  public static void main(String[] args){
    try{
  connect();
  System.out.println("-------------------- Simulation begins --------------------");
  final long startTime=System.currentTimeMillis();
  Universe universe=Universe.getUniverse(); // mora postojati TACNO JEDNA instanca klase svemir!Singleton objekat!
  System.out.println("The universe is created.");
  universe.createHeroes();
  for(int i=0;i<(universe.getHeroes()).length;i++)
    for(int j=0;j<(universe.getHeroes())[i].length;j++)
    universe.placeHero((universe.getHeroes())[i][j]);
  System.out.println("Heroes are created and placed randomly into universe.");
  universe.createShips();
  ArrayList<Ship> ships=universe.getShips();
  for(Ship tmp:ships)
    {
      tmp.boardCrew();
      universe.placeShip(tmp);
     }
    System.out.println("Ships are created, crew has been successfully boarded. Then, ships are randomly placed into universe.");

    ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream("universe.ser"));
    os.writeObject(universe);
    os.close();

    System.out.println("The war has began.");  
    
    Iterator iterator1=ships.iterator();
    while(iterator1.hasNext())
     ((Ship)iterator1.next()).start();
    Iterator iterator2=ships.iterator();
    while(iterator2.hasNext())
      ((Ship)iterator2.next()).join(); //main thread ceka na izvrsavanje Ship therad
    
    
    
    
    System.out.println("-------------------- Simulation ends --------------------");
    System.out.println("Total time:\t"+((System.currentTimeMillis()-startTime)/1000)+" [s]");
    System.out.println("\nThe starting formation of ships:");
    ObjectInputStream ois=new ObjectInputStream(new FileInputStream("universe.ser"));
    for(Ship tmp:((Universe)ois.readObject()).getShips())
    System.out.println(tmp.getName()+" started from "+tmp.positionToString());
    ois.close();
    }
    catch(Exception e){
      e.printStackTrace();
   }
  }
  
  public String translate(String sentence){
  String translatedSentence="Helper - Translated:\t";
  synchronized(Universe.getUniverse()){
  try{
    out.println(sentence); //salje serveru
    String fromServer;
    while((fromServer=in.readLine())!=null){
      if(fromServer.equals("exit"))
        break;  
      else
        translatedSentence+=fromServer;    
    }
  }
  catch(IOException e){
    e.printStackTrace();
    }
  return translatedSentence;
  }
 }
  
  public void store(Universe arg){}
  
  
  //za uspostavljanje konekcije sa Helperom i popunjavanje rijecnika
  public static void connect() {
  BufferedReader stdIn=null;
  InetAddress host=null;
    try{
      host=InetAddress.getLocalHost();
      clientSocket=new Socket(host,4444);
      out=new PrintWriter(clientSocket.getOutputStream(),true);
      in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      stdIn=new BufferedReader(new InputStreamReader(System.in)); //za citanje sa stIn
      String fromServer;  
      
      while((fromServer=in.readLine())!=null){
        System.out.println("Helper - "+fromServer);
        if(fromServer.equals("Dictionary is filled."))
          break;
      }
      
//      while((fromServer=in.readLine())!=null){
//        System.out.println("Helper - "+fromServer);
//        if(fromServer.equals("New enter?")||fromServer.equals("Enter pair (english - klingon):"))
//          out.println(stdIn.readLine()); 
//        if(fromServer.equals("Entering successfull."))
//          break;
//      }
      
      
    }
    catch(UnknownHostException e){
      System.err.println("Cannot find the host: "+host.getHostName());
    }
    catch(IOException e){
      System.out.println("Couldn't read/write from the connection: "+e.getMessage());
    }
   
   }

  public static boolean isEnd(){ //nema potrebe za synchronized!
    return end;
  }
}