package br.com.microservices.choreography.orderservice.core.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFilter {
	
	private String orderId;
	
	private String transactionId;

}