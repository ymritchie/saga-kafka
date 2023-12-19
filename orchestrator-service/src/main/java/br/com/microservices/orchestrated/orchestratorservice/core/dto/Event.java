package br.com.microservices.orchestrared.orquestratorservice.core.dto;


import java.time.LocalDateTime;
import java.util.List;

import br.com.microservices.orchestrated.orchestratorservice.core.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
