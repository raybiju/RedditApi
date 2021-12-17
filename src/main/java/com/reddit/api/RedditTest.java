package com.reddit.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RedditTest {


    private String getAuthToken(){
        RestTemplate restTemplate = new RestTemplate();
       HttpHeaders headers = new HttpHeaders();
        //Different login details as I had to re-create the app
        headers.setBasicAuth("eI086suCMPVlJkUNPLiA2A", "6-XeehZqfO6-jN9rgdfKgEaYiKm3hA");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.put("User-Agent",
                Collections.singletonList("\"Mozilla/5.0\"(by /u/Super_Consequence_83)"));
        String body = "grant_type=password&username=Super_Consequence_83&password=MrDeveloper143@b";
        HttpEntity<String> request
                = new HttpEntity<>(body, headers);
        String authUrl = "https://www.reddit.com/api/v1/access_token";
        ResponseEntity<String> response = restTemplate.postForEntity(
                authUrl, request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        try {
            map.putAll(mapper
                    .readValue(response.getBody(), new TypeReference<Map<String,Object>>(){}));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(map.get("access_token"));
    }

    public String readArticles(String subReddit) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String authToken = getAuthToken();
        headers.setBearerAuth(authToken);
        headers.put("User-Agent",
                Collections.singletonList("\"Mozilla/5.0\"(by /u/Super_Consequence_83)"));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        String url = "https://oauth.reddit.com/r/"+subReddit+"/hot";
        ResponseEntity<String> response
                = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }


    public void save()
    {
         String json=readArticles("java");
        Mongo mongo = new Mongo("localhost", 27017);
        DB db = mongo.getDB("testdb");

        DBCollection collection = db.getCollection("RedditData");

        DBObject dbObject = (DBObject) JSON.parse(json);
        collection.insert(dbObject);

    }

}
