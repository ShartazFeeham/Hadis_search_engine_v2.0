package hadis.searchengine.v2;

import hadis.searchengine.v2.static_data_server.repository.StaticRepo;
import hadis.searchengine.v2.utils.FFiles;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HSEv2Application {

	public static void main(String[] args) {
		SpringApplication.run(HSEv2Application.class, args);
	}

}
