package br.com.microservices.choreography.orderservice.core.service;

import br.com.microservices.choreography.orderservice.config.exception.ValidationException;
import br.com.microservices.choreography.orderservice.core.document.Event;
import br.com.microservices.choreography.orderservice.core.dto.EventFilter;
import br.com.microservices.choreography.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private EventRepository repository;

    public Event save(Event event){
        return repository.save(event);
    }

    public void notifyEnding(Event event){
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());

        save(event);

        log.info("Pedido {} com Saga notificada! TransactionId {}", event.getOrderId(), event.getTransactionId());
    }

    public List<Event> findAll(){
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Event findByFilter(EventFilter filter){
        validateFilter(filter);

        if (StringUtils.hasLength(filter.getOrderId())){
            return findByOrderId(filter.getOrderId());
        } else {
          return  findByTransactionId(filter.getTransactionId());
        }
    }

    private Event findByOrderId(String orderId){
        return repository.findTop1ByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new ValidationException("Evento não encontrado pelo OrderId"));
    }

    private Event findByTransactionId(String transactionId){
        return repository.findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId)
                .orElseThrow(() -> new ValidationException("Evento não encontrado pelo TransactionId"));
    }

    private void validateFilter(EventFilter filter){
        if(!StringUtils.hasLength(filter.getOrderId()) && !StringUtils.hasLength(filter.getTransactionId())){
            throw new ValidationException("OrderID ou TransactionID devem estar preenchidos!");
        }
    }
}
