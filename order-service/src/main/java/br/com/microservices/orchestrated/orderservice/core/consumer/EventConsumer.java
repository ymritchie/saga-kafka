package br.com.microservices.orchestrared.orderservice.core.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrared.orderservice.core.utils.JsonUtil;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

  private final JsonUtil jsonUtil;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.notify-ending}"
  )
  public void consumerNotifyEndindEvent(String payload){
    log.info("Recevendo evento {} de finalização", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }

}
