/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom.server;

import Server.ChatServerGUI;

/**
 *
 * @author David
 */
public class ChatRoomServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
           ChatServerGUI chatServer = new  ChatServerGUI();
           chatServer.jTextField1.setEditable(false);
           chatServer.ServerLog.setEditable(false);
           chatServer.setVisible(true);
         
    }
    
}
