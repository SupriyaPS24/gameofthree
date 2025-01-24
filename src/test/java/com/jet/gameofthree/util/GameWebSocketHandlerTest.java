package com.jet.gameofthree.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;

import static org.mockito.Mockito.mock;

@SpringBootTest
public class GameWebSocketHandlerTest {

    @Test
    void testDetermineUser() {
        GameWebSocketHandler handler = new GameWebSocketHandler();
        ServerHttpRequest mockRequest = mock(ServerHttpRequest.class);
        WebSocketHandler mockWsHandler = mock(WebSocketHandler.class);
        Map<String, Object> mockAttributes = mock(Map.class);

        StompPrincipal principal = (StompPrincipal) handler.determineUser(mockRequest, mockWsHandler, mockAttributes);
        assert principal.getName() != null;
    }


}
