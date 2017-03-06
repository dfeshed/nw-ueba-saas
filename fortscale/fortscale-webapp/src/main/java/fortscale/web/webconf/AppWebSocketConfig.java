package fortscale.web.webconf;

/**
 * Created by shays on 07/11/2016.
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class AppWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/wizard");
        config.setApplicationDestinationPrefixes("/fortscale-webapp");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/active_directory/ad_fetch_etl")
                .setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()))
                .setAllowedOrigins("*").withSockJS();
        registry.addEndpoint("/tags/user_tagging")
                .setHandshakeHandler(new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy()))
                .setAllowedOrigins("*").withSockJS();
    }
}
