package com.handle.dubhe.encryption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author handle
 * @date 2024-12-29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RsaUtil {
    private static final String ALGORITHM = "RSA";

    private static final PrivateKey PRIVATE_KEY;

    private static final PublicKey PUBLIC_KEY;

    private static final KeyFactory KEY_FACTORY;

    static {
        try {
            KEY_FACTORY = KeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("no such algorithm: " + ALGORITHM, e);
        }

        // 生成公钥/私钥对
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("no such algorithm: " + ALGORITHM, e);
        }

        // 密钥长度/位数
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PRIVATE_KEY = keyPair.getPrivate();
        PUBLIC_KEY = keyPair.getPublic();
    }

    // 获取私钥
    public static String getPrivateKey() {
        return Base64.getEncoder().encodeToString(PRIVATE_KEY.getEncoded());
    }

    // 获取公钥
    public static String getPublicKey() {
        return Base64.getEncoder().encodeToString(PUBLIC_KEY.getEncoded());
    }

    // 恢复私钥
    public static PrivateKey restorePrivateKey(String privateKey) throws GeneralSecurityException {
        PKCS8EncodedKeySpec skSpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        return KEY_FACTORY.generatePrivate(skSpec);
    }

    // 恢复公钥
    public static PublicKey restorePublicKey(String publicKey) throws GeneralSecurityException {
        X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        return KEY_FACTORY.generatePublic(pkSpec);
    }

    // 用公钥加密
    public static String encrypt(String message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, PUBLIC_KEY);
        byte[] encrypts = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypts);
    }

    // 用私钥解密
    public static String decrypt(String message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, PRIVATE_KEY);
        byte[] decrypts = cipher.doFinal(Base64.getDecoder().decode(message));
        return new String(decrypts, StandardCharsets.UTF_8);
    }
}