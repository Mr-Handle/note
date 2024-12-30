package com.handle.common.util.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * 数字证书工具类
 *
 * @author handle
 * @date 2024-12-30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CertificateUtil {
    /**
     * 从classpath读取KeyStore
     *
     * @param keyStoreFile KeyStore文件
     * @param password     KeyStore密码
     * @return KeyStore
     */
    public static KeyStore loadKeyStore(Class clazz, String keyStoreFile, String password) {
        try (InputStream input = clazz.getResourceAsStream(keyStoreFile)) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(input, password.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException("file not found in classpath: " + keyStoreFile);
        }
    }

    // 用公钥加密
    public static String encrypt(PublicKey publicKey, String plaintext) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypt = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypt);
    }

    // 用私钥解密
    public static String decrypt(PrivateKey privateKey, String ciphertext) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypt = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    // 用私钥签名
    public static String sign(PrivateKey privateKey, X509Certificate certificate, String message)
            throws GeneralSecurityException {
        Signature signature = Signature.getInstance(certificate.getSigAlgName());
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] sign = signature.sign();
        return Base64.getEncoder().encodeToString(sign);
    }

    // 验证签名
    public static boolean verify(X509Certificate certificate, String message, String sign) throws GeneralSecurityException {
        Signature signature = Signature.getInstance(certificate.getSigAlgName());
        signature.initVerify(certificate);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(sign));
    }
}