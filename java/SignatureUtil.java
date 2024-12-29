package com.handle.dubhe.encryption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

/**
 * @author handle
 * @date 2024-12-29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignatureUtil {
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    private static final PrivateKey PRIVATE_KEY;

    private static final PublicKey PUBLIC_KEY;

    private static final Signature SIGNATURE;

    static {
        // 生成公钥/私钥对
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PRIVATE_KEY = keyPair.getPrivate();
        PUBLIC_KEY = keyPair.getPublic();

        // 生成签名实例
        try {
            SIGNATURE = Signature.getInstance(SIGNATURE_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("no such algorithm: " + SIGNATURE_ALGORITHM, e);
        }
    }

    public static String sign(String message) throws InvalidKeyException, SignatureException {
        // 用私钥签名
        SIGNATURE.initSign(PRIVATE_KEY);
        SIGNATURE.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] sign = SIGNATURE.sign();
        return Base64.getEncoder().encodeToString(sign);
    }

    public static boolean isValidSignature(String signed, String message) throws InvalidKeyException, SignatureException {
        // 用公钥验证
        SIGNATURE.initVerify(PUBLIC_KEY);
        SIGNATURE.update(message.getBytes(StandardCharsets.UTF_8));
        return SIGNATURE.verify(Base64.getDecoder().decode(signed));
    }
}