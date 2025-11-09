package conexa.starwarschallenge;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StarwarschallengeApplication {

	public static void main(String[] args) {
		Dotenv.configure().directory("./").ignoreIfMissing().load();
		SpringApplication.run(StarwarschallengeApplication.class, args);
	}

}
