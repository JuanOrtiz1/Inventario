package com.micro.inventario.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "producto")
public class ProductoProperties {

    private Retry retry = new Retry();
    private Timeout timeout = new Timeout();

    public static class Retry {
        private int maxAttempts;
        private long delay;

        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }

        public long getDelay() { return delay; }
        public void setDelay(long delay) { this.delay = delay; }
    }

    public static class Timeout {
        private long milliseconds;

        public long getMilliseconds() { return milliseconds; }
        public void setMilliseconds(long milliseconds) { this.milliseconds = milliseconds; }
    }

    public Retry getRetry() { return retry; }
    public void setRetry(Retry retry) { this.retry = retry; }

    public Timeout getTimeout() { return timeout; }
    public void setTimeout(Timeout timeout) { this.timeout = timeout; }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) timeout.getMilliseconds());
        factory.setReadTimeout((int) timeout.getMilliseconds());
        return new RestTemplate(factory);
    }
}
