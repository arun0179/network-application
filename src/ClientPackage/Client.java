package ClientPackage;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


public class Client {

    private JTextField textField, ipField;
    private JTextArea textArea;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket connection;
    private int serverPort;
    private boolean exit;
    private boolean exitClient;
//    private String serverIP;
    private  JFrame jFrame;


    public Client(int portNo) {
        //  new JFrame to create window
        jFrame = new JFrame("Client Side"); // window's title
        exit = false;
        exitClient = false;
        serverPort = portNo;

        // components in JFrame
        textField = new JTextField();
        textArea = new JTextArea();
//        ipField = new JTextField();

        // initial component's setting when create at first
        textField.setEditable(false);
        textArea.setEditable(false);

        //add action to text field (enter btn)
        // when type message in text field and enter will do this part
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // when enter message will go to function send message that will be the output to client side
                // then set text field to be empty string
                sendMessage(e.getActionCommand()); // e.getActionCommand will get text in text field
                textField.setText("");
            }

        });

//        ipField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                serverIP = ipField.getText();
//                ipField.setText("");
//                System.out.println(serverIP);
//                showMessage(serverIP);
//                startClient();
//            }
//        });

        // add components to JFrame
//        jFrame.add(ipField, BorderLayout.NORTH);
        jFrame.add(textField, BorderLayout.SOUTH); // add text field  to JFrame
        jFrame.add(new JScrollPane(textArea)); // when you append the message in own side and client's side and there are maybe have a lot of messages so should create scroll bar
        jFrame.setSize(400,500); // set window size
        jFrame.setVisible(true); // set window can be visible
    }

    // to start client
    public void startClient(){
        while (true){
            try{
                // when you start the client
                connectToServe(); // connect with the server ด้วยการอ้างถึง IP ของ server และ port number
                setUpStream(); // set Output and Input Stream
                whileChatting(); // ในช่วงการสนทนานั้นจะมีการส่งข้อความไปให้อีกฝัั่งและการแสดงข้อความใน text area
                closeClient(); // close the connection in client's side
                break; // break การทำงาน
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ทำการ connect กับ server โดยที่ client เปิด socket ขึ้นมาแล้ว อ้างถึง IP ของ server และ port number
    private void connectToServe() {
        try {
            textArea.append("Connect to Server : ");
            connection = new Socket(InetAddress.getByName("localhost"),serverPort); // connect with server โดยการอ้างถึงเลขไอพีและพอร์ท
            textArea.append(connection.getInetAddress().getHostName()); // show server's ip that has a connection with
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // set up input and output stream, so can read and send to other side
    private void setUpStream() {
        try {
            outputStream = new ObjectOutputStream(connection.getOutputStream());// send message to the other side
            outputStream.flush();
            inputStream = new ObjectInputStream(connection.getInputStream()); // read message from the other side
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // while chatting
    private void whileChatting() {
        String message = "\nNow we're connected!";
        textArea.append(message); // แสดง message บน text area มามีการเชื่อมต่อเรียบร้อยแล้ว
        textField.setEditable(true); // ในตอนแรกเราจะยังไม่สามารถพิมพ์ตอบโต้ได้เนื่องจากยังไม่มีการเชื่อมต่อได้ จึงทำการเปิด text field ให้้พิมพ์ต่อได้
        do{
            try{
                message = (String) inputStream.readObject(); // ทำการอ่านข้อความของอีกฝั่งที่ส่งเข้ามา
                textArea.append(message); // แล้วนำมาแสดงในช่อง text area
                String msg[] = message.split(" "); // ตรวจสอบการพิมพ์ exit ของ server
                if (msg[2].equalsIgnoreCase("exit")){
                    closeClient();
                    break; // เมื่อ server/client type exit จะทำการ  break การสนทนา (จบการสนทนา)
                }
            }catch (Exception e){
                System.err.println("Client closed the connection.");
            }
        } while (!exitClient);
    }

    // เมื่อเกิด action กับ text field นั่นคือการ send message ไป client's side
    private void sendMessage(String message) {
        try{
            outputStream.writeObject("\nClient : " + message); // use output stream to write the message
            outputStream.flush(); // clean the output stream
            if (message.equalsIgnoreCase("exit")){ // client type exit
                exitClient = true;
                closeClient();
            }
            textArea.append("\nClient : " + message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // close the connection
    private void closeClient() {
        textArea.append("\nConnection is closed.\n");
        textField.setEditable(false);
        try{
            inputStream.close();
            outputStream.close();
            connection.close();
            System.exit(0); // finish process and close the window
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
