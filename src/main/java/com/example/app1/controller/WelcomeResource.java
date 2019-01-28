package com.example.app1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class WelcomeResource {

    public final static String ID_HEADER = "idOperation";

    @Value("${welcome.message}")
    private String welcomeMessage;
    @Value("${api.message}")
    private String apiMessage;
    @Value("${url.app2}")
    private String url;

    private RestTemplate restTemplate;

    public WelcomeResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * http://localhost:9090/welcome
     * http://localhost:8080/spring-ldap-rest/welcome
     * @return
     */
    @GetMapping("/welcome")
    public String retrieveWelcomeMessage() {
        // Complex Method
        return welcomeMessage;
    }

    /**
     * https://www.baeldung.com/rest-template
     * https://dzone.com/articles/caching-with-apache-http-client-and-spring-resttem
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/api/v2/name")
    public ResponseEntity<String> searchProducts(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestHeader MultiValueMap<String, String> headers) throws Exception {

        long startTime = System.nanoTime();
        String apiResponseBody = "ko";
        HttpHeaders headersResponse;

        UriComponents uriComponents = UriComponentsBuilder
            .fromHttpUrl(url)
            //.queryParam("page", 0)
            //.queryParam("size", 10)
            //.queryParam("orderBy", "purchaseCount")
            .build();

        /*
        String ifModifiedSince = request.getHeader(HttpHeaders.IF_MODIFIED_SINCE);
        HttpHeaders headers = new HttpHeaders();
        if (ifModifiedSince != null) {
            headers.set(HttpHeaders.IF_MODIFIED_SINCE, ifModifiedSince);
        }
        */

        /*
        HttpHeaders httpHeaders = restTemplate
                .headForHeaders(uriComponents.toUri());
                */


        //HttpEntity<Foo> request = new HttpEntity<>(new Foo("bar"));
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        /*
        ResponseEntity<String> apiResponse = restTemplate.exchange(
                uriComponents.toUri(),
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<List<ProductDTO>>() {});
                */

        try {

            ResponseEntity<String> apiResponse = restTemplate
                    .exchange(uriComponents.toUri().toString(),
                            HttpMethod.GET, httpEntity, String.class);

            /*
            ResponseEntity<String> apiResponse
                    = restTemplate.getForEntity(uriComponents.toUri(), String.class);
                    */

            headersResponse = apiResponse.getHeaders();

            if (apiResponse.getStatusCode().equals(HttpStatus.OK)) {
                apiResponseBody = apiResponse.getBody();

                /*
                String lastModified = apiResponse.getHeaders().getFirst(HttpHeaders.LAST_MODIFIED);
                if (lastModified != null) {
                    response.setHeader(HttpHeaders.LAST_MODIFIED, lastModified);
                }
                String cacheControl = apiResponse.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL);
                if (cacheControl != null) {
                    response.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
                }
                */


            } else if (apiResponse.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {
                response.setStatus(HttpStatus.NOT_MODIFIED.value());
                return null;
            } else {
                throw new RuntimeException("Got unexpected response from product service");
            }

        } finally {
            long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//            logger.info(printResultado(obj.getMaskReqBody(), listResult.toString())
//                    + " - " + elapsedTime + " ms.");
            log.info("url: {} - elapsedTime: {} ms.", url, elapsedTime);

        }

        return ResponseEntity.ok()
                //.header(HttpHeaders.CACHE_CONTROL, CacheControl.empty().cachePublic().getHeaderValue())
                .headers(headersResponse)
                .body(apiMessage + " " + apiResponseBody);
    }


    /**
     * http://localhost:9090/api/v3/name
     * @return
     */
    @GetMapping("/api/v3/name")
    public String retrieveName(@RequestBody Map<String,Object> requestBody,
                               @RequestHeader MultiValueMap<String, String> headers) throws Exception {

        long startTime = System.nanoTime();

        //String url = "http://localhost:9091/api/v2/surname";

        try {
            String idOperation = getValue(headers, ID_HEADER);
            log.info("idOperation: {}", idOperation);

            ResponseEntity<?> response = callMicroservice(url, requestBody, headers);


        } finally {
            long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
//            logger.info(printResultado(obj.getMaskReqBody(), listResult.toString())
//                    + " - " + elapsedTime + " ms.");
            log.info("url: {} - elapsedTime: {} ms.", url, elapsedTime);

        }


        // Complex Method
        return apiMessage;
    }

    public ResponseEntity<?> callMicroservice(String url, Map<String,Object> requestBody, MultiValueMap<String, String> headers) throws Exception {

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String > result = restTemplate.exchange(
//                contextPath.get() + path,
                url,
                HttpMethod.POST, requestEntity,
                String.class);

        return result;
    }


    private String getValue(MultiValueMap<String, String> headers, String key) {
        Object obj = headers.get(key);
        if(obj!=null) {
            List<String> list = (List<String>) obj;
            return list.get(0);

        }
        return "";
    }

}
