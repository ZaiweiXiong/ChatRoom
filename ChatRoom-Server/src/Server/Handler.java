package Server;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import javax.imageio.ImageIO;


public  class Handler extends Thread {
        
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private static HashSet<String> names;
        private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
        Map<String,Socket> map;
      
        private String str="";  
        private String temp="";
        
         ChatServerGUI chatServerGUI;
        
         public String FILE_TO_RECEIVED="./ServerFiles/";
                 
         public String FILE_TO_SEND ="./ServerFiles/";
               
          
         BufferedInputStream bis=null;
         OutputStream os=null;
            
        public Handler(Socket socket,HashSet<String> testnames) {
            this.socket = socket;
            this.names= testnames;
            
        }
        
           public Handler(Socket socket,HashSet<String> testnames, ChatServerGUI chatServerGUI, Map<String,Socket> map) {
               
            this.socket = socket;
            this.names= testnames;
            this.chatServerGUI=chatServerGUI;
            this.map = map;
            
        }

      
        public void run() {
            
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
                while (true) {
                    
                  
                    out.println("SUBMITNAME");
                    
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    
                    if (!name.contains("CMD")){
                        
                         synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                           
                            break;
                        }
                    }
                    
                }else {
                      
                       break;
                     
                    }
                   
                }
                
                // check online Users;
                
              if (!name.contains("CMD")){
                  
                map.put(name,socket);
                out.println("NAMEACCEPTED");
                chatServerGUI.appendMessage("User "+name+" has joined");
                writers.add(out);
                
                for (String s : names){
                    
                     str+=s+":";
                } 
                for (PrintWriter writer : writers) {
                    
                    writer.println("onlinePersons"+str);
                   
                }
                    
              } 
              
               // File sharing and client can receive broadcast
                while (true) {
                   
                   out.println("CMD:VerifyUser");
                
                   String input = in.readLine();
                   
                  //System.out.println("get input "+input);
                   
                   if (input.startsWith("CMD:Sender")){
                       
                          String [] strs =  input.split(":");
                           if (map.containsKey(strs[2])){
                             out.println("ok");
                           }else if (strs[2].equals("Anonymous")){
                                  out.println("ok");
                           }
                           else {
                              out.println("Client is not existed! ");
                           }
                   }
                   
                
                     
                    if (input == null) {
                        return;
                    }
                    
                 if (input.startsWith("CMD_FILESHARE")){
           
                     
                     String [] strs =  input.split(":");
                      name= strs[1];
                      String sendto=strs[3];
                      Socket tsoc = null;
                    
                     chatServerGUI.appendMessage("Start file sharing..."+name);
                     
                  
                     
                     // receiveFile(socket);
                     
                     if (!sendto.equals("Anonymous")) {
                         
                           
                         // send file to one;  
                     for (Map.Entry m:map.entrySet()){
                           
                           if (m.getKey().equals(sendto)) {
                                    //out.println("ok");
                                    tsoc=  (Socket)m.getValue();
                                    out =  new PrintWriter(tsoc.getOutputStream(), true);
                                    out.println("Image send to  2"+m.getKey() +" from: "+name+":"+strs[2]+":"+strs[4]);
                           }
                      
                      }
                     
                     }else {
                         
                      
                       
                      //send file to all 
                      for (Map.Entry m:map.entrySet()){
                           if (!m.getKey().equals(name)) {
                                    tsoc=  (Socket)m.getValue();
                                    out =  new PrintWriter(tsoc.getOutputStream(), true);
                                    out.println("Image send to 1 "+m.getKey() +" from: "+name+":"+strs[2]+":"+strs[4]);
                                    
                                    System.out.println("start send and receive!");
                                    
                                    
                           }
                      
                      }
                     
                     
                 }
                   
                               
                      
            }
                 if (input.startsWith("CMD:dragdropImage:")) {
                     
                          Socket tsoc=null;
                          String [] strs =  input.split(":");
                          FILE_TO_RECEIVED=  FILE_TO_RECEIVED+strs[3];
                          System.out.println("input->"+  FILE_TO_RECEIVED);
                            
                         BufferedImage image = ImageIO.read(socket.getInputStream());
    
                          try {
                             
                              ImageIO.write(image, "jpg", new File(FILE_TO_RECEIVED));//
            
                              }catch (IOException ex){
                                  
                                    ex.printStackTrace();
                            }
                         for (Map.Entry m:map.entrySet()){
                             
                           if (!m.getKey().equals(strs[2])) {
                               
                                    tsoc=  (Socket)m.getValue();
                                    out =  new PrintWriter(tsoc.getOutputStream(), true);
                                    out.println("dragdropImage:send:"+strs[2]+":"+strs[3]);
                                    
                           }
                      
                      } 
                           
                 
                 }
                 if (input.startsWith("CMD:ReceivingFileBydrag")){
                     
                         System.out.println("sssss"+input);
                         String [] strs =  input.split(":");
                         System.out.println("Start file receive..."+strs[2]);
                          out.println("dragAndDropOK:");
                         FILE_TO_SEND+=strs[3].trim();
                         System.out.println(" FILE_TO_SEND "+ FILE_TO_SEND);
                          //out.println("CMD:ReceivingFileBydrag:"+clientname+":"+filename);  
                         BufferedImage img = createImage(new File(FILE_TO_SEND)) ;
                         ImageIO.write(img, "jpg", socket.getOutputStream());
                         System.out.println("sent image from server!");
                         
                         try {
                           socket.close();
                         }catch(Exception error){
                         }
                        
                 }
                 
                 
                  if (input.startsWith("CMD:SendingFile:")){
                      
                            Socket tsoc=null;
                            String [] strs =  input.split(":");
                            FILE_TO_RECEIVED=  FILE_TO_RECEIVED+strs[3];
                            System.out.println( FILE_TO_RECEIVED);
                            String SendTo = strs[4];
                            receiveFile(socket);
                            //broast.. to ...
                            
                         if(!SendTo.equals("Anonymous")){
                             
                          for (Map.Entry m:map.entrySet()){
                           
                           if (m.getKey().equals(SendTo)) {
                                tsoc=  (Socket)m.getValue();
                                out =  new PrintWriter(tsoc.getOutputStream(), true);
                                out.println("Image send to  "+m.getKey() +" from: "+strs[3]+":"+strs[2]+":"+strs[4]);
                           }
                            
                            
                     }
                  }else {
                         for (Map.Entry m:map.entrySet()){
                             
                           if (!m.getKey().equals(strs[2])) {
                                    tsoc=  (Socket)m.getValue();
                                    out =  new PrintWriter(tsoc.getOutputStream(), true);
                                    //out.println("Image");
                                    out.println("Image send to  "+m.getKey() +" from: "+strs[3]+":"+strs[2]+":"+strs[4]);
                                    System.out.println("m.getKey()->"+m.getKey() );
                                    System.out.println("start send and receive!"+FILE_TO_RECEIVED);
                                    
                                    
                           }
                      
                      }
                         }         
                             
                  }
                   if ((input.startsWith("CMD:receiving:"))){
                        
                         String [] strs =  input.split(":");
                         chatServerGUI.appendMessage("Start file receive..."+strs[2]);
                         System.out.println("Start file receive..."+strs[2]);
                         out.println("ok");
                         FILE_TO_SEND+=strs[3].trim();
                         System.out.println("send path from server "+FILE_TO_SEND);
                         sendFile(socket, bis,  os);
                   
                   }
               
                  if (input.startsWith("CMD:Finished:")){
                      
                      String [] strs =  input.split(":");
                      name = strs[2];
                       
                           //System.out.println(input);
                      Socket tsoc = null;
                      for (Map.Entry m:map.entrySet()){
                          
                           if (!m.getKey().equals(name)) {
                                    tsoc=  (Socket)m.getValue();
                                    out =  new PrintWriter(tsoc.getOutputStream(), true);
                                    out.println("Completed sending file "+"to "+name);
                           }
                      
                      }
                     
                  }
                 
                    if (input.startsWith("CMD:Cancel:")){
                           String [] strs =  input.split(":");
                           name = strs[2];
                           //System.out.println(input);
                            Socket tsoc = null;
                      for (Map.Entry m:map.entrySet()){
                          
                           if (!m.getKey().equals(name)) {
                                    tsoc=  (Socket)m.getValue();
                                    out =  new PrintWriter(tsoc.getOutputStream(), true);
                                    out.println("Cancel sending "+"by client "+name);
                           }
                      
                      }
                     
                  }
                    
                   if (input.startsWith("Close:window")) {
                           String [] strs =  input.split(":");
                           name = strs[2];
                           names.remove(name);
                          if(map.containsKey(name)){
                             map.remove(name);
                         }
                          for (Map.Entry m:map.entrySet()){
                              
                                    if(m.getKey().equals(name)){
                                    //System.out.println("current name->"+m.getKey());
                                  }
                          }
                   }
                   
                    if (input.contains("Quit chatroom bye...")){
                        
                                      chatServerGUI.appendMessage("Quit chatroom bye..."+name);
                       }
                    
                    if(!input.contains("CMD:")){
                            
                           // Now that a successful name has been chosen, add the
                           // socket's print writer to the set of all writers so
                          // this client can receive broadcast
                          
                        if (!input.contains("CMD_FILESHARE")){
                        
                         for (PrintWriter writer : writers) {
                        
                               if (!input.equals("FILESHARE")) {
                                   
                                      if(!name.contains("CMD:SendFile")|!name.contains("CMD:receiveFile")){
                                          
                                           writer.println("MESSAGE " + name + ": " + input);
                                      }
                                   
                                   
                           }
                       }
                        
                     }
                        
              
               }
                  
        }
                
            
               
            } catch (IOException e) {
                 
               
                System.out.println(e);
                 names.remove(name);
                 writers.remove(out);
                 getOnlineList(names);
            
                 
                 
                 
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    names.remove(name);
                    if(map.containsKey(name)){
                        map.remove(name);
                    }
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
               
                } catch (IOException e) {
                  
                }
            }
        }
        
        public void getOnlineList(HashSet<String> names){
        
             
               
               for (String s : names){
                   
                     temp+=s+":";
                } 
                for (PrintWriter writer : writers) {
                    
                    writer.println("onlinePersons"+temp);
                     
                 
                 
                }
        }
        
 public  void receiveFile(Socket socket){
  
    try {
        
      System.out.println("receiveFile from test! "+FILE_TO_RECEIVED);
      FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVED);
      InputStream input = socket.getInputStream();   
      BufferedInputStream bis= new BufferedInputStream(input);
        /**  Create a temporary file **/
                                byte[] buffer = new byte[16022386];
                                int count= 0;
                                while((count = bis.read(buffer)) != -1){
                                  
                                    fos.write(buffer, 0, count);
                                }
                                fos.flush();
                                fos.close();
                                socket.close();
                             
    
      
    }catch (IOException e) {
                e.printStackTrace();
                System.out.println("error with receiveFile!");
    }
  
  }

 public  void sendFile(Socket sock, BufferedInputStream bis, OutputStream os){
  
    try {
     
      while (true) {
          
        System.out.println("Waiting...");
        
        try {
         
          System.out.println("Accepted connection : " + sock);
          // send file
          File myFile = new File (FILE_TO_SEND);
          byte [] mybytearray  = new byte [(int)myFile.length()];
          FileInputStream  fis = new FileInputStream(myFile);
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
   
    }catch (IOException error){
          error.printStackTrace();
          System.out.println("error from sendFile!");
    }
   
  }
 
 public BufferedImage createImage(File file) {
      
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (Exception ex) {
            System.out.println("Error reading file " + file.getName());
        }
       
        return img;
    }
 }