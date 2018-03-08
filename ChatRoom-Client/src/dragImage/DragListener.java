
package dragImage;



import Client.ChatClient;
import Client.SendFileClient;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;



public class DragListener implements DropTargetListener{
    
  
    private  JTextArea jTextArea1 ;
    private  JFrame frame  ;
    public static String clientname;
    SendFileClient sendfileclient;
    Socket  socket ;
    String SendTo;
    
    
    public DragListener ( JTextArea jTextArea1,JFrame frame) {
    
      
        this.jTextArea1 =  jTextArea1;
        this.frame =  frame;
      
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
      
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
      
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    
    }

    @Override
    public void drop(DropTargetDropEvent ev) {
       
        ev.acceptDrop(DnDConstants.ACTION_COPY);
        
        Transferable t = ev.getTransferable();
        
        DataFlavor[] df = t.getTransferDataFlavors();
        
        for (DataFlavor f : df){
        
              try {
                    
                  if (f.isFlavorJavaFileListType()) {
                        
                      List<File> files = (List<File>) t.getTransferData(f);
                      
                      for (File file : files) {
                          
                       displayImage(file.getPath());
                      }
              
                 }
              
              }catch (Exception ex) {
              
              
              }
        }
    }
     private int ShareImage() {
      return JOptionPane.showConfirmDialog(frame, "Hi "+clientname+","+" Do you want to share the file to ALL! ");
     }
    
    private void displayImage(String path) {
    
       BufferedImage img = null;
       
       
         if (ShareImage()==0 ){
             
          
             jTextArea1.append("share the image to all !"+"\n");
             try{
                 
                  String host = ChatClient.SERVER;
                  socket = new Socket(host, 9001);
                
                  
                  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                  PrintWriter out= new PrintWriter(socket.getOutputStream(), true);
                  
              out.println("CMD:SendFile");    
                   
      while (true) {
                       
                String line = in.readLine();
                
                  if (line.startsWith("CMD:VerifyUser")){
              
                     
                         
                          SendTo="Anonymous";
                     
                          out.println("CMD:Sender:"+SendTo);
                }
                  
          if (line.startsWith("ok")){
                 
                 
                String filename =new File(path).getName();
                 if (SendTo.equals(""))
                     
                 {  
                     SendTo="Anonymous";
                 }
            
                     out.println("CMD:dragdropImage:"+clientname+":"+filename +":"+SendTo);
                     File file = new File(path);
                     System.out.println(line+"dragdrop"+"CMD:SendFile:"+clientname+":"+filename +":"+SendTo+":"+file);
                     
                     img =  createImage(new File(path), clientname) ;
                     ImageIO.write(img, "jpg", socket.getOutputStream());
                     System.out.println("sent");
                       
                try {
                    socket.close();
                    break;
                } catch (IOException e) {
                  
                }
                       
                 
            }          
                   
   }
            
                   
             }catch (Exception error) {
                  
                    error.printStackTrace();
                    System.out.println("from dragdrog!");
             }finally{
             
                  
             }
               
         }else {
              jTextArea1.append("Cancel sharing image!"+"\n");
              
         }
       
       
    }
     public  BufferedImage  waterMark(String path,String name, BufferedImage image ){
    
        //BufferedImage image = null;
        try {
        image =ImageIO.read(new File(path));//
        
        }catch (IOException ex){
            
           ex.printStackTrace();
           
        
        }
    
       Graphics g = image.getGraphics();
       g.setColor(Color.red);
       g.setFont(g.getFont().deriveFont(30f));
       g.drawString(name, 46,30);
       g.dispose();
       try {
       ImageIO.write(image, "png", new File(path));//
       }catch (IOException ex){
           ex.printStackTrace();
       } 
       
       return image;
    }
     
  public BufferedImage createImage(File file,String name) {
      
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (Exception ex) {
            System.out.println("Error reading file " + file.getName());
        }
        Graphics2D g = (Graphics2D) img.getGraphics();
        Font font = new Font("Helvetica", Font.BOLD | Font.ITALIC, 64);
        g.setFont(font);
        g.setColor(Color.BLACK);
        //g.drawString(name, 2, 66);
        g.setColor(Color.WHITE);
        g.drawString(name, 0, 64);
        return img;
    }
    
}
