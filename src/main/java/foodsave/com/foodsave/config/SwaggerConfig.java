package foodsave.com.foodsave.config;


//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.info.Info;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@OpenAPIDefinition(
//        info = @Info(
//                title = "FoodSave API",
//                version = "1.0",
//                description = "API documentation for FoodSave application"
//        )
//)
//public class    SwaggerConfig {
//    // Здесь не требуется дополнительных методов, так как аннотация настроит OpenAPI
//}



import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

        @Bean
        public GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                        .group("public")
                        .pathsToMatch("/**")  // Настроить для всех путей
                        .build();
        }
}

