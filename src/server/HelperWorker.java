package src.server;

import java.io.*;
import java.net.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

//serverska strana aplikacije
public class HelperWorker extends Thread {
  private static Socket clientSocket=null;
  private static BufferedReader in=null;
  private static PrintWriter out=null;
  private static Map<String,HashMap<String,String>> dictionary; //multimapa: <imefajla,engleska rijec,klingonska rijec> 
  
  
  public HelperWorker(Socket arg){
    clientSocket=arg;
    try{
    out=new PrintWriter(clientSocket.getOutputStream(),true);
    in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    catch(IOException e){
      e.printStackTrace();
    }
    start(); //pokrecemo worker thread
  }
      
  
  
  
  @Override
  public void run(){
    boolean i=true;
    try{
    
    fillDictionary();  //popuni rijecnik
    String fromClient;
//    while(i){ //novi unos
//      out.println("New enter?");
//      fromClient=in.readLine();
//      if(fromClient.equals("Yes")){
//        out.println("Enter pair (english - klingon):");
//        System.out.println("JavaWorld - "+fromClient);
//        out.println(newEnter(fromClient=in.readLine()));
//        System.out.println("JavaWorld - "+fromClient);
//      }
//      else if(fromClient.equals("No"))
//      break;
//      
//     }
    while(i){//prevodjenje
      while((fromClient=in.readLine())!=null){
        System.out.println("JavaWorld - "+fromClient);
        String[] words=fromClient.split(" ");
        for(String tmp:words)
          out.println(translate(tmp)+" "); 
        out.println("exit");
    }
    }
    }
    catch(Exception e){}
    
}
 
  private static String newEnter(String arg) throws IOException{
    String notification="";       
    Set keyFiles=dictionary.keySet();
    Iterator iteratorFiles=keyFiles.iterator();
    while(iteratorFiles.hasNext()){ //iteracija kroz fajlove
      String nameFile=(String)iteratorFiles.next();
      if(nameFile.startsWith(((arg.split(" - "))[0]).charAt(0)+"")){
        HashMap<String,String> pairs=dictionary.get(nameFile);
        if((pairs.put((arg.split(" - "))[0],(arg.split(" - "))[1])!=null))
          notification+="Pair already exists.";
        else
          notification+="Entering successfull.";  
             }
           }
           return notification;
         }
       
   
   
  
  
  private static void fillDictionary() throws IOException { //TACNO//
    BufferedReader inURL=null;
    try{
    dictionary=new HashMap<String,HashMap<String,String>>();
    URL urlDictionary=new URL("http://www.oocities.org/star_trek_unlimited/Language/English-to-Klingon.htm");
    inURL=new BufferedReader(new InputStreamReader(urlDictionary.openStream())); //za citanje iz URL
    //upisujemo u odgovarajuci fajl:
    File dictionaryFolder=new File("server/dictionary");
    dictionaryFolder.mkdir();
    String inputLine,englishWord,klingonWord;
    HashMap<String,String> pairs=null;
    while((inputLine=inURL.readLine())!=null){ //dok ne dodjes do kraja
        if (((Pattern.compile("<br>$")).matcher(inputLine)).find()) {  //linija zavrsava sa <br>
          if(((Pattern.compile("^<p>")).matcher(inputLine)).find()) { //prepoznao je pocetak stranice
             String nameFile=""+(((inputLine.split("<p>"))[1]).charAt(0))+".txt"; //kreiraj fajl
             PrintWriter pw=new PrintWriter(new FileWriter(new File(dictionaryFolder,nameFile)),true); //kreiranje i otvaranje fajla za prisanje
             englishWord=(((((inputLine.split("<p>"))[1]).split("<br>"))[0]).split(" - "))[0];
             klingonWord=(((((inputLine.split("<p>"))[1]).split("<br>"))[0]).split(" - "))[1];
             pw.println(englishWord+"##"+klingonWord); //upisuje prvu liniju
             pw.close();
             pairs=new HashMap<String,String>(); //mapa koja se vezuje za jedan fajl
             pairs.put(englishWord,klingonWord);
          }
          else if(((Pattern.compile("^[a-z]")).matcher(inputLine)).find()){
            if(inputLine.contains(" - ")){ //postoje greske - ne uredjen par (englishWord, klingonWord) 
              englishWord=(((inputLine.split("<br>"))[0]).split(" - "))[0];
              klingonWord=(((inputLine.split("<br>"))[0]).split(" - "))[1];
              pairs.put(englishWord,klingonWord);
         
              PrintWriter pw=new PrintWriter(new FileWriter(new File(dictionaryFolder,inputLine.charAt(0)+".txt"),true),true);//append
              pw.println(englishWord+"##"+klingonWord);
              pw.close();
            }
          }
          dictionary.put(inputLine.charAt(0)+".txt",pairs);
    }
    }
     out.println("Dictionary is filled.");       
    }
    catch(MalformedURLException e){}
    finally {
      inURL.close();
    }
  
  }
  
  //(TACNO)//
  public static String translate(String arg){ //prevodi tacno jednu rijec
    String translation="["+arg+"]";  //ukoliko ne postoji prevod
    Set keyFiles=dictionary.keySet(); 
    Iterator iteratorFiles=keyFiles.iterator();
    while(iteratorFiles.hasNext()){
      String nameFile=(String)iteratorFiles.next();
      if(nameFile.startsWith(arg.charAt(0)+"")) { //pronalazak odg. fajla, tj. kljuca
        HashMap<String,String> pairs=dictionary.get(nameFile); //mapa koja odgovara tom fajlu
        Set keyPairs=pairs.keySet(); //engleske rijeci
        Iterator iteratorPairs=keyPairs.iterator(); 
        while(iteratorPairs.hasNext()){
          String englishWord=(String)iteratorPairs.next();
          if(englishWord.equalsIgnoreCase(arg)) 
            translation=pairs.get(englishWord);
        }
      }
    }
    return translation;
  }
  
  
  public static void printDictionary(){ //TACNO//
    System.out.println("Dictionary content:");
    Set keyFile=dictionary.keySet();  //keySet - skup svih kljuceva
    Iterator iteratorFiles=keyFile.iterator();  //prakticno, iterira kroz fajlove
    while(iteratorFiles.hasNext()){
      String nameFile=(String)iteratorFiles.next();
      System.out.println("File:\t"+nameFile+".txt");
      HashMap<String,String> pairs=dictionary.get(nameFile);
      Set keyPairs=pairs.keySet();
      Iterator iteratorPairs=keyPairs.iterator(); //iterira kroz rijeci eng. jezika
        while(iteratorPairs.hasNext()){
          String englishWord=(String)iteratorPairs.next();
          System.out.println(englishWord+" - "+pairs.get(englishWord));
      }
    }
  }

}


