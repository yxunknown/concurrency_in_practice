package cha4;

public class LockTry {
    public static void main(String[] args) {
        Transfer transfer = new Transfer();
        Usex x1 = new Usex(transfer, "x1");
        Usex x2 = new Usex(transfer, "x2");
        Usex x3 = new Usex(transfer, "x3");
        Usex x4 = new Usex(transfer, "x4");
        Usex x5 = new Usex(transfer, "x5");
        Usex x6 = new Usex(transfer, "x6");

        Setx s1 = new Setx(transfer, "s1");
        Setx s2 = new Setx(transfer, "s2");
        Setx s3 = new Setx(transfer, "s3");
        Setx s4 = new Setx(transfer, "s4");
        Setx s5 = new Setx(transfer, "s5");
        Setx s6 = new Setx(transfer, "s6");
        Setx s7 = new Setx(transfer, "s7");
        Setx s8 = new Setx(transfer, "s8");
        Setx s9 = new Setx(transfer, "s9");
        Setx s10 = new Setx(transfer, "s10");
        Setx s11 = new Setx(transfer, "s11");
        Setx s12 = new Setx(transfer, "s12");

        s4.start();
        x1.start();
        x2.start();
        s1.start();
        x3.start();
        x4.start();
        s2.start();
        x5.start();
        x6.start();
        s3.start();
        s5.start();

    }

}

class Usex extends Thread {

    private Transfer transfer;
    public Usex(Transfer transfer, String name) {
        this.transfer = transfer;
        setName(name);
    }
    @Override
    public void run() {
        super.run();
        while (true) {
            synchronized (transfer) {
                transfer.use(getName());
            }
            try {
                sleep(300);
            } catch (InterruptedException e) {
                transfer.notifyAll();
            }
        }
    }
}

class Setx extends Thread {
    private Transfer transfer;
    public Setx(Transfer transfer, String name) {
        this.transfer = transfer;
        setName(name);
    }
    @Override
    public void run() {
        while (true) {
            synchronized (transfer) {
                transfer.set(getName());
                transfer.set(getName());
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                transfer.notifyAll();
            }
        }
    }
}
