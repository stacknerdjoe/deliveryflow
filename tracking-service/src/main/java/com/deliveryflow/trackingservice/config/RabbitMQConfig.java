package com.deliveryflow.trackingservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange deliveryflowExchange() {
        return new TopicExchange("deliveryflow.exchange");
    }

    @Bean
    public Queue trackingOrderCreatedQueue() {
        return new Queue("tracking.order.created", true);
    }

    @Bean
    public Queue trackingOrderAssignedQueue() {
        return new Queue("tracking.order.assigned", true);
    }

    @Bean
    public Binding trackingOrderCreatedBinding() {
        return BindingBuilder.bind(trackingOrderCreatedQueue()).to(deliveryflowExchange()).with("order.created");
    }

    @Bean
    public Binding trackingOrderAssignedBinding() {
        return BindingBuilder.bind(trackingOrderAssignedQueue()).to(deliveryflowExchange()).with("order.assigned");
    }
}
