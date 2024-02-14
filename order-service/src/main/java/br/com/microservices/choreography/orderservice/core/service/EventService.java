package br.com.microservices.choreography.orderservice.core.service;

import br.com.microservices.choreography.orderservice.config.exception.ValidationException;
import br.com.microservices.choreography.orderservice.core.document.Event;
import br.com.microservices.choreography.orderservice.core.document.History;
import br.com.microservices.choreography.orderservice.core.document.Order;
import br.com.microservices.choreography.orderservice.core.dto.EventFilter;
import br.com.microservices.choreography.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.microservices.choreography.orderservice.core.enums.ESagaStatus.SUCCESS;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private static final String CURRENT_SERVICE = "ORDER_SERVICE";

    private EventRepository repository;

    public Event save(Event event){
        return repository.save(event);
    }

    public Event createEvent(Order order){
        var event = Event
                .builder()
                .source(CURRENT_SERVICE)
                .status(SUCCESS)
                .orderId(order.getId())
                .transactionId(order.getTransactionId())
                .payload(order)
                .createdAt(LocalDateTime.now())
                .build();

        addHistory(event, "Saga Started!");

        save(event);
        return event;
    }

    private void addHistory(Event event, String message){
        var history = History.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addToHistory(history);
    }

    public void notifyEnding(Event event){
        event.setSource(CURRENT_SERVICE);
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        setEndingHistory(event);
        save(event);
        log.info("Pedido {} com Saga notificada! TransactionId {}", event.getOrderId(), event.getTransactionId());
    }

    private void setEndingHistory(Event event) {
        if(SUCCESS.equals(event.getStatus())){
            log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}", event.getId());
            addHistory(event, "Saga finished successfully!");
        } else {
            log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}", event.getId());
            addHistory(event, "Saga finished with errors!");
        }
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
