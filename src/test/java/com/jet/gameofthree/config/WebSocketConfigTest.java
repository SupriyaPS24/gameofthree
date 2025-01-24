package com.jet.gameofthree.config;

import com.jet.gameofthree.util.GameWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static org.mockito.Mockito.*;

@SpringBootTest
public class WebSocketConfigTest {

    private final GameWebSocketHandler gameWebSocketHandler = mock(GameWebSocketHandler.class);

    @Test
    void testConfigureMessageBroker() {
        WebSocketMessageBrokerConfigurer config = new WebSocketConfig(gameWebSocketHandler);
        MessageBrokerRegistry mockRegistry = mock(MessageBrokerRegistry.class);

        config.configureMessageBroker(mockRegistry);
        verify(mockRegistry).enableSimpleBroker("/topic", "/queue");
        verify(mockRegistry).setApplicationDestinationPrefixes("/app");
    }

}
