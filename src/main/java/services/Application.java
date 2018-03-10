package services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.nio.file.Paths;

@SpringBootApplication
public class Application {
    public static final String pathAvatarsFolder = Paths.get("uploads")
            .toAbsolutePath().toString() + '/';

    // метод необходимый для загрузки аватарок
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("128KB");
        factory.setMaxRequestSize("128KB");
        return factory.createMultipartConfig();
    }

    public static void main(String[] args) {
        // создадим папочку в которой будем хранить все аватарки
        File avatarFolder = new File(pathAvatarsFolder);
        if (!avatarFolder.exists()) {
            System.out.println(avatarFolder.mkdir());
        }

        SpringApplication.run(Application.class, args);
    }
}