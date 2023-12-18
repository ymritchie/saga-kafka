package br.com.microservices.orquestrared.productvalidationservice.core.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import br.com.microservices.orquestrared.productvalidationservice.core.enums.ESagaStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {

  private String source;
  private ESagaStatus status;
  private String message;
  private LocalDateTime createAt;

}
