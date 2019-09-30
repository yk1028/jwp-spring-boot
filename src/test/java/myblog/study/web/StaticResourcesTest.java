package myblog.study.web;

import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.CacheControl;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import support.version.BlogVersion;

import javax.rmi.CORBA.Util;
import java.util.concurrent.TimeUnit;

import static myblog.WebMvcConfig.PREFIX_STATIC_RESOURCES;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StaticResourcesTest {
    private static final Logger logger = LoggerFactory.getLogger(StaticResourcesTest.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private BlogVersion version;

    @Test
    void helloworld() {
        EntityExchangeResult<String> response = client
                .get()
                .uri("/helloworld")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .cacheControl(CacheControl.empty())
                .expectBody(String.class)
                .returnResult();

        String etag = response
                .getResponseHeaders()
                .getETag();

        assertThat(etag).isNull();
    }

    @Test
    @DisplayName("모든 정적 자원에 대해 no-cache, private 설정을 하고 테스트 코드를 통해 검증한다.")
    void get_static_resources_no_cache_private() {
        String uri = PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/images/default/bg.jpg";
        EntityExchangeResult<String> response = client
                    .get()
                    .uri(uri)
                .exchange()
                    .expectStatus()
                        .isOk()
                    .expectHeader()
                        .cacheControl(CacheControl.noCache().cachePrivate())
                    .expectBody(String.class)
                        .returnResult();

        logger.debug("body : {}", response.getResponseBody());

        String etag = response.getResponseHeaders()
                .getETag();

        client
                    .get()
                    .uri(uri)
                    .header("If-None-Match", etag)
                .exchange()
                    .expectStatus()
                        .isNotModified();
    }

    @Test
    @DisplayName("확장자가 css인 경우는 max-age를 1년")
    void get_css_resources() {
        String uri = PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/css/index.css";
        EntityExchangeResult<String> response = client
                    .get()
                    .uri(uri)
                .exchange()
                    .expectStatus()
                        .isOk()
                    .expectHeader()
                        .cacheControl(CacheControl.maxAge(31536000, TimeUnit.MILLISECONDS))
                    .expectBody(String.class)
                        .returnResult();

        logger.debug("body : {}", response.getResponseBody());

        String etag = response.getResponseHeaders()
                .getETag();

        client
                    .get()
                    .uri(uri)
                    .header("If-None-Match", etag)
                .exchange()
                    .expectStatus()
                        .isNotModified();
    }

    @Test
    @DisplayName("확장자가 js인 경우는 no-cache, private 설정을 한다.")
    void get_js_resources() {
        String uri = PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/js/index.js";
        EntityExchangeResult<String> response = client
                    .get()
                    .uri(uri)
                .exchange()
                    .expectStatus()
                        .isOk()
                    .expectHeader()
                        .cacheControl(CacheControl.noCache().cachePrivate())
                    .expectBody(String.class)
                        .returnResult();

        logger.debug("body : {}", response.getResponseBody());

        String etag = response.getResponseHeaders()
                .getETag();

        client
                    .get()
                    .uri(uri)
                    .header("If-None-Match", etag)
                .exchange()
                    .expectStatus()
                        .isNotModified();
    }

    @Test
    @DisplayName("모든 정적 자원에 대해 no-cache, no-store 설정을 한다.")
    void get_static_resources_no_cache_no_store() {
        String uri = PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/js/index.js";
        EntityExchangeResult<String> response = client
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .valueEquals(HttpHeaders.CACHE_CONTROL, "no-cache, no-store")
                .expectBody(String.class)
                .returnResult();

        logger.debug("body : {}", response.getResponseBody());

        client
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
