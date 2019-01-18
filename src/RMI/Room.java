package RMI;

import java.io.Serializable;

import java.util.concurrent.atomic.AtomicInteger;

class Room implements Serializable {

    private static final long serialVersionUID = 86425696443242274L;

    String name;

    public AtomicInteger[][] room;


    public Room(String n, int cap) //constructor that sets all slots to zero - unbooked
    {
        this.name = n;
        room = new AtomicInteger[8][cap+1];

        for (int i = 0; i < 8; i++){
            for (int j = 0; j < cap+1; j++){
                room[i][j] = new AtomicInteger(0);
            }
        }

    }


    public boolean book(int day,int door) {

        synchronized (room[day][door]){
            if (room[day][door].intValue() == 0){
                room[day][door].getAndIncrement();
                System.out.println("day "+day + " and door "+ door+" was booked");
                return true;
            }else {
                return false;
            }
        }

    }

    public void cancel(int day,int roomNum) {

        synchronized (room[day][roomNum]){
            room[day][roomNum].getAndDecrement();
        }
    }

    public AtomicInteger[][] getRoom(){
        return room;
    }


}