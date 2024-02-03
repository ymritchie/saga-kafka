package br.com.microservices.orchestrated.orderservice.core.dto;


import java.util.List;

import br.com.microservices.orchestrated.orderservice.core.document.OrderProducts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
	private List<OrderProducts> products;

}
