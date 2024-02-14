package br.com.microservices.choreography.productvalidationservice.core.service;

import br.com.microservices.choreography.productvalidationservice.core.repository.ProductRepositoty;
import br.com.microservices.choreography.productvalidationservice.core.repository.ValidationRepository;
import br.com.microservices.choreography.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.choreography.productvalidationservice.core.dto.Event;
import br.com.microservices.choreography.productvalidationservice.core.dto.History;
import br.com.microservices.choreography.productvalidationservice.core.dto.OrderProducts;
import br.com.microservices.choreography.productvalidationservice.core.model.Validation;
import br.com.microservices.choreography.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.choreography.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.microservices.choreography.productvalidationservice.core.enums.ESagaStatus.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class ProductValidationService {

    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;
    private final ProductRepositoty productRepositoty;
    private final ValidationRepository validationRepository;

    public void validarProductosExistentes(Event event){
        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception e){
            log.error("Erro ao tratar de validar produtos: ", e);
            handleFailCurrentNotEexecuted(event, e.getMessage());
        }

        producer.sendEvent(jsonUtil.toJson(event));
    }



    private void validateProductsInformed(Event event){
        if (isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())){
            throw new ValidationException("Lista de produtos está vazia!!");
        }

        if (isEmpty(event.getPayload().getId()) || isEmpty(event.getPayload().getTransactionId())){
            throw new ValidationException("OrderId e TransacionId devem ser informados!!");
        }
    }

    private void checkCurrentValidation(Event event){
        validateProductsInformed(event);
        if (validationRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())){
            throw new ValidationException("Já existe outra TranssctionID para essa validação!!");
        }

        event.getPayload().getProducts().forEach(product -> {
            validateProductInformed(product);
            validateExistsProduct(product.getProduct().getCode());
        });
    }

    private void validateProductInformed(OrderProducts product){
        if (isEmpty(product.getProduct()) || isEmpty(product.getProduct().getCode())){
            throw new ValidationException("O produto deve ser informado!!");
        }
    }

    private void validateExistsProduct(String code){
        if(!productRepositoty.existsByCode(code)){
            throw new ValidationException("O produto não existe no banco de dados!!");
        }
    }

    private void handleSuccess(Event event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Produtos validados com sucesso!!");
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

    private void createValidation(Event event, boolean success) {
        var validation = Validation.builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();

        validationRepository.save(validation);
    }

    private void handleFailCurrentNotEexecuted(Event event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Falha na validação: ".concat(message));
    }

    public void rollbackEvent(Event event){
        changeValidationSuccessStatus(event, false);
        event.setStatus(FAIL);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Rollback executado para validação do produto!!");
        producer.sendEvent(jsonUtil.toJson(event));
    }

    private void changeValidationSuccessStatus(Event event, boolean success) {
        validationRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .ifPresentOrElse(validation -> {
                    validation.setSuccess(success);
                    validationRepository.save(validation);
                },
                () -> createValidation(event, false));
    }

}
