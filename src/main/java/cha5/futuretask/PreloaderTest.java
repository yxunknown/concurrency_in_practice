package cha5.futuretask;

public class PreloaderTest {
    public static void main(String[] args) {
        Preloader preloader = new Preloader();
        preloader.start();
        try {
            while (true) {
                System.out.println(Thread.currentThread().getName() + "trying to get Production");
                // if the future task is not completed, get method will be blocked until the task completes
                // if the task is completed, will return the result or exception
                System.out.println(preloader.get());
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}
