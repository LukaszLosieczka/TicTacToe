package com.example.backend.service;

import com.example.backend.dto.AuthTokens;
import com.example.backend.dto.LoginUser;
import com.example.backend.exception.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    @Value("${cognito.clientId}")
    private String awsClientId;

    @Override
    public AuthTokens authenticateUser(LoginUser loginUser) throws BadCredentialsException {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", loginUser.getLogin());
        authParams.put("PASSWORD", loginUser.getPassword());
        //authParams.put("SECRET_HASH", calculateSecretHash());

        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .clientId(awsClientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .build();

        try {
            InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);
            AuthenticationResultType authenticationResult = authResponse.authenticationResult();
            String accessToken = authenticationResult.accessToken();
            // String idToken = authenticationResult.idToken();
            String refreshToken = authenticationResult.refreshToken();

            log.info("User correctly authenticated");

            return new AuthTokens(accessToken, refreshToken);

        } catch (NotAuthorizedException e) {
            log.error("User could not be authorized");
            throw new BadCredentialsException("User could not be authorized");
        } catch (UserNotConfirmedException e) {
            log.error("User is not confirmed");
            throw new BadCredentialsException("User is not confirmed");
        } catch (UserNotFoundException e) {
            log.error("Password or login invalid");
            throw new BadCredentialsException("Password or login invalid");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public AuthTokens refreshTokens(String refreshToken) throws TokenExpiredException {
        return null;
    }

//    private String calculateSecretHash() {
//        SecretKeySpec signingKey = new SecretKeySpec(
//                awsClientSecret.getBytes(StandardCharsets.UTF_8),
//                "HmacSHA256");
//        try {
//            Mac mac = Mac.getInstance("HmacSHA256");
//            mac.init(signingKey);
//            mac.update(awsClientName.getBytes(StandardCharsets.UTF_8));
//            byte[] rawHmac = mac.doFinal(awsClientId.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(rawHmac);
//        } catch (Exception e) {
//            throw new RuntimeException("Error while calculating ");
//        }
//    }
}
