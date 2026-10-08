package github.com.io.im2back.order_service.configuration;


import github.com.io.im2back.order_service.amqp.dto.input.OrderCommandPayload;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.metadata.JdbcMetadataStore;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.selector.MetadataStoreSelector;

import javax.sql.DataSource;

@Configuration
public class IdempotencyConfiguration {

    @Bean
    public ConcurrentMetadataStore metadataStore(DataSource dataSource) {
        return new JdbcMetadataStore(dataSource);
    }

    @Bean
    public MetadataStoreSelector metadataStoreSelector(ConcurrentMetadataStore metadataStore) {
        return new MetadataStoreSelector(
                message -> {
                    OrderCommandPayload<?> command = (OrderCommandPayload<?>) message.getPayload();
                    return command.eventId().toString();
                },
                metadataStore
        );
    }
}