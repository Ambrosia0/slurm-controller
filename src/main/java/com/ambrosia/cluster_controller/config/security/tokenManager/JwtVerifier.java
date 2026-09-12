package com.ambrosia.cluster_controller.config.security.tokenManager;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@EnableScheduling
@Slf4j
@Component
public class JwtVerifier {
    private Map<String, KeyPair> keyMap;
    private String currentKeyId;
    
    private JWSSigner signer;
    private JWSVerifier verifier;

    public JwtVerifier() throws Exception{
        this.keyMap = new ConcurrentHashMap<>();
        this.currentKeyId = generateKeyPair();
        var keys = keyMap.get(currentKeyId);
        changeSigner(keys);
        log.info("Jwt config class initialized");
    }

    public SignedJWT signJwt(JWTClaimsSet claimsSet){
        try {
            var jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(currentKeyId).build(),
                claimsSet
            );
            jwt.sign(signer);
            return jwt;
        } catch (Exception e) {
            log.error("Caught exception while signing jwt's! {}", e);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void verify(SignedJWT signedJWT){
        signedJWT.verify(verifier);
    }

    public String generateKeyPair() throws Exception{
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        var keyPair = keyPairGenerator.generateKeyPair();
        String keyId = Base64.encode(keyPair.getPublic().getEncoded()).toString();
        keyMap.put(keyId, keyPair);
        changeSigner(keyPair);
        return keyId;
    }

    @Scheduled(cron = "0 0 0 */10 * ?")
    public void rotateKey() throws Exception{
        this.currentKeyId = generateKeyPair();
        log.info("Key rotation");
    }

    private void changeSigner(KeyPair keys){
        signer = new RSASSASigner(keys.getPrivate());
        verifier = new RSASSAVerifier((RSAPublicKey)keys.getPublic());
    }
}
