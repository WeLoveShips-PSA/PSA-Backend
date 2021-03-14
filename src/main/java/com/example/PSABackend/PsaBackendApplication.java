package com.example.PSABackend;

import com.example.PSABackend.DAO.PortNetConnectorDAO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@SpringBootApplication
@EnableScheduling
public class PsaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PsaBackendApplication.class, args);
	}

}
