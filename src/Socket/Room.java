package Socket;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Room {
    public String name;
    private AtomicInteger[][] allRooms;
    private int capacity;

    public Room(String n, int cap) //constructor that sets all slots to zero - unbooked
    {
        this.name = n;

        allRooms = new AtomicInteger[8][cap + 1];

        allRooms = new AtomicInteger[8][cap + 1];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < cap + 1; j++) {
                allRooms[i][j] = new AtomicInteger(0);
            }
        capacity = cap;

    }


    public boolean roomAvailable(int day, int doorNum) {
        //  Door temp = doors[day][doorNum];
        AtomicInteger temp = allRooms[day][doorNum];
        synchronized (temp) {

            if (temp.get() == 0) {
                return true;
            } else {
                return false;
            }

        }
    }

    public boolean book(int day, int doorNum) {
        AtomicInteger temp = allRooms[day][doorNum];
        synchronized (temp)
        {
            if(temp.get() == 1)
                return false;
            else
            {
                temp.compareAndSet(0, 1);
                return true;
            }

        }



    }

    public boolean cancel(int day, int doorNum) {
        AtomicInteger temp = allRooms[day][doorNum];
        synchronized (temp)
        {
            if(temp.get()== 0)
                return false;
            else
            {
                temp.compareAndSet(1, 0);
                return true;
            }

        }


    }

//    public int getCapacity() {
//        return capacity.get(0);
//    }

    public String getCapacity() {
        String s = "";
        synchronized (this) {
            for (int i = 1; i < 8; i++) {
                switch (i) {
                    case 1:
                        s += "Mon:  ";
                        break;
                    case 2:
                        s += "Tue:  ";
                        break;
                    case 3:
                        s += "Wed:  ";
                        break;
                    case 4:
                        s += "Thu:  ";
                        break;
                    case 5:
                        s += "Fri:  ";
                        break;
                    case 6:
                        s += "Sat:  ";
                        break;
                    case 7:
                        s += "Sun:  ";
                        break;
                    default:
                        s += "";
                }
                for (int j = 1; j < capacity + 1; j++) {
//                    switch (j)
//                    {
//                        case 1:
//                            s += "1 ";
//                            break;
//                        case 2:
//                            s += "2 ";
//                            break;
//                        case 3:
//                            s += "3 ";
//                            break;
//                        case 4:
//                            s += "4 ";
//                            break;
//                        case 5:
//                            s += "5 ";
//                            break;
//                        case 6:
//                            s += "6 ";
//                            break;
//                        case 7:
//                            s += "7 ";
//                            break;
//                        default:
//                            s += "";
//                    }
                    s += allRooms[i][j] + " ";
                }
                s += "\n";
            }
        }
        return s;
    }
}
