package top.guoleishenbo.watermarkback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.guoleishenbo.watermarkback.mapper")
public class WatermarkBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatermarkBackApplication.class, args);
	}

}
