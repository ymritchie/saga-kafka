package br.com.microservices.orquestrared.orquestratorservice.core.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import br.com.microservices.orquestrared.orquestratorservice.core.enums.EEventSource;
import br.com.microservices.orquestrared.orquestratorservice.core.enums.ESagaStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

  private String id;
  private String transactionId;
  private String orderId;
  private Order payload;
  private EEventSource source;
  private ESagaStatus status;
  private List<History> eventHistory;
  private LocalDateTime createAt;

}