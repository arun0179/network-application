package ServerPackage;


public class ServerRun {
    public static void main(String[] args) {
        Server server = new Server(); // สร้าง Server side window ขึ้นมา
        server.startServer(); // ทำการ start server (to wait + set up + chat with a client)
    }
}
