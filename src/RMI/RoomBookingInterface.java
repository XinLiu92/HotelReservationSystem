package RMI;
import java.rmi.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the interface, it contains the 5 methods which the Client can use.
 */

public interface RoomBookingInterface extends Remote
{

    long openAccount() throws RemoteException;
    Account getRemoteAccount(long number) throws RemoteException;
    ConcurrentHashMap<String,Room> getAvailableRoom() throws RemoteException;

}