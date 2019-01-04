package stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTaste {
    public static void main(String[] args) {
//        List<Integer> list = Stream.of(1, 2, 3, 4, 6, 9, 10, 23)
//            .filter(n -> n < 10 && n > 2)
//            .collect(Collectors.toList());
//        System.out.println(list);

        Arrays.asList("China", "ChongQing", "Nanan district", "Huayuan Rd Street", "YiFeng Garden", "No.41 Building")
            .stream()
            .filter(s -> s.contains(" "))
            .map(String::toUpperCase)
            .sorted()
            .forEach(System.out::println);
    }
}
