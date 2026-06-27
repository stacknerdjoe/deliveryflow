package com.deliveryflow.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queues.order-created}")
    private String orderCreatedQueue;

    @Value("${rabbitmq.queues.order-assigned}")
    private String orderAssignedQueue;

    @Value("${rabbitmq.routing-keys.order-created}")
    private String orderCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.order-assigned}")
    private String orderAssignedRoutingKey;

    @Bean
    public TopicExchange deliveryflowExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(orderCreatedQueue, true);
    }

    @Bean
    public Queue orderAssignedQueue() {
        return new Queue(orderAssignedQueue, true);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
            .bind(orderCreatedQueue())
            .to(deliveryflowExchange())
            .with(orderCreatedRoutingKey);
    }

    @Bean
    public Binding orderAssignedBinding() {
        return BindingBuilder
            .bind(orderAssignedQueue())
            .to(deliveryflowExchange())
            .with(orderAssignedRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
