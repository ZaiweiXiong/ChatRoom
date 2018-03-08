package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class ServerThread implements Runnable  {
    
     private  ServerSocket socketserver ;
     private ChatServerGUI chatServerGUI;
     private static  int PORT ;
     private boolean keepGoing=true;
     static HashSet<String> names = new HashSet<String>();
     
     public static  Map<String,Socket> map=new HashMap<String,Socket>();  
    
    public ServerThread(ServerSocket server,ChatServerGUI chatServerGUI,int port){
    
        this.socketserver = server;
        this.chatServerGUI = chatServerGUI;
        this.PORT =port;
       
        try {
            
             
             System.out.println("The chat server is running.");
             chatServerGUI.appendMessage("The chat server is running." + PORT);
             socketserver = new ServerSocket(PORT);
            
        }catch (IOException ex) {
        
                ex.printStackTrace();
        }
    
    }

    @Override
    public void run()  {
        
        
        try {
           
            try {
                int i=1;
                while (keepGoing) {
                    
                    new Handler(socketserver .accept(),names,chatServerGUI,map).start();
                    System.out.println("Connected successfully!"+i);
                    chatServerGUI.appendMessage("Connected successfully!");
                    i++;
                }
                
            } finally {
                socketserver .close();
            }
            
        }    catch (IOException ex) {
            
             chatServerGUI.appendMessage("[IOException]: "+ ex.getMessage());
              System.out.println("close server!");
            
         }
        
    }
   
public void stop(){
        
         try {
            socketserver.close();
            keepGoing = false;
            System.out.println("Server is now closed..!");
            System.exit(0);
            
        } catch (IOException e) {
            System.out.println("close server!");
        }
    }
    
}
