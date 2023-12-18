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
public class History {

  private String source;
  private String status;
  private String message;
  private LocalDateTime createAt;

}
