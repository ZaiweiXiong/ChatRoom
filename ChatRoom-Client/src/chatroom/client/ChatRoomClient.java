/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.client;

import Client.ChatClient;
import Client.ClientGUI;

/**
 *
 * @author David
 */
public class ChatRoomClient {

    /**
     * @param args the command line arguments
     */
    //static ChatClient  chatClient;
    public static void main(String[] args) {
        
            ClientGUI clientGui =  new  ClientGUI();
        
            ClientGUI.messageArea.setEditable(false);
            ClientGUI.jButton1.requestFocus(true);
          
            ClientGUI.chatClient = new ChatClient( ClientGUI.jTextField1, ClientGUI.messageArea,clientGui,ClientGUI.txtpane2);
         
           Thread clientThread = new Thread(ClientGUI.chatClient);
           clientThread.start();
           clientGui.connectToDragDrop();
        
    }
    
}
