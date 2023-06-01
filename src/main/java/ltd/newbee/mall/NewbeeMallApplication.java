package ltd.newbee.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("ltd.newbee.mall.dao")
public class NewbeeMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewbeeMallApplication.class, args);
    }

    public int[] addTwoNumber(int[] numbers, Integer sum) {

        for (int i = 0; i < numbers.length; i++) {

            for (int j = i+1; j < numbers.length; j++) {

                if (numbers[i] +numbers[j] == sum) return new int[] {i,j};

            }

        }

        return new int[0];

    }

}
