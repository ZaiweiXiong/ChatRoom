package Client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import javax.swing.JOptionPane;


public class SendFileClient implements Runnable{
    
     
      BufferedReader in;
      PrintWriter out;
     
     
      Socket  socket ;
     
      public String FILE_TO_SEND = "";
      
      String UserName ="";
      String SendTo="";
     
     public SendFileClient(String name){
         
     this.UserName = name;
     
    
     }
    @Override
    public void run() {
        
        try {
        
        String host = ChatClient.SERVER;
        socket = new Socket(host, 9001);
        
        if (socket.isConnected()){
            System.out.println(socket);
        }else {
            System.out.println("NO");
        }
    
        //new Socket(host, 9001);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
         
       out.println("CMD:SendFile");     
         
        while (true){
            
      
                 
          String line = in.readLine();
          
          if (line.startsWith("CMD:VerifyUser")){
              
                     if (SendTo.equals("")){
                         
                          SendTo="Anonymous";
                     }
                   out.println("CMD:Sender:"+SendTo);
                }
          
            if (line.startsWith("ok")){
                 
                 
                 String filename = new File(FILE_TO_SEND).getName();
                 if (SendTo.equals(""))
                 {  
                     SendTo="Anonymous";
                 }
                 
  
                   
                  File file = new File(FILE_TO_SEND);
                 
                 
        
                 out.println("CMD:SendingFile:"+UserName+":"+filename +":"+SendTo+":"+"IP");
               
                 System.out.println(line+" sendFile()"+"CMD_FILESHARE:"+UserName+":"+filename +":"+SendTo+":"+"IP"+":"+file);
                 
                 
                 System.out.println("start send file!");

                   sendFile(socket);
                  
              
                }
            
            if(line.startsWith("Client is not existed!")){
                        JOptionPane.showMessageDialog(null, "Client is not existed!");
                        break;
            }
            
        
        }
     
        
        }catch (Exception error){
       
        }
        
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
            
            System.out.println("(int)myFile.length() " + (int)myFile.length());
            
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
    
 
  
 
    
}
