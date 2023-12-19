package br.com.microservices.orquestrared.orderservice.core.document;


import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection="order")
public class Order {

  @Id	
  private String id;
  private List<OrderProducts> products;
  private LocalDateTime createAt;
  private String transactionId;
  private Double totalAmount;
  private Integer totalItems;


}
