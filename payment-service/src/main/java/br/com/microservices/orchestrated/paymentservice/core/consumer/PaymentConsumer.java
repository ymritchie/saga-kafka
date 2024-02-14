package br.com.microservices.orchestrated.paymentservice.core.consumer;

import br.com.microservices.orchestrated.paymentservice.core.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.microservices.orchestrated.paymentservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentConsumer {

  private final JsonUtil jsonUtil;
  private final PaymentService paymentService;

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.payment-success}"
  )
  public void consumerSuccessEvent(String payload){
    log.info("Recevendo evento {} de payment-success", payload);
    var event = jsonUtil.toEvent(payload);
    paymentService.realizePayment(event);
  }

  @KafkaListener(
    groupId = "${spring.kafka.consumer.group-id}",
    topics = "${spring.kafka.topic.payment-fail}"
  )
  public void consumerFailEvent(String payload){
    log.info("Recevendo evento {} de payment-fail", payload);
    var event = jsonUtil.toEvent(payload);
    paymentService.realizeRefund(event);
  }


}
