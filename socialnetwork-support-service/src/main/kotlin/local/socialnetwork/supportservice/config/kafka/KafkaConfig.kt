package local.socialnetwork.supportservice.config.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import org.apache.kafka.common.serialization.StringSerializer

import org.springframework.beans.factory.annotation.Value

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer

import org.springframework.kafka.support.serializer.JsonDeserializer

import org.springframework.kafka.support.serializer.JsonSerializer

@EnableKafka
@Configuration
open class KafkaConfig {

    @Value("\${sn.kafka.server}")
    private val kafkaServer: String? = null

    @Value("\${sn.kafka.groupId}")
    private val groupId: String? = null

    @Value("\${sn.kafka.trustedPackages")
    private val trustedPackages: String? = null

    private fun producerConfig() : HashMap<String, Any> {

        val config = HashMap<String, Any>()

        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaServer!!
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        return config

    }

    @Bean
    open fun producerFactory() : ProducerFactory<String, Any> {
        return DefaultKafkaProducerFactory(producerConfig())
    }

    private fun consumerConfig() : HashMap<String, Any> {

        val props = HashMap<String, Any>()

        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaServer!!
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId!!
        props[JsonDeserializer.TRUSTED_PACKAGES] = trustedPackages!!

        return props

    }

    @Bean
    open fun consumerFactory() : ConsumerFactory<String, Any> {
        return DefaultKafkaConsumerFactory(consumerConfig())
    }

    @Bean
    open fun kafkaTemplate() : KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    open fun kafkaListenerContainerFactory() : KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Any>> {

        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()

        factory.consumerFactory = consumerFactory()
        factory.setReplyTemplate(kafkaTemplate())

        return factory

    }
}