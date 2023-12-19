package br.com.microservices.orchestrated.orderservice.core.repository;

import br.com.microservices.orchestrated.orderservice.core.document.Event;

public interface EventRepository extends MongoRepository<Event, String> {

}
