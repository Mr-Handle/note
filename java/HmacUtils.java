package com.handle.dubhe.encryption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * @author handle
 * @date 2024-12-21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HmacUtils {
    public static final String HMAC_SHA_512 = HmacAlgorithms.HMAC_SHA_512.getName();

    @Getter
    @Setter
    public static class EncryptionBO {
        private String secretKey;

        private String hash;
    }

    public static EncryptionBO getEncryptionBO(String input) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] secretKey = generateSecretKey();
        EncryptionBO encryptionBO = new EncryptionBO();
        encryptionBO.setSecretKey(Hex.encodeHexString(secretKey));
        encryptionBO.setHash(generateHash(secretKey, input));
        return encryptionBO;
    }

    /**
     * 用指定算法生成密钥
     */
    private static byte[] generateSecretKey() throws NoSuchAlgorithmException {
        // 1.获取HmacMD5的KeyGenerator实例
        KeyGenerator keyGenerator = KeyGenerator.getInstance(HMAC_SHA_512);

        // 2.通过KeyGenerator创建SecretKey实例
        SecretKey secretKey = keyGenerator.generateKey();

        // 3.生成随机的key字节数组，得到salt
        return secretKey.getEncoded();
    }

    /**
     * 密码核实
     */
    public static boolean verifyInput(String secretKey, String input, String storeHash)
            throws DecoderException, NoSuchAlgorithmException, InvalidKeyException {
        String inputHash = generateHash(secretKey, input);
        return Objects.equals(inputHash, storeHash);
    }

    /**
     * 用指定算法和密钥生成哈希值
     */
    private static String generateHash(String secretKey, String input)
            throws NoSuchAlgorithmException, InvalidKeyException, DecoderException {
        return generateHash(Hex.decodeHex(secretKey), input);
    }

    /**
     * 用指定算法和密钥生成哈希值
     */
    private static String generateHash(byte[] secretKeys, String input) throws NoSuchAlgorithmException, InvalidKeyException {
        // 1.恢复成SecretKey
        SecretKey secretKey = new SecretKeySpec(secretKeys, HMAC_SHA_512);

        // 2.用指定算法获取Mac实例
        Mac mac = Mac.getInstance(HMAC_SHA_512);

        // 3.用secretKey初始化mac
        mac.init(secretKey);

        // 4.对Mac实例反复调用update(byte[])输入数据
        mac.update(input.getBytes(StandardCharsets.UTF_8));

        // 5.调用Mac实例的doFinal()获取最终的哈希值
        byte[] password = mac.doFinal();

        // 6.byte[] 转为16进制字符串
        return Hex.encodeHexString(password);
    }
}