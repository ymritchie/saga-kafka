package br.com.microservices.choreography.productvalidationservice.core.repository;

import br.com.microservices.choreography.productvalidationservice.core.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepositoty extends JpaRepository<Product, Integer> {

    Boolean existsByCode(String code);
}
