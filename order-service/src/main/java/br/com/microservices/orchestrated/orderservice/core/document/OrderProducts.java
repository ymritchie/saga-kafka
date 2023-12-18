package br.com.microservices.orquestrared.orderservice.core.document;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {

  private Product product;
  private int quantity;

}
