package RMI;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    static int ordersize=  0;
    public static void main(String args[]) throws Exception {
        RoomBookingServer server = new RoomBookingServer();

        Thread threadServer = new Thread(new RunServer(server));


        threadServer.start();
        threadServer.join();

        List<Thread> list1 = new ArrayList<>();
        List<RoomBookingClient> clientList = new ArrayList<>();



//
        for (int i = 0; i < 100; i++){
            RoomBookingClient client1 = new RoomBookingClient();
            clientList.add(client1);
            Thread thread1 = new Thread(new BookTest(client1));
            list1.add(thread1);
        }

        for (Thread t: list1){
                t.start();
        }

        for (Thread t: list1){
            t.join();
        }

//        Thread.sleep(1000);
//
//        List<Thread> list2 = new ArrayList<>();
//
//        for (int i = 0; i < 2; i++ ){
//
//            Thread thread1 = new Thread(new BookTest2(clientList.get(i)));
//            list2.add(thread1);
//        }
//
//
//        for (Thread t: list2){
//            t.start();
//        }
//        for (Thread t: list2){
//            t.join();
//        }

        for (Map.Entry<AtomicInteger,String> enty:server.getStub().getOrderList().entrySet()){
            System.out.println(enty.getKey().intValue()+"  "+enty.getValue());
        }
        System.out.println(server.getStub().getOrderList().size());


        for (RoomBookingClient r :clientList){
            System.out.println("clien id "+r.getAccount().getAccountNum());;
        }

    }

    static class BookTest implements Runnable{
        RoomBookingClient client;
        public BookTest(RoomBookingClient client){
            this.client = client;
        }
        @Override
        public void run(){
            String[] room = {"Room1","Room2","Room3","Room4"};
            try {


                    for (int i = 0; i < 1;i++){
                        for (int j = 1; j < 2; j++){
                            for (int k = 0; k < 2; k++){
                                client.getAccount().bookRoom(room[i],j,k);
                            }
                        }
                    }
                   // ordersize += client.getAccount().getReserveInfo().size();
                    //System.out.println("client order size "+client.getAccount().getReserveInfo().size());

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    static class BookTest2 implements Runnable{
        RoomBookingServer server;
        RoomBookingClient client;
        public BookTest2(RoomBookingClient client){
            this.client = client;
        }
        @Override
        public void run(){

            try {


                ConcurrentHashMap<AtomicInteger,String> map = client.getAccount().getReserveInfo();

                for (AtomicInteger order : map.keySet()){
                    //System.out.println(order);
                    client.getAccount().cancelRoom(order.intValue());
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    static class RunServer implements Runnable{
        RoomBookingServer server;

        public RunServer(RoomBookingServer server){
            this.server = server;
        }
        @Override
        public void run(){
            server.start();
        }
    }
}


