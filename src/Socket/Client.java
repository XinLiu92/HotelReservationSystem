package Socket;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    public static final String IP_ADDR = "localhost";//服务器地址
    public static final int PORT = 12345;//服务器端口号
    private int id;
    private ArrayList<Integer> orderNumber = new ArrayList<>();
    private Object orderLock = new Object();

    public static void main(String[] args) {
        Client client = new Client();
        client.startClient();
        //client.startFakeClient(3);
    }


    public  void startClient()
    {
        System.out.println("Client starts...");
        //System.out.println("当接收到服务器端字符为 \"Exit\" 的时候, 客户端将终止\n");
        int flag = 0;
        while (true) {

            Socket socket = null;
            try {
                //创建一个流套接字并将其连接到指定主机上的指定端口号
                socket = new Socket(IP_ADDR, PORT);
                if(flag == 0)
                {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String temp = in.readUTF();
                    id = Integer.parseInt(temp);
                    System.out.println("My id is: "+id);
                    //System.out.println("flag is: "+flag);

                }
                flag++;
                //System.out.println(id);
                // 如接收到 "OK" 则断开连接
                //System.out.println(" ");
                System.out.println("*********************Room Booking Service********************");
                System.out.println("");
                System.out.println("                   Please select a service");
                System.out.println("");
                System.out.println("1. List of all rooms.");
                System.out.println("2. Check availability of a room.");
                System.out.println("3. Book a room.");
                System.out.println("4. Cancel a room ");
                System.out.println("");
                System.out.println("");
                System.out.println("Select a number between 1 and 4, Exit to exit");
                System.out.println("");
                System.out.flush();
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream input = new DataInputStream(socket.getInputStream());
                while(true)
                {

//                    String id = input.readUTF();
//                    System.out.println(id);
                    String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    if(str.equals("Exit"))
                    {
                        System.out.println("Client closes soon...");
                        out.writeUTF("Exit");
                        //socket.close();
                        break;
                    }
                    out.writeUTF(str);
                    String ret = input.readUTF();
                    System.out.println(ret);
                    if ("Exit".equals(ret)) {
                        System.out.println("Client closes soon...");
                        Thread.sleep(500);
                        break;
                    }

                }
                out.close();
                input.close();
                break;
            } catch (Exception e) {
                System.out.println("客户端异常:" + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                        System.out.println("客户端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
    }

    public void startFakeClient( int opt)
    {
        System.out.println("Client starts...");
        int flag = 0;
        while(true)
        {
            Socket socket = null;
            try {
                socket = new Socket(IP_ADDR, PORT);
                if(flag == 0)
                {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String temp = in.readUTF();
                    id = Integer.parseInt(temp);
                    System.out.println("My id is: "+id);
                   // System.out.println("flag is: "+flag);

                }
                flag++;
//                System.out.println("*********************Room Booking Service********************");
//                System.out.println("");
//                System.out.println("                   Please select a service");
//                System.out.println("");
//                System.out.println("1. List of all rooms.");
//                System.out.println("2. Check availability of a room.");
//                System.out.println("3. Book a room.");
//                System.out.println("4. Cancel a room.");
//                System.out.println("");
//                System.out.println("");
//                System.out.println("Select a number between 1 and 4, 0 to exit");
//                System.out.println("");
//                System.out.flush();
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream input = new DataInputStream(socket.getInputStream());
                Random random = new Random();
                Random ram = new Random();
                while(true)
                {

                   // int randNum = random.nextInt(3) + 1;
                    int randNum = opt;
                    if(randNum == 1)
                    {
                        out.writeUTF("1");
                        System.out.println("1");
                        String ret = input.readUTF();            // list of all rooms.
                        System.out.println(ret);

                    }else if(randNum ==2 )
                    {

                        out.writeUTF("2");
                        System.out.println("2");
                        int randChoice = ram.nextInt(3) + 1; // random choose room 1-4.
                        input.readUTF(); // receive some hints about to choose the room.
                        out.writeUTF("Room"+ Integer.toString(randChoice)); // send the room which the fake client wanna check.
                        input.readUTF(); // receive some hints about to choose the day.
                        randChoice = ram.nextInt(7) + 1; // range of day: 1 - 7
                        out.writeUTF(Integer.toString(randChoice));
                        input.readUTF(); // receive some hints about to choose the door number.
                        randChoice = ram.nextInt(5) + 1; // range of door number is from 1 - 5.
                        out.writeUTF(Integer.toString(randChoice));
                        String ret = input.readUTF(); // receive the final results.
                        System.out.println(ret);

                    }else if(randNum == 3)
                    {

                        out.writeUTF("3");
                        System.out.println("3");
//                        input.readUTF(); // receive the hints about enter client id
//                        out.writeUTF(Integer.toString(id));

                        input.readUTF(); // receive some hints about to choose room.
                        int randChoice = ram.nextInt(3) + 1; // range of room is 1-4.
                        out.writeUTF("Room"+Integer.toString(randChoice));
                        input.readUTF(); // receive some hints about to choose the day.
                        randChoice = ram.nextInt(7) + 1; // the range of the day is from 1 - 7.
                        out.writeUTF(Integer.toString(randChoice));
                        input.readUTF(); // receive some hints about to choose the door number.
                        randChoice = ram.nextInt(4) + 1; // door number from 1 - 5
                        out.writeUTF(Integer.toString(randChoice));
                        //System.out.println("+++++++++++++++++++++++");
                        String ret = input.readUTF(); // receive final result.
                        //System.out.println("-------------------------");
                        String[] temp = ret.split(",");
                        //ordStr = ordStr.replaceAll("\n","");
                        //System.out.println("+++++"+temp[1]);
                        int ord = Integer.parseInt(temp[1]);
                        orderNumber.add(ord);
                        System.out.println("Book result is: "+ret);


                    }else if(randNum == 4)
                    {
                        out.writeUTF("4");
                        System.out.println("4");
                        input.readUTF(); // receive the hints about enter order number
                        out.writeUTF(Integer.toString(orderNumber.get(0)));    // should be order number
                        System.out.println("ordernumber: "+orderNumber.get(0));
                        orderNumber.remove(0);
                       // input.readUTF();
//                        int randChoice = ram.nextInt(3)+1;
//                        out.writeUTF("Room"+Integer.toString(1));
//                        input.readUTF(); // receive some hints about to choose the day.
//                        randChoice = ram.nextInt(7); // the range of the day is from 0 - 6.
//                        out.writeUTF(Integer.toString(0));
                        //input.readUTF(); // receive some hints about to choose the start time.
                        //randChoice = ram.nextInt(12);
                        //out.writeUTF(Integer.toString(randChoice));
                        String ret = input.readUTF(); // receive final result.
                        System.out.println("Cancel result is: "+ret);
                    }
                }

            } catch (IOException e) {
                System.out.println("客户端异常:" + e.getMessage());

            }
        }
    }

    public void mimicBookRoom(int opt, String roomName, int day, int doorNum) throws IOException {
        Socket socket = null;
        socket = new Socket(IP_ADDR, PORT);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream input = new DataInputStream(socket.getInputStream());
        input.readUTF(); //receive id

        out.writeUTF(Integer.toString(opt));
        input.readUTF();
        out.writeUTF(roomName);
        input.readUTF();
        out.writeUTF(Integer.toString(day));
        input.readUTF();
        out.writeUTF(Integer.toString(doorNum));
        input.readUTF();
        //System.out.println("done");

    }

    public void mimicCancelRoom(int opt, String roomName, int day, int doorNum) throws IOException {
        Socket socket = null;
        socket = new Socket(IP_ADDR, PORT);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream input = new DataInputStream(socket.getInputStream());
        input.readUTF(); //receive id

        out.writeUTF(Integer.toString(opt));
        input.readUTF();
        out.writeUTF(roomName);
        input.readUTF();
        out.writeUTF(Integer.toString(day));
        input.readUTF();
        out.writeUTF(Integer.toString(doorNum));
        input.readUTF();
        // above is book part.


        synchronized (orderLock)
        {
            for(int i = 0; i < orderNumber.size(); i++)
            {
                //System.out.println("-------------------------------");
                out.writeUTF("4");
                String temp = input.readUTF();
                System.out.println(temp);
                out.writeUTF(Integer.toString(orderNumber.get(i)));
                input.readUTF();
            }
        }

    }
}
