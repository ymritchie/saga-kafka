package br.com.microservices.orquestrared.orderservice.core.document;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  private String id;
  private List<OrderProducts> products;
  private LocalDateTime createAt;
  private String transactionId;
  private Double totalAmount;
  private Integer totalItems;


}
