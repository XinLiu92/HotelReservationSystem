package RMI;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public interface Account extends java.rmi.Remote {

    int bookRoom(String room, int day, int door) throws RemoteException;

    Boolean cancelRoom(int orderNumber) throws RemoteException;

    ConcurrentHashMap<AtomicInteger,String> getReserveInfo() throws RemoteException;

    Boolean checkAvailable(String room, int day, int door) throws RemoteException;
    long getAccountNum() throws RemoteException;

}
