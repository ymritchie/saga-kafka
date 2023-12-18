package br.com.microservices.orquestrared.inventoryservice.core.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class SagaOrchestratorProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;


  public void sendEvent(String topic, String payload){
    try {
        log.info("Enviando evento para o topico {} com os dados {}", topic, payload);
        kafkaTemplate.send(topic, payload);
    } catch (Exception e){
      log.error("Error ao enviar para o topico {} a informação {}", topic, payload, e);
    }
  }
}
