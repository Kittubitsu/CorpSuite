package toshu.org.corpsuite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class CorpSuiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorpSuiteApplication.class, args);
    }

}
