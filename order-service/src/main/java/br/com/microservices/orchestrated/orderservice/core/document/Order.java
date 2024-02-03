package br.com.microservices.orchestrated.orderservice.core.document;


import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
