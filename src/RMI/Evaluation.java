package RMI;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Evaluation {

    public static void main(String args[]) throws InterruptedException {
        RoomBookingServer server = new RoomBookingServer();

        Thread threadServer = new Thread(new Test.RunServer(server));

        threadServer.start();
        threadServer.join();

        int n = 100;

        List<RoomBookingClient> clientsList = new ArrayList<>();


        List<Future<RoomBookingClient>> futures = new java.util.ArrayList<>(n);
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int m = 0; m < n ; m++) {
            //int finalM = m;
            futures.add(exec.submit(() -> {
                //RoomBookingClient client = clientsList.get(finalM);
                RoomBookingClient client = new RoomBookingClient();
                String[] room = {"Room1","Room2","Room3","Room4"};
                try {
                        for (int i = 0; i < room.length;i++){
                            for (int j = 1; j < 8; j++){
                                for (int k = 0; k < 5; k++){
                                    client.getAccount().bookRoom(room[i],j,k);
                                }
                            }
                        }



                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                return client;
            }));
        }
        exec.shutdown();

        int ordersize = 0;
        for (Future<RoomBookingClient> f : futures) {

            try {
                //System.out.println("this client has order : "+ f.get());
                try {
                    ordersize += f.get().getAccount().getReserveInfo().size();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                clientsList.add(f.get());

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }




        ExecutorService execCancel = Executors.newCachedThreadPool();
        List<Future<Integer>> futures2 = new java.util.ArrayList<>(n);
        for (int m = 0; m < clientsList.size();m++){
            int finalM = m;
            futures2.add(execCancel.submit(() -> {
                RoomBookingClient client = clientsList.get(finalM);
                try {
                    ConcurrentHashMap<AtomicInteger,String> map = client.getAccount().getReserveInfo();

                    for (AtomicInteger order : map.keySet()){
                        client.getAccount().cancelRoom(order.intValue());
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return client.getAccount().getReserveInfo().size();
            }));
        }

        exec.shutdown();

        int orderLeft = 0;
        for (Future<Integer> f : futures2) {

            try {
                orderLeft += f.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }



        for (Map.Entry<AtomicInteger,String> enty:server.getStub().getOrderList().entrySet()){
            System.out.println(enty.getKey().intValue()+"  "+enty.getValue());
        }
        System.out.println(server.getStub().getOrderList().size() + " client size total order size "+ ordersize);
        System.out.println("order left is "+orderLeft);

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
