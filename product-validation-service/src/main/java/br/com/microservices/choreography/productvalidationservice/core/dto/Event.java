package br.com.microservices.choreography.productvalidationservice.core.dto;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.microservices.choreography.productvalidationservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.util.CollectionUtils.isEmpty;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

  private String id;
  private String transactionId;
  private String orderId;
  private Order payload;
  private String source;
  private ESagaStatus status;
  private List<History> eventHistory;
  private LocalDateTime createdAt;

  public void addToHistory(History history){
    if (isEmpty(eventHistory)){
      eventHistory = new ArrayList<>();
    }

    eventHistory.add(history);
  }

}
