package ServerPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private JTextField textField;
    private JTextArea textArea;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket socket;
    private Socket connection;
    private boolean exit;

    public Server() {
        JFrame jFrame = new JFrame("Server Side"); // window's title
        exit=false; // ตัวแปรสำหรับการ break การทำงานของ server เมื่อ server types the word "EXIT"

        // components in JFrame
        textField = new JTextField();
        textArea = new JTextArea();

        // initial component's setting when create at first
        textField.setEditable(false);
        textArea.setEditable(false);

        // when type message in text field and enter will do this part
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // when enter message will go to function send message that will be the output to client side
                // then set text field to be empty string
                sendMessage(e.getActionCommand());
                textField.setText("");
            }

        });
        // add components to JFrame
        jFrame.add(textField, BorderLayout.SOUTH); // add text field  to JFrame
        jFrame.add(new JScrollPane(textArea)); // when you append the message in own side and client's side and there are maybe have a lot of messages so should create scroll bar
        jFrame.setSize(400,500); // set window size
        jFrame.setVisible(true); // set window can be visible
    }

    // to start server
    public void startServer(){
        try{
            // when you start the server, will new the Server socket to open port (server)
            exit = false;
            socket = new ServerSocket(1719);
            while (true){
                try{
                    waitForConnection(); // wait for connect with client
                    setUpStream(); // set Output and Input Stream
                    whileChatting(); // ในช่วงการสนทนานั้นจะมีการส่งข้อความไปให้อีกฝัั่งและการแสดงข้อความใน text area
                    closeServer(); // close the connection
                    break;
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    // wait for the connection
    private void waitForConnection() {
        try{
            textArea.append("Waiting for client's connection\n"); // show message in text area
            connection = socket.accept(); // เมื่อมีการเชื่อมต่อเข้ามาด้วยการอ้างถึง IP และ port ก็เริ่มทำการเชื่อมต่อ
            textArea.append("Connect to client : " + connection.getInetAddress().getHostName() + "\n"); // show connection's ip that has a connection with
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // set up input and output stream
    private void setUpStream() {
        try {
            outputStream = new ObjectOutputStream(connection.getOutputStream()); // to send message to the other side
            outputStream.flush(); // clean the output stream
            inputStream = new ObjectInputStream(connection.getInputStream()); // to read message from other side
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // while chatting
    private void whileChatting() {
        String message = "Now we're connected!";
        textArea.append(message); // แสดง message บน text area มามีการเชื่อมต่อเรียบร้อยแล้ว
        textField.setEditable(true); //ในตอนแรกเราจะยังไม่สามารถพิมพ์ตอบโต้ได้เนื่องจากยังไม่มีการเชื่อมต่อได้ จึงทำการเปิด text field ให้้พิมพ์ต่อได้
        do {
            try {
                message = (String) inputStream.readObject(); // ทำการอ่านข้อความของอีกฝั่งที่ส่งเข้ามา
                textArea.append(message); // แล้วนำมาแสดงในช่อง text area
                String msg[] = message.split(" ");  // ตรวจสอบการพิมพ์ exit ของ client
                if (msg[2].equalsIgnoreCase("exit")) {
                    closeServer();// the function to close connection
                    textArea.setText(""); // clear old message when old connection is closed.
                    break;
                }
            } catch (Exception e) {
                System.err.println("Server closed the connection.");
            }
        } while (!exit);
        if(!exit){
            startServer(); // and start server again to wait another client's connection
        }
    }

    // เมื่อเกิด action กับ text field นั่นคือการ send message ไป client's side
    private void sendMessage(String message) {
        try{
            outputStream.writeObject("\nServer : " + message); // use output stream to write the message (send to the other side)
            outputStream.flush(); // clean the output stream
            textArea.append("\nServer : " + message);// show message in own side in text area
            if (message.equalsIgnoreCase("exit")) { // server type exit
                exit = true;
                textArea.append("\nConnection is closed.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // close the connection
    private void closeServer() {
        textField.setEditable(false);
        try{
            outputStream.close();
            inputStream.close();
            connection.close();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
