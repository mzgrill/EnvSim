package de.htw.saar.env.sim;

import de.htw.saar.env.sim.io.IOManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnvSimApplication {


	public static void main(String[] args) {
		if (IOManager.getInstance().initialize(args)) {
			SpringApplication.run(EnvSimApplication.class, args);
		}

	}
}
