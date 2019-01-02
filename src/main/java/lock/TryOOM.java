package lock;

import java.util.Date;

public class TryOOM {
    public static void main(String[] args) {
        try {
            new Date();
            new Date();
            new Date();
            new Date();
            new Date();
            new Date();
            new Date();
            new Date();
            new Date();
            throw new OutOfMemoryError("out of memory");
        } catch (OutOfMemoryError e) {
            System.out.println("OOM ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("happy ending");
        }
    }
}
