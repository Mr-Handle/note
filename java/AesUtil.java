package com.handle.dubhe.encryption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author handle
 * @date 2024-12-21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AesUtil {
    // 使用AES算法
    private static final String AES = "AES";

    // GCM模式将加密操作转换为流操作，可以处理任意长度的数据，无需填充
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";

    // AES-GCM 模式通常建议使用 12 字节（96位）的 IV，因为这种长度在性能和安全性之间达到了良好的平衡
    private static final int IV_LENGTH = 12;

    // 认证标签的位长度，通常设置为128位
    private static final int GCM_TAG_LENGTH = 128;

    private static final int KEY_SIZE = 256;

    @Getter
    @Setter
    public static class AesBo {
        private String secretKey;

        private String iv;
    }

    public static AesBo getAesBO() throws NoSuchAlgorithmException {
        byte[] secretKey = generateSecretKey();
        byte[] iv = generateIV();
        AesBo encryptionBO = new AesBo();
        encryptionBO.setSecretKey(Base64.getEncoder().encodeToString(secretKey));
        encryptionBO.setIv(Base64.getEncoder().encodeToString(iv));
        return encryptionBO;
    }

    /**
     * 用指定算法生成密钥
     */
    private static byte[] generateSecretKey() throws NoSuchAlgorithmException {
        // 1.获取HmacMD5的KeyGenerator实例
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        keyGenerator.init(KEY_SIZE);

        // 2.通过KeyGenerator创建SecretKey实例
        SecretKey secretKey = keyGenerator.generateKey();

        // 3.生成随机的key字节数组，得到salt
        return secretKey.getEncoded();
    }

    private static byte[] generateIV() throws NoSuchAlgorithmException {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        secureRandom.nextBytes(iv);
        return iv;
    }

    public static String encrypt(String secretKey, String iv, String plaintext)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = getCipher(secretKey, iv, Cipher.ENCRYPT_MODE);
        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String secretKey, String iv, String cipherText)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = getCipher(secretKey, iv, Cipher.DECRYPT_MODE);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText, StandardCharsets.UTF_8);
    }

    private static Cipher getCipher(String secretKey, String iv, int mode)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] secretKeys = Base64.getDecoder().decode(secretKey);
        byte[] ivs = Base64.getDecoder().decode(iv);
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        SecretKey realSecretKey = new SecretKeySpec(secretKeys, AES);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, ivs);
        cipher.init(mode, realSecretKey, spec);
        return cipher;
    }
}