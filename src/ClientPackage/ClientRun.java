package ClientPackage;


public class ClientRun {
    public static void main(String[] args) {
        Client client = new Client(1719); // สร้างหน้าต่างของ client ขึ้นมาและทำการอ้าง port เพื่อที่จะได้เชื่อมกับ server ได้
        client.startClient(); // เริ่มต้นการทำงานของ client
    }
}
