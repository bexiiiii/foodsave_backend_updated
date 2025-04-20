package foodsave.com.foodsave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Все пути
                .allowedOrigins("http://localhost:9527/")
                .allowedOrigins("http://10.201.5.95:9527")  // Адрес вашего фронтенда
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")  // Разрешенные методы
                .allowedHeaders("*")  // Разрешенные заголовки
                .allowCredentials(true);  // Разрешаем отправку cookies (если нужно)
    }
}
