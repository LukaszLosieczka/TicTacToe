package com.example.backend.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoUserPoolConfig {

    @Value("${cognito.userPoolId}")
    private String userPoolId;
    @Value("${cognito.clientId}")
    private String clientId;
    @Value("${cognito.clientSecret}")
    private String clientSecret;
    @Value("${cognito.region}")
    private String region;
    public String getUserPoolId() {
        return userPoolId;
    }
    public String getClientId() {
        return clientId;
    }
    public String getClientSecret() {
        return clientSecret;
    }
    public String getRegion() {
        return region;
    }
    @Bean
    public AWSCognitoIdentityProvider awsCognitoIdentityProvider() {
        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")))
                .withRegion(Regions.fromName(region))
                .build();
    }
}
