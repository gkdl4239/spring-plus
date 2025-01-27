package org.example.expert.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import java.io.File;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.scheme}")
    private String scheme;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.ssl.enabled}")
    private boolean sslEnabled;

    @Value("${elasticsearch.ssl.certificateAuthorities:}") // 기본값 빈 문자열
    private String caCertPath;

    @Bean
    public ElasticsearchClient elasticsearchClient() throws Exception {
        // 기본 인증 설정
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(username, password));

        // RestClient 빌더 설정
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(host, port, scheme)
            )
            .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
                    if (sslEnabled) {
                        try {
                            SSLContextBuilder sslBuilder = SSLContextBuilder.create();
                            if (caCertPath != null && !caCertPath.isEmpty()) {
                                sslBuilder.loadTrustMaterial(new File(caCertPath), null);
                            } else {
                                // 인증서 모두 허용
                                sslBuilder.loadTrustMaterial(null, (certificate, authType) -> true);
                            }
                            SSLContext sslContext = sslBuilder.build();
                            httpClientBuilder.setSSLContext(sslContext);

                            if (caCertPath == null || caCertPath.isEmpty()) {

                                httpClientBuilder.setSSLHostnameVerifier(
                                    NoopHostnameVerifier.INSTANCE);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider);
                }
            });

        // RestClient 생성
        RestClient restClient = builder.build();

        // Transport 생성 (ElasticsearchTransport 타입으로 변경)
        ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper()
        );

        // ElasticsearchClient 생성
        return new ElasticsearchClient(transport);
    }
}
