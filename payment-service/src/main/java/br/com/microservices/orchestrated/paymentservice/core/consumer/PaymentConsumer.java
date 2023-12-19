package br.com.microservices.orchestrared.paymentservice.core.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ProductValidationConsumer {

  private final JsonUtil jsonUtil;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.payment-success}"
  )
  public void consumerSuccessEvent(String payload){
    log.info("Recevendo evento {} de payment-success", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.payment-fail}"
  )
  public void consumerFailEvent(String payload){
    log.info("Recevendo evento {} de payment-fail", payload);
    var event = jsonUtil.toEvent(payload);
    log.info(event.toString());
  }


}
