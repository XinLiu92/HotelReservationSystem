package RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AccountImpl  extends UnicastRemoteObject implements Account{
    private static final long serialVersionUID = 86425696444L;
    final long accountNumber;

    ConcurrentHashMap<String,Room> roomMap;
    private AtomicInteger orderNum;
    ConcurrentHashMap<AtomicInteger,String> orderList;
    private  ConcurrentHashMap<AtomicInteger,String> myOrderMap;

    public AccountImpl(long number, ConcurrentHashMap<String,Room> r,ConcurrentHashMap<AtomicInteger,String> orderList,AtomicInteger orderNum) throws RemoteException{
        accountNumber = number;
        //System.out.println("in account, id is  "+accountNumber);
        roomMap = r;
        this.orderList = orderList;
        this.orderNum = orderNum;
        myOrderMap = new ConcurrentHashMap<>();
    }

    public long getAccountNum() throws RemoteException{
        return accountNumber;
    }

    public int bookRoom(String room, int day,int door) throws RemoteException{

            if (roomMap.containsKey(room)){
                if (roomMap.get(room).book(day,door)){
                    AtomicInteger newOrderNum = new AtomicInteger(orderNum.incrementAndGet());
                    String info = room+"/"+day+"/"+door;
                    orderList.put(newOrderNum,info);
                    myOrderMap.put(newOrderNum,info);
                    return orderNum.intValue();
                }else {
                    System.err.println("the room cannot book for some reason");
                    return -1;
                }
            }else {
                System.err.println("cannot find this room in our system");
                return -1;
            }

    }

    public Boolean cancelRoom(int orderNumber) throws RemoteException{

        for (AtomicInteger orderNum : myOrderMap.keySet()){
            if (orderNum.intValue() == orderNumber){
                String info = orderList.get(orderNum);
                String array[]= info.split("/");
                String room =array[0];
                int day =Integer.parseInt(array[1]);
                int door = Integer.parseInt(array[2]);
                if (roomMap.containsKey(room)){
                    roomMap.get(room).cancel(day,door);
                    orderList.remove(orderNum,info);
                    myOrderMap.remove(orderNum,info);
                    return true;
                }else {
                    System.err.println("cannot find the room");
                    return false;
                }

            }
        }

        return  false;
    }

    public ConcurrentHashMap<AtomicInteger,String> getReserveInfo() throws RemoteException{

        return myOrderMap;
    }

    public Boolean checkAvailable(String room, int day, int door) throws RemoteException{

        if (roomMap.containsKey(room)){
            if (roomMap.get(room).room[day][door].intValue() == 0){
                System.out.println("this room is available");
                return true;
            }else {
                System.out.println("this room is not available");
                return false;
            }
        }else {
            System.out.println("cannot find this room");
            return false;
        }

    }
}
