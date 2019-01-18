package RMI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


class RoomBookingServer {

    static int port = 8888;
    private static RoomBookingImpl impl;

    private static Registry createRegistry() {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(port); //如果该端口未被注册，则抛异常
            registry.list(); //拿到该端口注册的rmi对象
        } catch (final Exception e) {
            try {
                registry = LocateRegistry.createRegistry(port);//捕获异常，端口注册
            } catch (final Exception ee) {
                ee.printStackTrace();
            }
        }
        return registry;
    }

    public static void bind() {
        Registry registry =  createRegistry();
        try {
            impl = new RoomBookingImpl();
            registry.rebind("mytask", impl); //这就是绑定,client里lookup必须和"mytast"一样才能远程调用impl
            System.out.println("Server is running ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start(){
        try {
            bind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RoomBookingImpl getStub(){
        return impl;
    }


    public static void  ghostClient(){
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.submit(()->{
            try {
                RoomBookingClient ghostClient = new RoomBookingClient();
                ghostClient.fakeClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        exec.shutdown();


    }
    public static void main (String[] args)
    {
        start();
        ghostClient();
    }

}