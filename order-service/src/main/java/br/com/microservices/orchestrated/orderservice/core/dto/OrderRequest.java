package br.com.microservices.orchestrated.orderservice.core.dto;


import java.util.List;

import br.com.microservices.orchestrated.orderservice.core.document.OrderProducts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructors;

@Data
@AllArgsConstructor
@NoArgsConstructors
public class OrderRequest {
	private List<OrderProducts> products;

}
