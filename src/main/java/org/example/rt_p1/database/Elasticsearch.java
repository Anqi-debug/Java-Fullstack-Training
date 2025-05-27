package org.example.rt_p1.database;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

public class Elasticsearch {
    public static void main(String[] args) {
        // Set up low-level client
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)
        ).build();

        // Create the transport with a Jackson mapper
        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);

        try {
            InfoResponse info = client.info();
            System.out.println("✅ Connected to Elasticsearch:");
            System.out.println("Cluster name: " + info.clusterName());
            System.out.println("Elasticsearch version: " + info.version().number());
        } catch (ElasticsearchException | java.io.IOException e) {
            System.out.println("❌ Failed to connect:");
            e.printStackTrace();
        }
    }
}

