package br.com.microservices.orchestrated.orderservice.core.repository;

import br.com.microservices.orchestrated.orderservice.core.document.Order;

public interface OrderRepository extends MongoRepository<Order, String> {

}
