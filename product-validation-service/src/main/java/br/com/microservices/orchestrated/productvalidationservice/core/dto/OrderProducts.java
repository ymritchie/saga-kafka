package br.com.microservices.orchestrared.productvalidationservice.core.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {

  private Product product;
  private int quantity;

}
