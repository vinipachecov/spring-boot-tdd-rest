package com.bookstore.libraryapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

	@Scheduled(cron = "0 46 20 1/1 * ?")
	public void schedulingTaskTest() {
		System.out.println("AGENDAMENTO FUNCIONANDO COM SUCESSO");
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
