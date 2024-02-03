package br.com.microservices.orchestrated.paymentservice.core.utils;

import br.com.microservices.orchestrated.paymentservice.core.dto.Event;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JsonUtil{

  private final ObjectMapper objectMapper;

  public String toJson(Object obj){
	    try {
	      return objectMapper.writeValueAsString(obj);
	    } catch (Exception e){
	      return "";
	    }
	  }

	  public Event toEvent(String json){
	    try {
	      return objectMapper.readValue(json, Event.class);
	    } catch (Exception e){
	      return new Event();
	    }
	  }
}
