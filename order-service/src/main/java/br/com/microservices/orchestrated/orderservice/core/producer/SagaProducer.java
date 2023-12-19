package br.com.microservices.orchestrared.orderservice.core.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SagaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topic.start-saga}")
  private String startSagaTopic;

  public void sendEvent(String payload){
    try {
        log.info("Enviando evento para o topico {} com os dados {}", startSagaTopic, payload);
        kafkaTemplate.send(startSagaTopic, payload);
    } catch (Exception e){
      log.error("Error ao enviar para o topico {} a informação {}", startSagaTopic, payload, e);
    }
  }
}
