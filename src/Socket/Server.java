package Socket;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    public static final int PORT = 12345;//监听的端口号
    //private AtomicInteger id;
    private int id;
    private ArrayList<Room> roomList;
    private AtomicInteger orderNumber;
    private ConcurrentHashMap<AtomicInteger, String> map;
    private Object idLock;
    //public Object orderLock;

    public Server() {
        // id = new AtomicInteger(0);
        id = 0;
        roomList = new ArrayList<>();
        map = new ConcurrentHashMap<>();
        idLock = new Object();
        //orderNumber = 1000;
        orderNumber = new AtomicInteger(1000);
       // orderLock = new Object();
    }

    public static void main(String[] args) {
        System.out.println("Server starts...\n");
        Server server = new Server();
        server.init();
    }

    public void start() {
       // System.out.println("Server starts...");
        init();

    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // synchronized (serverSocket) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接
                synchronized (this) {
                    Socket client = serverSocket.accept();
//                       id.getAndIncrement();
//                       System.out.println("id is: " + id);

                    initRooms();
                    // 处理这次连接
                    new HandlerThread(client);
                }
                // }
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;

        public HandlerThread(Socket client) {
            socket = client;

            new Thread(this).start();
        }

        public void run() {
            try {
                String clientInputStr;
                int flag = 0;
//                idOut.close();
                //int time = 0;
                while (true) {

                    // 向客户端回复信息
//                    DataOutputStream idOut = new DataOutputStream(socket.getOutputStream());
//                    idOut.writeUTF("Your ID: "+id);
                    // 发送键盘输入的一行
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    //读取客户端数据
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    String hint = "\n*********************Room Booking Service********************";
                    hint += " \n                   Please select a service \n";
                    hint += "1. List of all rooms.\n";
                    hint += "2. Check availability of a room.\n";
                    hint += "3. Book a room.\n";
                    hint += "4. Cancel a room.\n";
                    hint += "Select a number between 1 and 4, Exit to exit\n";
                    if (flag == 0) {

                        //id.getAndIncrement();
                        //System.out.println("ID: "+id);
                        synchronized (idLock) {
                            out.writeUTF(Integer.toString(id));
                            id++;

                        }

                        //id++;

                    }

                    clientInputStr = input.readUTF();//这里要注意和客户端输出流的写方法对应,否则会抛 EOFException
                    int clientIn = Integer.parseInt(clientInputStr);
                    if (clientIn == 0) {
                        socket.close();
                        break;
                    }
                    System.out.println(clientIn);
                    if (clientIn == 1) {
                        //System.out.println("Im in");

                        String all = allRooms();
                        String str = "The full list of rooms is as follows \nRoom|Capacity \n ";
                        str += all;
                        str += " \n";
                        str += hint;
                        out.writeUTF(str);


                    } else if (clientIn == 2) {
                        String str = "Check a room \nEnter the room name \n";
                        out.writeUTF(str);
                        String roomName = input.readUTF();
                        str = "Enter the day - \n1=Mon, 2=Tues, 3=Wed, 4=Thurs, 5=Fri, 6=Sat, 7=Sun";
                        out.writeUTF(str);

                        String day = input.readUTF();
                        //str = "Enter the start time - \n0=8am, 1=9am, 2=10am, 3=11am, 4=12pm, 5=1pm, 6=2pm, 7=3pm, 8=4pm, 9=5pm, 10=6pm, 11=7pm";
                        //out.writeUTF(str);
                        str = "Enter the door number you want: \n";
                        out.writeUTF(str);
                        String doorNum = input.readUTF();
                        //String time = input.readUTF();

                        String result = checkRoom(roomName, Integer.parseInt(day), Integer.parseInt(doorNum));
                        result += " \n";
                        result += hint;
                        out.writeUTF(result);
                    } else if (clientIn == 3) {
//                        String str = "Enter your ID\n";
//                        out.writeUTF(str);
                        //String tempID = input.readUTF();
                        String str = "Room Booking Service - Rooms can be booked from 8am to 8pm\nTime slots go from 0 for 8am up to 11 for 7pm - Enter a value in this range\n";
                        str += "Enter the room name\n";
                        out.writeUTF(str);
                        String roomName = input.readUTF();
                        //System.out.println("Room Name: "+ roomName);
                        str = "Enter the day -\n1=Mon , 2=Tues, 3=Wed ,4=Thurs , 5=Fri, 6=Sat, 7=Sun\n";
                        out.writeUTF(str);
                        String day = input.readUTF();

                        str = "Enter the door number you want: \n";
                        out.writeUTF(str);
                        String doorNum = input.readUTF();
                        //str = "Enter the start time -\n0=8am , 1=9am , 2=10am , 3=11am , 4=12pm , 5=1pm , 6=2pm , 7=3pm , 8=4pm , 9=5pm , 10=6pm , 11= 7pm\n";
                        //out.writeUTF(str);
                        //String time = input.readUTF();
                        String result = bookRoom(roomName, Integer.parseInt(day), Integer.parseInt(doorNum));
                        if(result.startsWith("Room"))
                        {
                            result += "Your order number is," + (orderNumber.intValue() - 1)+",";
                        }
                        result += '\n' + hint;
                        out.writeUTF(result);

                    } else if (clientIn == 4) {
                        String str = "Enter your Order number\n";
                        out.writeUTF(str);
                        String ordNum = input.readUTF();
                        String result = cancelRoom(Integer.parseInt(ordNum));
                        System.out.println(result);
                        result += hint;
                        out.writeUTF(result);
                    } else if (clientInputStr.equals("Exit")) {
                        System.out.println("Server closed...");
                        socket.close();
                        break;
                    }
                    // input.readUTF(); // read user input again.
                    flag++;
                    //
                }
            } catch (Exception e) {
                //System.out.println("服务器 run 异常: " + e.getMessage());
                e.printStackTrace();
            }
            //finally {
//                if (socket != null) {
//                    try {
//                        socket.close();
//                    } catch (Exception e) {
//                        socket = null;
//                        System.out.println("服务端 finally 异常:" + e.getMessage());
//                    }
//                }
//            }
        }
    }


    public void initRooms() {
        String record = null;        // a variable that stores the line which was read from file
        String tempRoom = null;      //Reads in the Room name from file
        String tempCap = null;       //Reads in the capacity from file

        //int recCount = 0;
        int num;
        int capacity;
        try {
            //This reads in the text from the file and uses that to create the
            //Room Objects. The name is specified first in the text file and the
            //capacity is specified last. This is manipulated in order to take in
            //these parameters when creating the Rooms.

            BufferedReader b = new BufferedReader(new FileReader("C:\\CS835\\HotelBookingSystem\\src\\Socket\\Rooms.txt"));
            while ((record = b.readLine()) != null) {
                num = (record.lastIndexOf(" ", record.length())) + 1;
                tempRoom = record.substring(0, num - 1);                 //Reads in the Room name from file

                tempCap = record.substring(num, record.length());
                capacity = Integer.parseInt(tempCap);                   //Reads in the capacity from file

                //RoomArray[recCount] = new Room(tempRoom, capacity);     //Fills the array with the created Objects.

                roomList.add(new Room(tempRoom, capacity));

            }
            b.close();    //close the input stream.

        } catch (IOException e) {
            System.out.println("Error!" + e.getMessage());
        }
    }


    public int findRoom(String str) {
        // synchronized (roomList) {
        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).name.equals(str)) {
                return i;
            }
        }
        return -1;
        //  }
    }


    public String allRooms() {
        String all = new String();
        if (!roomList.isEmpty()) {

            for (int i = 0; i < roomList.size(); i++) {
                all += roomList.get(i).name + ": \n" + "      1 2 3 4 5 \n" + roomList.get(i).getCapacity() + "\n";
            }

        }
        return all;
    }

    public String checkRoom(String str, int day, int doorNum) {
        int index;
        index = findRoom(str);
        String s;
        if (index == -1) {
            s = "Sorry, the room doesn't exist";
        } else {
            //  synchronized (roomList) {
            if (roomList.get(index).roomAvailable(day, doorNum) == true) {
                s = "Room is available";
            } else {
                s = "Room is not available";

            }
            // }
        }
        return s;

    }

    public String bookRoom(String str, int day, int doorNum) {
        int index = findRoom(str);
        String s;
        if (index == -1) {
            s = "Sorry, the room doesn't exist";
        } else {
            //  synchronized (roomList) {
            if (roomList.get(index).roomAvailable(day, doorNum) == true) {
                String combo = str + "/" + day + "/" + doorNum;
                boolean result = roomList.get(index).book(day, doorNum);
                    if(result)
                    {
                        AtomicInteger temp = new AtomicInteger(orderNumber.getAndIncrement());
                        map.put(temp, combo);
                    }

                s = "Room has been successfully booked.";
            } else {
                s = "Sorry but the Room has already been booked.";

            }
            //   }
        }

        return s;

    }

    public String cancelRoom(int ordNum) {
        String result = null;
        String temp = null;
        //temp =  map.get(ordNum);
            for (AtomicInteger orderNum : map.keySet())
            {
                if (orderNum.intValue() == ordNum)
                {
                    String info = map.get(orderNum);
                    String[] s = info.split("/");
                    String roomName = s[0];
                    String day = s[1];
                    String on = s[2];
                    int index = findRoom(roomName);
                    boolean check = roomList.get(index).cancel(Integer.parseInt(day), Integer.parseInt(on));
                    if(check)
                    {
                        result = "Room has been canceled successfully";
                        map.remove(orderNum, info);
                       // System.out.println("++++++++++++++"+map.size());
                    }else
                    {
                        result = "Room has already been canceled";
                    }
                    return result;
                }

            }

        return "Order number is not found";



    }

    public int getMapSize()
    {
        return map.size();
    }

//    public boolean checkCancel(String str, int ordNum) {
//        //int[] tempCap;
//        //int index = findRoom(str);
//        String roomName = null;
//        String temp;
//        if (!map.isEmpty())
//        //roomName = map.get(ordNum);
//        {
//            temp = map.get(ordNum);
//            String[] s = temp.split("/");
//            roomName = s[0];
//        }
//
//
//        if (roomName.equals(str)) {
////            tempCap = roomList.get(index).capacity;
//            return true;
//        } else
//            return false;
//
//
//    }

//    public void printCapacity()
//    {
//        for(int i = 0; i < roomList.size(); i++)
//        {
//            int [] temp = roomList.get(i).capacity;
//            for(int j = 0; j < temp.length; j++)
//                System.out.println("Room"+(i+1) + " capacity: "+temp[j]);
//        }
//    }

}
