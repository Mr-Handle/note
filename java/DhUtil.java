package com.handle.dubhe.encryption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.KeyAgreement;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author handle
 * @date 2024-12-29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DhUtil {
    private static final String ALGORITHM = "DH";

    private static final KeyFactory KEY_FACTORY;

    static {
        try {
            KEY_FACTORY = KeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("no such algorithm: " + ALGORITHM, e);
        }
    }

    // 生成公钥/私钥对
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        // 密钥长度/位数
        keyPairGenerator.initialize(512);
        return keyPairGenerator.generateKeyPair();
    }

    // 生成密钥
    public static String generateSecretKey(PrivateKey privateKey, String receivePublicKey) throws GeneralSecurityException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM);
        // 自己的PrivateKey
        keyAgreement.init(privateKey);
        // 恢复对方的PublicKey
        PublicKey publicKey = restorePublicKey(receivePublicKey);
        keyAgreement.doPhase(publicKey, true);
        // 生成SecretKey密钥:
        byte[] secretKey = keyAgreement.generateSecret();
        return Base64.getEncoder().encodeToString(secretKey);
    }

    // 恢复公钥
    private static PublicKey restorePublicKey(String publicKey) throws GeneralSecurityException {
        X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        return KEY_FACTORY.generatePublic(pkSpec);
    }
}