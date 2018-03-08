/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Client.ReceivingFile.FILE_SIZE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 *
 * @author David
 */
public class ClientMethod {
    
    String FILE_TO_SEND="";
    String FILE_TO_RECEIVED="";
    
 public ClientMethod() {
 
 }
 
 public  void sendFile( Socket socket ){
                    
try {
      
          System.out.println("Waiting... from sendFile");
          System.out.println("Accepted connection : " +  socket);
          // send file
          File myFile = new File (FILE_TO_SEND);
       
          FileInputStream fis = new FileInputStream(myFile);
          BufferedInputStream bis = new BufferedInputStream(fis);
          OutputStream output = socket.getOutputStream();
           
     
            /** Create a temporary file storage **/
            byte[] buffer = new byte[(int)myFile.length()];
            
         
            
            int count= 0;
            while((count = bis.read(buffer)) > 0){
              
                output.write(buffer, 0, count);
            }
               output.flush();
               output.close();
               System.out.println("Done.");
        
        
  
     
    }catch (IOException error){
          error.printStackTrace();
          System.out.println("error from sendFile!");
    }
   
  }
 
  public  void sendFile(  ServerSocket servsock ,Socket sock,  FileInputStream fis,BufferedInputStream bis, OutputStream os,int SOCKET_PORT ){
  
    try {
      
      servsock = new ServerSocket(SOCKET_PORT);
        
      while (true) {
          
        System.out.println("Waiting...");
        
        try {
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);
          // send file
          File myFile = new File (FILE_TO_SEND);
          byte [] mybytearray  = new byte [(int)myFile.length()];
          fis = new FileInputStream(myFile);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = sock.getOutputStream();
          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray,0,mybytearray.length);
          os.flush();
          System.out.println("Done.");
        }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (sock!=null) sock.close();
        }
        break;
      }
      servsock.close();
    }catch (IOException error){
          error.printStackTrace();
          System.out.println("error from sendFile!");
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
               System.out.println("sock close!");
        }
     
    }
   
 
  
  }
  
  public static String  gethostname() {
    String hostname="";
    try
       {
                   InetAddress addr;
                   addr = InetAddress.getLocalHost();
                   hostname = addr.getHostName();
        }
                catch (UnknownHostException ex)
            {
                  System.out.println("Hostname can not be resolved");
            }
                return hostname;
   }
  
   public static String  getIP() throws UnknownHostException, SocketException{
        
    System.out.println("Your Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
    Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
    for (; n.hasMoreElements();)
    {
        NetworkInterface e = n.nextElement();

        Enumeration<InetAddress> a = e.getInetAddresses();
        for (; a.hasMoreElements();)
        {
            InetAddress addr = a.nextElement();
            //System.out.println(" s " + addr.getHostAddress());
        }
    }
    return InetAddress.getLocalHost().getHostAddress();
    
    }
  
  
    
}
