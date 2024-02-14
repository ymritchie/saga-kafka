package br.com.microservices.choreography.orderservice.core.document;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.microservices.choreography.orderservice.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static org.springframework.util.CollectionUtils.isEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection="event")
public class Event {

  @Id
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
