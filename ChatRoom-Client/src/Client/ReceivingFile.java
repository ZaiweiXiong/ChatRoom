/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ReceivingFile implements Runnable{

      public Socket socket ;
      int bytesRead=0;
      BufferedReader in;
      PrintWriter out;
     
      FileOutputStream fos = null;
      BufferedOutputStream bos = null;
      
      static int FILE_SIZE;
      String FILE_TO_RECEIVED;
      String clientName;
      String filename;
       
      
     public ReceivingFile(int FILE_SIZE,String FILE_TO_RECEIVED,String clientName,String filename) {
    
       this.FILE_SIZE = FILE_SIZE;
       this.FILE_TO_RECEIVED=FILE_TO_RECEIVED;
       this.clientName = clientName;
       this.filename = filename;
       
      
    }
    @Override
    public void run() {
        
         try {
                 
               String host = ChatClient.SERVER;
               socket =new Socket(host, 9001);
                    
               in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               out = new PrintWriter(socket.getOutputStream(), true);
               
               out.println("CMD:receiveFile");  
              
              while (true) {
                  
                    String line = in.readLine();
                    
                     if (line.startsWith("CMD:VerifyUser")){
                
                       out.println("CMD:receiving:"+clientName+":"+filename);  
                         
                }else if (line.startsWith("ok")){
                
                    System.out.println("start receiving file.........."+clientName+":"+filename);
                    int current=0;
                    receiveFile(socket, fos,  bos , bytesRead, current);
                    break;
                }
              
              }
              
         }catch (Exception e) {
        
        
        }
            
       
    }
    
public void receiveFile(Socket sock, FileOutputStream fos, BufferedOutputStream bos ,int bytesRead,int current){
       
    try {
         
      System.out.println("Connecting...");

      // receive file
      byte [] mybytearray  = new byte [FILE_SIZE];
      InputStream is = sock.getInputStream();
      fos = new FileOutputStream(FILE_TO_RECEIVED);
      bos = new BufferedOutputStream(fos);
      bytesRead = is.read(mybytearray,0,mybytearray.length);
      current = bytesRead;

      do {
         bytesRead =
            is.read(mybytearray, current, (mybytearray.length-current));
         if(bytesRead >= 0) current += bytesRead;
      } while(bytesRead > -1);

      bos.write(mybytearray, 0 , current);
      bos.flush();
      System.out.println("File " + FILE_TO_RECEIVED
          + " downloaded (" + current + " bytes read)");
     
      
    }catch (IOException error){
        error.printStackTrace();
          System.out.println("error from ReceivingFile!");
    }
    finally {
        try {
            
             if (fos != null) fos.close();
             if (bos != null) bos.close();
              if (sock != null) sock.close();
              
        }catch (Exception error) {
                error.printStackTrace();
                System.out.println("sock close!");
        }
     
    }
   
 
  
  }

    
}
