package com.bookstore.libraryapi;

import com.bookstore.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

	@Autowired
	private EmailService emailService;

	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<String> emails = Arrays.asList("library-api-1a9547@inbox.mailtrap.io");
			emailService.sendMails("testando serviço de emails.", emails);
			System.out.println("enviou EMAIL!!!!");
		};
	}


	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}



	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
