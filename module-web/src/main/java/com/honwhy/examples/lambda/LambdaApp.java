package com.honwhy.examples.lambda;

import lombok.Data;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaApp {

    @Data
    static public class Person {
        private String name;
        private Integer age;
        public Person() {
            this.name = "name" + Math.random();
            this.age = (int)(Math.random() * 100);
        }
    }
    public static void main(String[] args) {
        // 快速随机生成 List<Person> 100个元素
        List<Person> persons = Stream.generate(Person::new).limit(10000).collect(Collectors.toList());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("grouping-a");
        // group by name into HashMap<String, List<Person>>
        Map<String, List<Person>> map = persons.stream().collect(Collectors.groupingBy(Person::getName));
        stopWatch.stop();
        stopWatch.start("grouping-b");
        Map<String, List<Person>> map2 = persons.stream()
                .collect(Collectors.groupingBy(Person::getName,
                        () -> new HashMap<>(256),
                        Collectors.toList()));
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        System.out.println(map.size()+map2.size());
    }
}
