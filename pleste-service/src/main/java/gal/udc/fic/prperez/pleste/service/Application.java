package gal.udc.fic.prperez.pleste.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"gal.udc.fic.prperez.pleste.model"})
@ComponentScan(basePackages = {"gal.udc.fic.prperez.pleste.service"})
@ComponentScan(basePackages = {"gal.udc.fic.prperez.pleste.model.devices.dao"})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
