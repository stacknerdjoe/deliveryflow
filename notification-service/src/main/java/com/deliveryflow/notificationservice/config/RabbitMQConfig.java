package com.deliveryflow.notificationservice.config;

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
    public Queue notificationOrderCreatedQueue() {
        return new Queue("notification.order.created", true);
    }

    @Bean
    public Queue notificationOrderAssignedQueue() {
        return new Queue("notification.order.assigned", true);
    }

    @Bean
    public Binding notificationOrderCreatedBinding() {
        return BindingBuilder.bind(notificationOrderCreatedQueue()).to(deliveryflowExchange()).with("order.created");
    }

    @Bean
    public Binding notificationOrderAssignedBinding() {
        return BindingBuilder.bind(notificationOrderAssignedQueue()).to(deliveryflowExchange()).with("order.assigned");
    }
}
