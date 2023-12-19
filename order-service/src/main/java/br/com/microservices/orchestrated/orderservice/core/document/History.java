package br.com.microservices.orquestrared.orderservice.core.document;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {

  private String source;
  private String status;
  private String message;
  private LocalDateTime createAt;

}
