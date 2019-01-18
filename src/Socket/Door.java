package Socket;

import java.util.concurrent.atomic.AtomicBoolean;

public class Door {
    private boolean status;

    public Door()
    {
        status = true;
    }

    public boolean getStatus()
    {
        return status;
    }

    public void setStatus(boolean flag)
    {
        status = flag;
    }

}
