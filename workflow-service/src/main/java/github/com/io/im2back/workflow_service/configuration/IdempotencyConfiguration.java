package github.com.io.im2back.workflow_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.metadata.JdbcMetadataStore;
import org.springframework.integration.metadata.MetadataStore;
import github.com.io.im2back.workflow_service.amqp.dto.input.WorkflowEventPayload;
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
                    WorkflowEventPayload<?> event = (WorkflowEventPayload<?>) message.getPayload();
                    return event.eventId().toString();
                },
                metadataStore
        );
    }
}