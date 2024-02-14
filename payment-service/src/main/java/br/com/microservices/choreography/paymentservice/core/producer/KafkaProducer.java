package br.com.microservices.choreography.paymentservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public void sendEvent(String payload, String topic){
    try {
        log.info("Enviando evento para o topico {} com os dados {}", topic, payload);
        kafkaTemplate.send(topic, payload);
    } catch (Exception e){
      log.error("Error ao enviar para o topico {} a informação {}", topic, payload, e);
    }
  }
}
