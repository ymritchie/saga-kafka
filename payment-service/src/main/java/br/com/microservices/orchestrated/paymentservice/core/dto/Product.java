package br.com.microservices.orquestrared.paymentservice.core.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  private String code;
  private String unitValue;

}
