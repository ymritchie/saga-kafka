package br.com.microservices.orquestrared.productvalidationservice.core.utils;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.microservices.orquestrared.productvalidationservice.core.dto.Event;


@Component
@AllArgsConstructor
public class JsonUtil{

  private final ObjectMapper objectMapper;

  private String toJson(Object obj){
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e){
      return "";
    }
  }

  private Event toEvent(String json){
    try {
      return objectMapper.readValue(json, Event.class);
    } catch (Exception e){
      return new Event();
    }
  }
}
