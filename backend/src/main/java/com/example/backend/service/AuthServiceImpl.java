package com.example.backend.service;

import com.example.backend.dto.AuthTokens;
import com.example.backend.dto.ConfirmationToken;
import com.example.backend.dto.LoginUser;
import com.example.backend.dto.RegisterUser;
import com.example.backend.exception.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
class AuthServiceImpl implements AuthService{
    @Value("${cognito.clientId}")
    private String awsClientId;

    @Value("${cognito.region}")
    private String awsRegion;

    @Override
    public AuthTokens authenticateUser(LoginUser loginUser) throws BadCredentialsException {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(awsRegion))
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

            log.info("User " + loginUser.getLogin() + " correctly authenticated");

            return new AuthTokens(accessToken, refreshToken);

        } catch (NotAuthorizedException e) {
            log.error("Password or login invalid");
            throw new BadCredentialsException("Password or login invalid");
        } catch (UserNotConfirmedException e) {
            log.error("User is not confirmed");
            throw new BadCredentialsException("User is not confirmed");
        } catch (UserNotFoundException e) {
            log.error("User not found");
            throw new BadCredentialsException("User not found");
        }
    }

    @Override
    public AuthTokens refreshTokens(String refreshToken) throws TokenExpiredException {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(awsRegion))
                .build();

        Map<String, String> authParams = new HashMap<>();
        authParams.put("REFRESH_TOKEN", refreshToken);

        InitiateAuthRequest refreshTokenRequest = InitiateAuthRequest.builder()
                .clientId(awsClientId)
                .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                .authParameters(authParams)
                .build();

        InitiateAuthResponse refreshTokenResponse = cognitoClient.initiateAuth(refreshTokenRequest);
        AuthenticationResultType authenticationResult = refreshTokenResponse.authenticationResult();

        log.info("Token refreshed correctly. Returning new token");

        String accessToken = authenticationResult.accessToken();

        return new AuthTokens(accessToken, refreshToken);
    }

    @Override
    public void registerNewUserAccount(RegisterUser userDto) {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(awsRegion))
                .build();

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .clientId(awsClientId)
                .username(userDto.getLogin())
                .password(userDto.getPassword())
                .userAttributes(
                        AttributeType.builder().name("email").value(userDto.getEmail()).build()
                )
                .build();

        try {
            cognitoClient.signUp(signUpRequest);
        }catch(UsernameExistsException ex){
            log.info(ex.getMessage());
            throw new IllegalArgumentException("Nieudana próba utworzenia konta. Konto o podanym lognie już istnieje");
        }
        catch(InvalidParameterException ex){
            log.info(ex.getMessage());
            throw new IllegalArgumentException("Nieudana próba utworzenia konta. Podano nieprawidłowe atrybuty");
        }

        log.info("User " + userDto.getLogin() + " signed up, but still needs confirmation");
    }

    @Override
    public void confirmUserAccount(ConfirmationToken token) {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(awsRegion))
                .build();

        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(awsClientId)
                .username(token.getLogin())
                .confirmationCode(token.getValue())
                .build();
        try {
            cognitoClient.confirmSignUp(confirmSignUpRequest);
        }catch(ExpiredCodeException ex){
            log.info(ex.getMessage());
            throw new IllegalArgumentException("Nieudana próba potwierdzenia konta. Podany kod stracił ważność");
        }
        catch(InvalidParameterException ex){
            log.info(ex.getMessage());
            throw new IllegalArgumentException("Nieudana próba potwierdzenia konta");
        }
        log.info("User " + token.getLogin() + " confirmed successfully");
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
