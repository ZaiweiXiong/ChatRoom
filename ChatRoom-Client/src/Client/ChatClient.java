package Client;

import dragImage.DragListener;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;



/**
 *
 * @author David
 */
public class ChatClient   implements Runnable {
    
   
      private JTextField textField;
      private JTextArea messageArea;
      private JTextPane txtpane2;
   
      public static String clientName;
      private JFrame frame;
      public static Socket socket ;
     
    
      public static String SERVER ; 
   
      public  static String FILE_TO_RECEIVED = "";
      public  BufferedReader in;
      public  PrintWriter out;
  
     
     public final static int FILE_SIZE = 16022386;
     int port;
          
public ChatClient(JTextField textField,JTextArea messageArea,JFrame frame,JTextPane txtpane2) {
    
         this.textField =textField;
         this.messageArea =messageArea;
         this.frame = frame;
         this.txtpane2 = txtpane2;
 }
public ChatClient(){

}

  public String getName() {
            clientName= JOptionPane.showInputDialog(
            frame,
            "please input name to enter chat room:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
            return clientName;
    }
  public String getHost(){
  
            SERVER= JOptionPane.showInputDialog(
            frame,
            "please input IP address to enter chat room:",
            "Screen  for host IP ",
            JOptionPane.PLAIN_MESSAGE);
            if (validIP(SERVER)){
                 return SERVER;
             }
            return getHost();
  
  }
  
  public String getIPandport(){
    
        
      JTextField xField = new JTextField(20);
      JTextField yField = new JTextField(8);

      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("Host IP:"));
      myPanel.add(xField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Port"));
      myPanel.add(yField);

     int result = JOptionPane.showConfirmDialog(null, myPanel, 
               "Please Enter host IP and port ", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
          
          if (validIP(xField.getText())){
                port = Integer.parseInt(yField.getText());
               return SERVER=xField.getText();
            }
       
      }
      
            return  getIPandport();
  }
    @Override
    public void run() {
       
        
        try {
         

        // Make connection and initialize streams
    
         socket = new Socket(getHost(), 9001);
       
        in = new BufferedReader(new InputStreamReader(
        socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
             
          
             
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                
                if (!getName().equals("")){
                out.println(clientName);
                
               
                DragListener.clientname=clientName;
                frame.setTitle("welcome to "+ clientName);
                frame.setVisible(true);
                }
               
            } 
            
            else if (line.startsWith("onlinePersons")) {
                
                HashSet<String> set = new HashSet<String>();
                String s = line.substring(13);
                 String [] strs = s.split(":");
                 for (int i=0;i<strs.length;i++){
                     
                       set.add(strs[i]);
                      
                 }
                        sampleOnlineList(set);
               
                 
               
            } else if (line.startsWith("NAMEACCEPTED")){
                
                        textField.setEditable(true);
                        messageArea.append("welcome enter chatroom!"+"\n");
            }
            
            else if(line.startsWith("disconnected")){
                   
                        messageArea.append("Client "+line.substring(12)+" has disconnected!"+"\n");
                     
                      
            }
            
            
            
            else if (line.startsWith("MESSAGE")) {
                
                
                messageArea.append(line.substring(8) + "\n");
                
                
            }else if (line.startsWith("Image")){
                
                     System.out.println(line);
                     
                    String[] strs = line.split(":");
                  
                    System.out.println(line);
                      
                   
                     int confirm = JOptionPane.showConfirmDialog(frame, "From: "+strs[2]+"\nFilename: "+strs[1]+"\nwould you like to Accept.?");
                    
                    if (confirm==0){
                        
                       System.out.println("yes");
                  
                    
                     
                     FILE_TO_RECEIVED = "./fileshare/"+strs[1];
                     
                     ReceivingFile receiveFile = new ReceivingFile(FILE_SIZE,  FILE_TO_RECEIVED,clientName,strs[1]);
                     Thread  ReceivingFileThread = new Thread( receiveFile);
                     ReceivingFileThread.start();
                    
                    
                     messageArea.append("From "+strs[2]+" share the file to folder  "+" fileshare "+strs[1]+"\n");
                     out.println("CMD:Finished:"+clientName);
                   
                    }else {
                        
                       messageArea.append("Canceled file sharing!"+"\n");
                       out.println("CMD:Cancel:"+clientName);
                       System.out.println("Canceled file sharing!");
                 }
                    
            }else if (line.startsWith("dragdropImage:send:")) {
            
                   
                   String[] strs = line.split(":");
                   int confirm = JOptionPane.showConfirmDialog(frame, "From: "+strs[2]+"\nFilename: "+strs[3]+"\nwould you like to Accept.?");
                   
                   if (confirm==0){
                       
                         FILE_TO_RECEIVED = "./fileshare/";
                        ReceivingFileBydrag imagereceive = new ReceivingFileBydrag(socket,FILE_TO_RECEIVED,clientName,strs[3]);
                        Thread ReceivingFileBydrag  = new Thread(imagereceive );
                        ReceivingFileBydrag.start();
                        
                     }else {
                          messageArea.append("Canceled file sharing for drag drop"+"\n");
                   }
            }
            
            
            else if (line.startsWith("Cancel sending"+"\n")){
                
                  messageArea.append(line+"\n");
            
            }else if (line.startsWith("Completed sending file ")){
                        messageArea.append(line+"\n");
            }
            
          }
        }catch (Exception error){
        
        }
       
    }
    
     public void Send (String X) {
     
         out.println(X);
     
     }
     public void disconected(){
       try {
             socket.close();
       }catch (IOException error) {
        error.printStackTrace();
       }
    
     }
    private void sampleOnlineList(HashSet<String> set){
          
                     txtpane2.setEditable(true);
                     txtpane2.removeAll();
                     txtpane2.setText("");
                     
                     for (String s :set) {
                         
                      JPanel panel = new JPanel();
                      panel.setLayout(new FlowLayout(FlowLayout.LEFT));
                      panel.setBackground(Color.white);
            
                     Icon icon = new ImageIcon(this.getClass().getResource("/images/online.png"));
                     JLabel label = new JLabel(icon);
                     label.setText(s.toString());
                     panel.add(label);
                     int len = txtpane2.getDocument().getLength();
                     txtpane2.setCaretPosition(len);
                     txtpane2.insertComponent(panel);
                      
                       sampleAppend();
              }
                    txtpane2.setEditable(false);
      }
      
    private void sampleAppend(){
           
        int len = txtpane2.getDocument().getLength();
        txtpane2.setCaretPosition(len);
        txtpane2.replaceSelection("\n");
    }
    
    public static boolean validIP (String ip) {
    try {
        if ( ip == null || ip.isEmpty() ) {
            return false;
        }

        String[] parts = ip.split( "\\." );
        if ( parts.length != 4 ) {
            return false;
        }

        for ( String s : parts ) {
            int i = Integer.parseInt( s );
            if ( (i < 0) || (i > 255) ) {
                return false;
            }
        }
        if ( ip.endsWith(".") ) {
            return false;
        }

        return true;
    } catch (NumberFormatException nfe) {
        return false;
    }
}
    

   
}
