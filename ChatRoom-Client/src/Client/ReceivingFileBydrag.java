/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Student
 */
public class ReceivingFileBydrag implements Runnable{

    public Socket socket ;
    public String receivePath;
    String clientname;
    String filename;
    BufferedReader in;
    PrintWriter out;
    
    public ReceivingFileBydrag (Socket socket,String receivePath,String name,String filename){
    
        this.socket= socket;
        this.receivePath=receivePath;
        this.clientname=name;
        this.filename=filename;
  }
    @Override
    public void run() {
        
       
        
        try {
                
              String host = ChatClient.SERVER;
              socket =new Socket(host, 9001);
              in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
              out = new PrintWriter(socket.getOutputStream(), true);
              
              out.println("CMD:ReceivingFileBydrag"); 
              
              while (true) {
                  
                    String line = in.readLine();
                    
                     if (line.startsWith("CMD:VerifyUser")){
                        
                       out.println("CMD:ReceivingFileBydrag:"+clientname+":"+filename);  
                          System.out.println("receive image....2 "+receivePath);
                        
                         
                }else if (line.startsWith("dragAndDropOK:")){
                        
                    System.out.println(line);
                    BufferedImage image = ImageIO.read(socket.getInputStream());
                    System.out.println("receive image..3.."+receivePath);
                    
                          try {
                             
                              ImageIO.write(image, "jpg", new File(receivePath+filename));//
            
                              }catch (IOException ex){
                                  
                                    ex.printStackTrace();
                            }
                          
                           ImageIcon pictureImage = new ImageIcon(image);
                           JOptionPane.showMessageDialog(null, "", "Border States", 
                           JOptionPane.PLAIN_MESSAGE, pictureImage);
                           break;
                
                }
             
              }
              
              
        }catch (Exception error) {
                error.printStackTrace();
        
        }
        
        
       
    }
    
}
