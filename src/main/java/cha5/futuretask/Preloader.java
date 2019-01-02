package cha5.futuretask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Preloader {
    private final FutureTask<Production> future = new FutureTask<>(ProductionProducer::loadProduction);
    private final Thread thread = new Thread(future);

    public void start() {
        thread.start();
    }

    public Production get() throws Exception  {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw e;
        }
    }
}


class Production {

    private String name = "long time executed";

    @Override
    public String toString() {
        return "Production: { name: " + name + " }";
    }
}

class ProductionProducer {
    public static Production loadProduction() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() +
                " loading production at " +
                (i + 1) * 100 / 10.0 + "%");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return new Production();
    }
}
