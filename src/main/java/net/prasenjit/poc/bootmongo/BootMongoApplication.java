package net.prasenjit.poc.bootmongo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BootMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootMongoApplication.class, args);
    }

    @Bean
    OpenAPI apiInfo1() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("New API")
                                .description("A brand new API with no content.  Go nuts!")
                                .version("1.0.0")
                )
                ;
    }
}
