package com.handle.dubhe.encryption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 口令加密工具类
 *
 * @author handle
 * @date 2024-12-21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PbeUtil {
    private static final String ALGORITHM = "PBEWithSHA1And256BitAES-CBC-BC";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String encrypt(String password, byte[] salt, String input)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = getCipher(password, salt, Cipher.ENCRYPT_MODE, bytes);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String password, byte[] salt, String input)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        byte[] encrypted = Base64.getDecoder().decode(input);
        byte[] decrypted = getCipher(password, salt, Cipher.DECRYPT_MODE, encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static byte[] getCipher(String password, byte[] salt, int mode, byte[] input)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        PBEParameterSpec pbeps = new PBEParameterSpec(salt, 1000);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey, pbeps);
        return cipher.doFinal(input);
    }
}