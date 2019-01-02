package cha2.counting;

public class CountingFactorizerRunner {
    public static void main(String[] args) {
        // unsafe test
        CountingFactorizer countingFactorizer = new CountingFactorizer();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    System.out.println(countingFactorizer.count());
                }
            }).start();
        }
    }
}
