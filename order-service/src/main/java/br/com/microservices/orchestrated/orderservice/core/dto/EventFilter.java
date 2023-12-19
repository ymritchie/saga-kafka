package br.com.microservices.orchestrated.orderservice.core.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructors;

@Data
@AllArgsConstructor
@NoArgsConstructors
public class EventFilter {
	
	private String orderId;
	
	private String transactionId;

}
