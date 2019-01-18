package RMI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.exit;
import static java.lang.System.setOut;


public class RoomBookingClient {

    /**
     * This is the Client Class. It takes an input from the user, calls the methods available
     * to the client from the server class and gives an ouput depending on the operation performed.
     */

    public static AtomicBoolean validChoice = new AtomicBoolean(true);
    public static RMI.RoomBookingInterface rbi;
    private  Account account;
    private  long accountNum;

    public RoomBookingClient() throws Exception {
        getRemoteObject();
    }
    public void getRemoteObject() throws Exception {

        rbi = (RoomBookingInterface) Naming.lookup("rmi://localhost:" + RoomBookingServer.port + "/mytask");

        accountNum = rbi.openAccount();
        System.out.println("in client account num is "+ accountNum);
        account = rbi.getRemoteAccount(accountNum);

    }

    public Account getAccount() {
        return account;
    }

    public void book() {
        try {

            getRemoteObject();

            while (validChoice.get() != false) {
                //A small command line interface for the user to use the system.

                System.out.println(" ");
                System.out.println("*********************Room Booking Service********************");
                System.out.println("");
                System.out.println("                   Please select a service");
                System.out.println("");
                System.out.println("1. List of all rooms.");
                System.out.println("2. Check availability of a room.");
                System.out.println("3. Book a room.");
                System.out.println("4. Cancel room reservation");
                System.out.println("5. Get my reservation information");


                System.out.println("");

                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("");
                System.out.println("Select a number between 1 and 5, 0 to exit");
                System.out.println("");
                System.out.flush();
                String response = input.readLine();

                int ipt = Integer.parseInt(response);
                if(ipt != 1 && ipt !=2 && ipt != 3 && ipt != 4 && ipt !=5 && ipt !=0 ){
                    System.out.println("invalid input");
                    continue;
                }

                try {


                    switch (ipt) {
                        case 0:
                            System.out.println("Goodbye");   //User has quit the application.
                            validChoice.compareAndSet(true, false);
                            break;

                        case 1:
                            System.out.println("");
                            System.out.println("The full list of rooms is as follows");
                            System.out.println("");
//                            System.out.println("Room |Capacity");
//                            System.out.println("-----|--------");
                            //ListOfAllRooms = rbi.allRooms();  //Run the allRooms method which
                            ConcurrentHashMap<String,Room> list = rbi.getAvailableRoom();
                            for (Map.Entry<String,Room> entry : list.entrySet()){
                                System.out.println("RoomType: "+entry.getKey());
                                System.out.println("Num 0  1  2  3  4");
                                for (int j = 1; j < entry.getValue().room.length;j++){
                                    switch (j){
                                        case 1:
                                            System.out.print("Mon ");
                                            break;

                                        case 2:
                                            System.out.print("Tue ");
                                            break;
                                        case 3:
                                            System.out.print("Wen ");
                                            break;
                                        case 4:
                                            System.out.print("Thr ");
                                            break;
                                        case 5:
                                            System.out.print("Fri ");
                                            break;
                                        case 6:
                                            System.out.print("Sat ");
                                            break;
                                        case 7:
                                            System.out.print("Sun ");
                                            break;
                                    }

                                    for (int k = 1; k < entry.getValue().room[j].length;k++){
                                        System.out.print(entry.getValue().room[j][k].intValue()+"  ");
                                    }

                                    System.out.println("");
                                }
                            }

                            System.out.println("");
                            break;

                        case 2:
                            System.out.println("");
                            System.out.println("Check a room");
                            System.out.println("Enter the room name");
                            String check_room = input.readLine();

                            System.out.println("Enter the day - ");
                            System.out.println("1=Mon , 2=Tues, 3=Wed ,4=Thurs , 5=Fri, 6=Sat, 7=Sun");
                            String check_day = input.readLine();
                            int real_day = Integer.parseInt(check_day);

                            System.out.println("Enter the door number: from 0 - 4");
                            String check_door = input.readLine();
                            int real_door = Integer.parseInt(check_door);

                            System.out.println("You are trying to book room " + check_room + " on " + real_day);

                            if (account.checkAvailable(check_room,real_day,real_door)){
                                System.out.println("this room is available");
                            }else {
                                System.out.println("this room is not available");
                            }

                            System.out.println("");

                            break;

                        case 3:
                            System.out.println("Room Booking Service - Rooms can be booked from Monday -Sunday");
                            System.out.println("");
                            System.out.println("Enter the room name");
                            String book_room = input.readLine();

                            System.out.println("");
                            System.out.println("Enter the day -");
                            System.out.println("1=Mon , 2=Tues, 3=Wed ,4=Thurs , 5=Fri, 6=Sat, 7=Sun");
                            String book_day = input.readLine();
                            int real_day2 = Integer.parseInt(book_day);


                            System.out.println("Enter the door number you want to book: from 0 - 4");
                            String book_door = input.readLine();
                            int real_door2 = Integer.parseInt(book_door);
                            int tmpOrderNum = account.bookRoom(book_room,real_day2,real_door2);
                            //This checks whether a room is available, if it is it then reserves the room.
                            if ( tmpOrderNum!= -1){
                                System.out.println("your room booked, order number is "+tmpOrderNum);
                            }else {
                                System.out.println("cannot book your room");
                            }
                            System.out.println("");
                            break;

                        case 4:
                            System.out.println("Please enter the order number to cancel the room: ");
                            String inputOrderNum = input.readLine();
                            int orderNum = Integer.parseInt(inputOrderNum);
                            if (account.cancelRoom(orderNum)){
                                System.out.println("cancelled your room");
                            }else {
                                System.out.println("sorry cannot cancel your room");
                            }
                            System.out.println("");
                            break;
                        case 5:
                            System.out.println("Pulling out your reservation information");
                            System.out.println("day note: 1=Mon , 2=Tues, 3=Wed ,4=Thurs , 5=Fri, 6=Sat, 7=Sun");
                            for (Map.Entry<AtomicInteger,String> entry : account.getReserveInfo().entrySet()){
                                System.out.print("Order number: "+entry.getKey().intValue()+", ");
                                String info = entry.getValue();
                                String array[]= info.split("/");
                                String room =array[0];
                                int day =Integer.parseInt(array[1]);
                                int door = Integer.parseInt(array[2]);

                                System.out.print(room+" day "+day+", door number "+door);
                            }
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Sorry but you have entered one of the fields incorrectly, Please try again ");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex);
        }
    }



    public void fakeClient() {


        try {

            getRemoteObject();
            //
            while (validChoice.get() != false) {
                //A small command line interface for the user to use the system.

                System.out.println(" ");
                System.out.println("*********************Room Booking Service********************");
                System.out.println("");
                System.out.println("                   Please select a service");
                System.out.println("");
                System.out.println("1. List of all rooms.");
                System.out.println("2. Check availability of a room.");
                System.out.println("3. Book a room.");
                System.out.println("4. Cancel room reservation");
                System.out.println("5. Get my reservation information");


                System.out.println("");

                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("");
                System.out.println("Select a number between 1 and 5, 0 to exit");
                System.out.println("");
                System.out.flush();

                List<Integer> list1 = new ArrayList<Integer>();
                list1.add(0);
                list1.add(1);
                list1.add(2);
                list1.add(3);
                list1.add(4);
                list1.add(5);
                int ipt = list1.get(new Random().nextInt(list1.size()));

                try {
                    switch (ipt) {
                        case 0:
                            System.out.println("Goodbye");   //User has quit the application.
                            validChoice.compareAndSet(true, false);
                            break;

                        case 1:
                            System.out.println("");
                            System.out.println("The full list of rooms is as follows");
                            System.out.println("");
//                            System.out.println("Room |Capacity");
//                            System.out.println("-----|--------");
                            //ListOfAllRooms = rbi.allRooms();  //Run the allRooms method which
                            ConcurrentHashMap<String,Room> list = rbi.getAvailableRoom();
                            for (Map.Entry<String,Room> entry : list.entrySet()){
                                System.out.println("RoomType: "+entry.getKey());
                                System.out.println("Num 0  1  2  3  4");
                                for (int j = 1; j < entry.getValue().room.length;j++){
                                    switch (j){
                                        case 1:
                                            System.out.print("Mon ");
                                            break;

                                        case 2:
                                            System.out.print("Tue ");
                                            break;
                                        case 3:
                                            System.out.print("Wen ");
                                            break;
                                        case 4:
                                            System.out.print("Thr ");
                                            break;
                                        case 5:
                                            System.out.print("Fri ");
                                            break;
                                        case 6:
                                            System.out.print("Sat ");
                                            break;
                                        case 7:
                                            System.out.print("Sun ");
                                            break;
                                    }

                                    for (int k = 1; k < entry.getValue().room[j].length;k++){
                                        System.out.print(entry.getValue().room[j][k].intValue()+"  ");
                                    }

                                    System.out.println("");
                                }
                            }

                            System.out.println("");
                            break;

                        case 2:
                            System.out.println("");
                            System.out.println("Check a room");
                            System.out.println("Enter the room name");
                            List<String> roomOpt = new ArrayList<String>();
                            roomOpt.add("Room1");
                            roomOpt.add("Room2");
                            roomOpt.add("Room3");
                            roomOpt.add("Room4");
                            String check_room = roomOpt.get(new Random().nextInt((3 - 0) + 1) + 0);

                            System.out.println("Enter the day - ");
                            System.out.println("1=Mon , 2=Tues, 3=Wed ,4=Thurs , 5=Fri, 6=Sat, 7=Sun");
//                            String check_day = input.readLine();
//                            int real_day = Integer.parseInt(check_day);

                            int[] datOpt = {1, 2, 3, 4, 5, 6,7};

                            int real_day = datOpt[new Random().nextInt(datOpt.length)];

                            System.out.println("Enter the door number: from 0 - 4");

                            int[] doorOpt = {0,1,2,3,4};
                            int real_door = datOpt[new Random().nextInt(doorOpt.length)];

                            //System.out.println("You are trying to book room " + check_room + " on " + real_day);

                            if (account.checkAvailable(check_room,real_day,real_door)){
                                System.out.println("this room is available");
                            }else {
                                System.out.println("this room is not available");
                            }

                            System.out.println("");

                            break;

                        case 3:
                            System.out.println("Room Booking Service - Rooms can be booked from Monday -Sunday");
                            System.out.println("");
                            System.out.println("Enter the room name");
                            String[] roomOpt1 = {"Room1", "Room2", "Room3", "Room4"};
                            String book_room = roomOpt1[new Random().nextInt(roomOpt1.length) ];
//                            String book_room = "Room1";

                            System.out.println("");
                            System.out.println("Enter the day -");
                            System.out.println("1=Mon , 2=Tues, 3=Wed ,4=Thurs , 5=Fri, 6=Sat, 7=Sun");
//                            String book_day = input.readLine();
//                            int real_day2 = Integer.parseInt(book_day);
                            int[] datOpt2 = {1, 2, 3, 4, 5, 6,7};
                            int real_day2 = datOpt2[new Random().nextInt(datOpt2.length)];


                            System.out.println("Enter the door number you want to book: from 0 - 4");
                            int[] doorOpt2 = {0,1,2,3,4};
                            int real_door2 = datOpt2[new Random().nextInt(doorOpt2.length) ];

                            int tmpOrderNum = account.bookRoom(book_room,real_day2,real_door2);
                            if ( tmpOrderNum!= -1){
                                System.out.println("your room booked, order number is "+tmpOrderNum);
                            }else {
                                System.out.println("cannot book your room");
                            }
                            System.out.println("");
                            break;

                        case 4:

                            System.out.println("Please enter the order number to cancel the room: ");
                            Random random    = new Random();



                            Set<AtomicInteger> keyset = account.getReserveInfo().keySet();
                            List<AtomicInteger> keys = new ArrayList(keyset);
                            int randomOrderNum;
                            if (keys.size() != 0){
                                randomOrderNum = keys.get( random.nextInt(keys.size())).intValue();
                                if (account.cancelRoom(randomOrderNum)){
                                    System.out.println("cancelled your room");
                                }else {
                                    System.out.println("sorry cannot cancel your room");
                                }
                                System.out.println("");
                            }

                            break;

                        case 5:
                            System.out.println("Pulling out your reservation information");
                            System.out.println("day note: 1=Mon , 2=Tues, 3=Wed ,4=Thurs , 5=Fri, 6=Sat, 7=Sun");
                            for (Map.Entry<AtomicInteger,String> entry : account.getReserveInfo().entrySet()){
                                System.out.print("Order number: "+entry.getKey().intValue()+", ");
                                String info = entry.getValue();
                                String array[]= info.split("/");
                                String room =array[0];
                                int day =Integer.parseInt(array[1]);
                                int door = Integer.parseInt(array[2]);

                                System.out.print(room+" day "+day+", door number "+door);
                                System.out.println("");
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Sorry but you have entered one of the fields incorrectly, Please try again ");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex);
        }
    }





    public static void main(String[] args) throws Exception{
        RoomBookingClient client = new RoomBookingClient();

        //client.fakeClient();
        client.book();

        //System.out.println("finish");

    }
}
