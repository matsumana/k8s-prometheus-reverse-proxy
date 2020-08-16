package info.matsumana.psystrike.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.linecorp.armeria.client.WebClient;

@SpringBootTest
public class ReverseProxyServiceTest {

    @Autowired
    Clock clock;

    @Autowired
    ReverseProxyService reverseProxyService;

    @Test
    void generatePrefix() {
        assertThat(ReverseProxyService.generatePrefix("/foo/")).isEqualTo("/foo");
        assertThat(ReverseProxyService.generatePrefix("/foo")).isEqualTo("/foo");
        assertThat(ReverseProxyService.generatePrefix("foo/")).isEqualTo("/foo");
        assertThat(ReverseProxyService.generatePrefix("foo")).isEqualTo("/foo");

        assertThat(ReverseProxyService.generatePrefix("/foo/bar/")).isEqualTo("/foo/bar");
        assertThat(ReverseProxyService.generatePrefix("/foo/bar")).isEqualTo("/foo/bar");
        assertThat(ReverseProxyService.generatePrefix("foo/bar/")).isEqualTo("/foo/bar");
        assertThat(ReverseProxyService.generatePrefix("foo/bar")).isEqualTo("/foo/bar");

        assertThat(ReverseProxyService.generatePrefix("/")).isEmpty();
        assertThat(ReverseProxyService.generatePrefix("")).isEmpty();
    }

    @Test
    void generateRequestHeaderUserAgent() {
        final String userAgent = reverseProxyService.generateRequestHeaderUserAgent();
        assertThat(userAgent).startsWith("psystrike/");
    }

    @Test
    void setupWebClientsCleanupTimer() throws InterruptedException {
        final long WEB_CLIENTS_REMOVE_TASK_DELAY_MILLIS = Duration.ofSeconds(1).toMillis();
        final long WEB_CLIENTS_REMOVE_TASK_PERIOD_MILLIS = Duration.ofSeconds(10).toMillis();
        final long WEB_CLIENTS_REMOVE_THRESHOLD_SECONDS = 30;
        final long SLEEP_BUFFER_SECONDS = Duration.ofSeconds(1).toMillis();

        final LocalDateTime now = LocalDateTime.now(clock);
        final Map<String, Pair<WebClient, LocalDateTime>> webClients = new ConcurrentHashMap<>();
        webClients.put("host1:8080", ImmutablePair.of(WebClient.of(), now.minusSeconds(10)));
        webClients.put("host2:8080", ImmutablePair.of(WebClient.of(), now.minusSeconds(20)));
        webClients.put("host3:8080", ImmutablePair.of(WebClient.of(), now.minusSeconds(30)));

        reverseProxyService.setupWebClientsCleanupTimer(webClients, clock,
                                                        WEB_CLIENTS_REMOVE_TASK_DELAY_MILLIS,
                                                        WEB_CLIENTS_REMOVE_TASK_PERIOD_MILLIS,
                                                        WEB_CLIENTS_REMOVE_THRESHOLD_SECONDS);
        assertThat(webClients.size()).isEqualTo(3);

        Thread.sleep(WEB_CLIENTS_REMOVE_TASK_DELAY_MILLIS + SLEEP_BUFFER_SECONDS);
        assertThat(webClients.size()).isEqualTo(2);

        Thread.sleep(WEB_CLIENTS_REMOVE_TASK_PERIOD_MILLIS + SLEEP_BUFFER_SECONDS);
        assertThat(webClients.size()).isEqualTo(1);

        Thread.sleep(WEB_CLIENTS_REMOVE_TASK_PERIOD_MILLIS + SLEEP_BUFFER_SECONDS);
        assertThat(webClients.size()).isEqualTo(0);
    }
}
