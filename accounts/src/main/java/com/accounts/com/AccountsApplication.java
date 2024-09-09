package com.accounts.com;

import com.accounts.com.dto.AccountsContactInfoDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableConfigurationProperties(value = {AccountsContactInfoDto.class})
@OpenAPIDefinition(
		info = @Info(
				title = "Accounts microservice REST API Documentation",
				description = " Accounts microservice REST API Documentation",
				version = "v1"

		),
		externalDocs = @ExternalDocumentation(
				description =  " Accounts microservice REST API Documentation",
				url = "http://localhost:8096/swagger-ui/index.html"
		)
)
public class AccountsApplication {


	public static void main(String[] args) {

		SpringApplication.run(AccountsApplication.class, args);
		System.out.println("accounts application");
	}
	@Bean
	public RestTemplate ClientRestTemplate(){

		return new RestTemplate();
	}



}
