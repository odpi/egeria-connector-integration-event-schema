/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the  Egeria project. */

package org.odpi.openmetadata.adapters.connectors.integration.eventschema.connection;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the connection to an instance of a Confluent (c) Schema Registry
 */
@SuppressWarnings("Var")
public class ConfluentRestCalls implements ConnectionStrategy {

    /*
     * This fields have to be set by egeria/ the atlas connector.
     * First we concentrate on the functionality.
     */
    final private String targetURL;

    public ConfluentRestCalls(String targetURL) {
        super();
        this.targetURL = targetURL;
    }

    /**
     * REST call to retrieve a list of all subjects.
     *
     * @return List of all subjects, if no subjects are listed, an empty array is returned.
     */
    @Override
    public List<String> listAllSubjects() {
        RestTemplate restTemplate;
        try {
            restTemplate = restTemplate();
        } catch (Exception e) {
            //TODO: Exception handling
            return Collections.emptyList();
        }
        HttpEntity<?> request = createHTTPTemplate();
        String urlWithQueryParameters = targetURL + "subjects/";
        ResponseEntity<String> responseEntity = restTemplate.exchange(urlWithQueryParameters, HttpMethod.GET, request, String.class);

        String jsonResponse = responseEntity.getBody();

        return convertResponseToStringArray(jsonResponse);
    }

    /**
     * REST call to retrieve the versions of a subject.
     *
     * @return List of all subjects, if no subjects are listed, an empty array is returned.
     */
    @Override
    public List<String> getVersionsOfSubject(String subject) {
        RestTemplate restTemplate;
        try {
            restTemplate = restTemplate();
        } catch (Exception e) {
            //TODO: Exception Handling
            return Collections.emptyList();
        }
        HttpEntity<?> request = createHTTPTemplate();
        String urlWithQueryParameters = targetURL + "/subjects/" + subject + "/versions/";
        ResponseEntity<String> responseEntity = restTemplate.exchange(urlWithQueryParameters, HttpMethod.GET, request, String.class);

        String jsonResponse = responseEntity.getBody();

        return convertResponseToStringArray(jsonResponse);
    }

    /**
     * REST call to retrieve the versions of a subject.
     *
     * @return List of all subjects, if no subjects are listed, an empty array is returned.
     */
    @Override
    public String getSchema(String subject, String version) {
        RestTemplate restTemplate;
        try {
            restTemplate = restTemplate();
        } catch (Exception e) {
            //TODO: Exception Handling
            return StringUtils.EMPTY;
        }
        HttpEntity<?> request = createHTTPTemplate();
        String urlWithQueryParameters = targetURL + "/subjects/" + subject + "/versions/" + version + "/schema/";
        ResponseEntity<String> responseEntity = restTemplate.exchange(urlWithQueryParameters, HttpMethod.GET, request, String.class);

        return responseEntity.getBody();
    }

    private HttpEntity<?> createHTTPTemplate() {
        // set authentication
        var authHeaders = new HttpHeaders();

//        authHeaders.setBasicAuth(confluentUserid, confluentPassword);

        return new HttpEntity<>(authHeaders);

    }

    public List<String> convertResponseToStringArray(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.length() < 2) {
            return Collections.emptyList();
        }
        jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
        jsonResponse = jsonResponse.replaceAll("\"", "");
        String[] result = jsonResponse.split(",");
        return Arrays.asList(result);
    }

    private RestTemplate restTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);

        HttpClientConnectionManager httpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder
                .create()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(requestFactory);
    }
}
