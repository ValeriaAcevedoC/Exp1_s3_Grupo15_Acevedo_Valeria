package com.duoc.guiasbff.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.cola.guias}")
    private String colaGuias;

    @Value("${rabbitmq.cola.creacion-guias}")
    private String colaCreacionGuias;

    @Value("${rabbitmq.cola.errores}")
    private String colaErrores;

    @Value("${rabbitmq.exchange.guias}")
    private String exchangeGuias;

    @Value("${rabbitmq.routing-key.guias}")
    private String routingKeyGuias;

    @Value("${rabbitmq.routing-key.creacion-guias}")
    private String routingKeyCreacionGuias;

    @Value("${rabbitmq.routing-key.errores}")
    private String routingKeyErrores;

    @Bean
    public Queue colaGuias() {
        return new Queue(colaGuias, true);
    }

    @Bean
    public Queue colaCreacionGuias() {
        return new Queue(colaCreacionGuias, true);
    }

    @Bean
    public Queue colaErrores() {
        return new Queue(colaErrores, true);
    }

    @Bean
    public DirectExchange exchangeGuias() {
        return new DirectExchange(exchangeGuias);
    }

    @Bean
    public Binding bindingGuias() {
        return BindingBuilder
                .bind(colaGuias())
                .to(exchangeGuias())
                .with(routingKeyGuias);
    }

    @Bean
    public Binding bindingCreacionGuias() {
        return BindingBuilder
                .bind(colaCreacionGuias())
                .to(exchangeGuias())
                .with(routingKeyCreacionGuias);
    }

    @Bean
    public Binding bindingErrores() {
        return BindingBuilder
                .bind(colaErrores())
                .to(exchangeGuias())
                .with(routingKeyErrores);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jsonMessageConverter) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);

        return rabbitTemplate;
    }
}
