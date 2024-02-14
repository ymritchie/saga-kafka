package br.com.microservices.choreography.paymentservice.core.service;

import br.com.microservices.choreography.paymentservice.config.exception.ValidationException;
import br.com.microservices.choreography.paymentservice.core.dto.Event;
import br.com.microservices.choreography.paymentservice.core.dto.History;
import br.com.microservices.choreography.paymentservice.core.dto.OrderProducts;
import br.com.microservices.choreography.paymentservice.core.enums.ESagaStatus;
import br.com.microservices.choreography.paymentservice.core.producer.KafkaProducer;
import br.com.microservices.choreography.paymentservice.core.saga.SagaExecutionController;
import br.com.microservices.choreography.paymentservice.core.utils.JsonUtil;
import br.com.microservices.choreography.paymentservice.core.enums.EPaymentStatus;
import br.com.microservices.choreography.paymentservice.core.model.Payment;
import br.com.microservices.choreography.paymentservice.core.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";
    private static final Double REDUCE_INITIAL_VALUE = 0.0;
    private static final Double MINIMUM_AMOUNT_VALUE = 0.1;

    private final SagaExecutionController sagaExecutionController;
    private final PaymentRepository paymentRepository;

    public void realizePayment(Event event){
        try{
            checkCurrentValidation(event);
            createPendingPayment(event);
            var payment = findByOrderIdAndTransactionId(event);
            validateAmount(payment.getTotalAmount());
            changePaymentToSuccess(payment);
            handleSuccess(event);
        } catch (Exception e){
            log.error("Erro ao fazer pagmentos: ", e);
            handleFailCurrentNotEexecuted(event, e.getMessage());
        }
        sagaExecutionController.handleSaga(event);
    }


    private void checkCurrentValidation(Event event){
        if (paymentRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())){
            throw new ValidationException("Já existe outra TranssctionID para essa validação!!");
        }
    }

    private void createPendingPayment(Event event) {
        var totalAmount = calculateTotalAmount(event);
        var totalItems = calculateTotalItems(event);
        var payment = Payment
                .builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();

        save(payment);
        setEventAmountItems(event, payment);
    }

    private double calculateTotalAmount(Event event){
        return event
                .getPayload()
                .getProducts()
                .stream()
                .map(e -> e.getQuantity() * e.getProduct().getUnitValue())
                .reduce(REDUCE_INITIAL_VALUE, Double::sum);
    }

    private int calculateTotalItems(Event event){
        return event
                .getPayload()
                .getProducts()
                .stream()
                .map(OrderProducts::getQuantity)
                .reduce(REDUCE_INITIAL_VALUE.intValue(), Integer::sum);
    }
    private void setEventAmountItems(Event event, Payment payment){
        event.getPayload().setTotalAmount(payment.getTotalAmount());
        event.getPayload().setTotalItems(payment.getTotalItems());
    }



    private void validateAmount(double amount){
        if(amount < MINIMUM_AMOUNT_VALUE) {
            throw new ValidationException("O valor mínimo permitido é de ".concat(MINIMUM_AMOUNT_VALUE.toString()));
        }
    }

    private void changePaymentToSuccess(Payment payment){
        payment.setStatus(EPaymentStatus.SUCCESS);
        save(payment);
    }

    private void handleSuccess(Event event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Pagamento realizado com sucesso!!");
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

    private void handleFailCurrentNotEexecuted(Event event, String message) {
        event.setStatus(ESagaStatus.ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Falha ao realizar pagamento: ".concat(message));
    }

    public void realizeRefund(Event event){
        event.setStatus(ESagaStatus.FAIL);
        event.setSource(CURRENT_SOURCE);

        try {
            changePaymentStatusToRefund(event);
            addHistory(event, "Rollback executado para o pagamento!!");
        } catch(Exception e) {
            addHistory(event, "Rollback não executado para o pagamento: ".concat(e.getMessage()));
        }

        sagaExecutionController.handleSaga(event);
    }

    private void changePaymentStatusToRefund(Event event){
        var payment = findByOrderIdAndTransactionId(event);
        payment.setStatus(EPaymentStatus.REFUND);
        setEventAmountItems(event, payment);
        save(payment);
    }

    private Payment findByOrderIdAndTransactionId(Event event){
        return paymentRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .orElseThrow(() -> new ValidationException("Pagamento não encontrado pelo OrderId e TransactionID!"));
    }

    private void save(Payment payment){
        paymentRepository.save(payment);
    }



}
