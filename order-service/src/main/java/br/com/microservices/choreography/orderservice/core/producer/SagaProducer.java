package br.com.microservices.choreography.orderservice.core.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SagaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topic.product-validation-start}")
  private String productValidationStartTopic;

  public void sendEvent(String payload){
    try {
        log.info("Enviando evento para o topico {} com os dados {}", productValidationStartTopic, payload);
        kafkaTemplate.send(productValidationStartTopic, payload);
    } catch (Exception e){
      log.error("Error ao enviar para o topico {} a informação {}", productValidationStartTopic, payload, e);
    }
  }
}
