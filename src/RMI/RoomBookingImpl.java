package RMI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RoomBookingImpl extends UnicastRemoteObject implements RoomBookingInterface {

    private static final long serialVersionUID = 6131922116577454476L;


    public ConcurrentHashMap<String,Room> RoomMap = new ConcurrentHashMap<>();

    ConcurrentHashMap<AtomicInteger,String> orderList = new ConcurrentHashMap<>();
    private AtomicInteger orderNum = new AtomicInteger(1000);
    private AtomicLong id = new AtomicLong(0);
    int capacity;
    private ConcurrentHashMap<Long, Account> totalAccount;

    RoomBookingImpl() throws RemoteException
    {
        super ();
        synchronized (this) {
            totalAccount = new ConcurrentHashMap<>();
        }
        String record = null;
        String tempRoom = null;
        String tempCap = null;

        int num;

        try
        {
            BufferedReader b = new BufferedReader(new FileReader("/Users/xinliu/Desktop/cs835/HotelSystem/HotelBookingSystem/src/RMI/Rooms.txt"));
            while((record = b.readLine()) != null)
            {
                num = (record.lastIndexOf (" ", record.length ())) + 1;
                tempRoom = record.substring (0,num -1);                 //Reads in the Room name from file

                tempCap = record.substring  (num,record.length ());
                capacity = Integer.parseInt(tempCap);                   //Reads in the capacity from file
                //RoomArray.add(new Room(tempRoom,capacity));

                RoomMap.put(tempRoom,new Room(tempRoom,capacity));
            }
            b.close();    //close the input stream.

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public long[] getAllAccounts() {

        long[] res = new long[totalAccount.size()];
        int i = 0;

        for (long id : totalAccount.keySet()) {
            res[i] = id;
            i++;
        }
        return res;
    }

    public synchronized long openAccount() throws RemoteException {

        Account account = null;
        id.incrementAndGet();
        //System.out.println("open account id is "+id.longValue());
        try {
            account = new AccountImpl(id.longValue(),RoomMap,orderList,orderNum);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        totalAccount.put(id.longValue(), account);

        return id.longValue();
    }


    public Account getRemoteAccount(long number) throws RemoteException {

        if (!totalAccount.containsKey(number)) {

            AccountImpl account = null;
            long id = number;

            try {
                account = new AccountImpl(id,RoomMap,orderList,orderNum);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            totalAccount.put(id, (Account) account);
        }

        return totalAccount.get(number);
    }


    public void closeAccount(long number)  {
        Account ra = totalAccount.get(number);

        synchronized (this) {

            try {
                totalAccount.remove(number, ra);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ConcurrentHashMap<AtomicInteger, String> getOrderList() {
        return orderList;
    }

    @Override
    public ConcurrentHashMap<String, Room> getAvailableRoom() {

        for (Map.Entry<String,Room> entry : RoomMap.entrySet()){
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
        return RoomMap;
    }

}
