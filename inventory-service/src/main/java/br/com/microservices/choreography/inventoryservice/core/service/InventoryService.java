package br.com.microservices.choreography.inventoryservice.core.service;

import br.com.microservices.choreography.inventoryservice.config.exception.ValidationException;
import br.com.microservices.choreography.inventoryservice.core.dto.Event;
import br.com.microservices.choreography.inventoryservice.core.dto.History;
import br.com.microservices.choreography.inventoryservice.core.dto.Order;
import br.com.microservices.choreography.inventoryservice.core.dto.OrderProducts;
import br.com.microservices.choreography.inventoryservice.core.model.Inventory;
import br.com.microservices.choreography.inventoryservice.core.repository.InventoryRepository;
import br.com.microservices.choreography.inventoryservice.core.repository.OrderInventoryRepository;
import br.com.microservices.choreography.inventoryservice.core.model.OrderInventory;
import br.com.microservices.choreography.inventoryservice.core.producer.KafkaProducer;
import br.com.microservices.choreography.inventoryservice.core.saga.SagaExecutionController;
import br.com.microservices.choreography.inventoryservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.choreography.inventoryservice.core.enums.ESagaStatus.*;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {
    private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";

    private final InventoryRepository inventoryRepository;
    private final OrderInventoryRepository orderInventoryRepository;

    private final SagaExecutionController sagaExecutionController;

    public void updateInventory(Event event){
        try {
            checkCurrentValidation(event);
            createOrderInventory(event);
            updateInventory(event.getPayload());
            handleSuccess(event);
        } catch (Exception e){
            log.error("Erro ao atualizar inventário: ", e);
            handleFailCurrentNotEexecuted(event, e.getMessage());
        }

        sagaExecutionController.handleSaga(event);
    }

    private void checkCurrentValidation(Event event){
        if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())){
            throw new ValidationException("Já existe outra TranssctionID para essa validação!!");
        }
    }

    private void createOrderInventory(Event event) {
        event
                .getPayload()
                .getProducts()
                .forEach(product -> {
                    var inventory = findInventoryByProductCode(product.getProduct().getCode());
                    var orderInventory = createOrderInventory(event, product, inventory);
                    orderInventoryRepository.save(orderInventory);
                });
    }

    private OrderInventory createOrderInventory(Event event, OrderProducts product, Inventory inventory) {
        return OrderInventory
                .builder()
                .inventory(inventory)
                .oldQuantity(inventory.getAvailable())
                .orderQuantity(product.getQuantity())
                .newQuantity(inventory.getAvailable() - product.getQuantity())
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .build();
    }

    private void updateInventory(Order order){
        order
                .getProducts()
                .forEach(product -> {
                    var inventory = findInventoryByProductCode(product.getProduct().getCode());
                    checkInventory(inventory.getAvailable(), product.getQuantity());
                    inventory.setAvailable(inventory.getAvailable() - product.getQuantity());
                    inventoryRepository.save(inventory);
                });
    }

    private void checkInventory(int available, int orderQuantity){
        if (orderQuantity > available) {
            throw  new ValidationException("Quantidade da Ordem maior que o disposnível!!");
        }
    }

    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Inventário atualizado com sucesso!!");
    }

    private void addHistory(Event event, String message){
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        event.addToHistory(history);
    }

    private void handleFailCurrentNotEexecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Falha ao atualizar inventário: ".concat(message));
    }

    public void rollbackInventory(Event event){
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);

        try {
            returnQuantityToInventory(event);
            addHistory(event, "Rollback executado para o inventário!!");
        } catch(Exception e) {
            addHistory(event, "Rollback não executado para o inventário: ".concat(e.getMessage()));
        }

        sagaExecutionController.handleSaga(event);
    }

    private void returnQuantityToInventory(Event event) {
        orderInventoryRepository.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .forEach(order -> {
                    var inventory = order.getInventory();
                    inventory.setAvailable(order.getOldQuantity());
                    inventoryRepository.save(inventory);
                    log.info("Inventário restaurado da ordem {} de {} para {}",
                            event.getPayload().getId(),
                            order.getNewQuantity(),
                            order.getOldQuantity());
                });
    }

    private Inventory findInventoryByProductCode(String productCode){
        return inventoryRepository
                .findByProductCode(productCode)
                .orElseThrow(() -> new ValidationException("Inventário não encontrado para este código de produto!!"));
    }

}
