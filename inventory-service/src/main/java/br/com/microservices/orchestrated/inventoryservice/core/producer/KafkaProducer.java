package br.com.microservices.orquestrared.inventoryservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topic.orchestrator}")
  private String orchestratorTopic;

  public void sendEvent(String payload){
    try {
        log.info("Enviando evento para o topico {} com os dados {}", orchestratorTopic, payload);
        kafkaTemplate.send(orchestratorTopic, payload);
    } catch (Exception e){
      log.error("Error ao enviar para o topico {} a informação {}", orchestratorTopic, payload, e);
    }
  }
}
