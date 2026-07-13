package com.duoc.guias.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import jakarta.annotation.PostConstruct;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.cola.guias}")
    private String colaGuias;

    @Value("${rabbitmq.cola.errores}")
    private String colaErrores;

    @Value("${rabbitmq.exchange.guias}")
    private String exchangeGuias;

    @Value("${rabbitmq.routing-key.guias}")
    private String routingKeyGuias;

    @Value("${rabbitmq.routing-key.errores}")
    private String routingKeyErrores;

    @Bean
    public Queue colaGuias() {
        return new Queue(colaGuias, true);
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

    @PostConstruct
    public void prueba() {
        System.out.println("========== RABBIT CONFIG CARGADA ==========");
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
}