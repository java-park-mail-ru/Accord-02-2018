package ru.mail.park;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class ApplicationTest {

    @SuppressWarnings("EmptyMethod")
    @Test
    void contextLoads() {
    }
}
