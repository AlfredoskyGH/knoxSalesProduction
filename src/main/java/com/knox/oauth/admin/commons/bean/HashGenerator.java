package com.knox.oauth.admin.commons.bean;

import java.security.SecureRandom;

/**
 * Clase generadora de token temporal accesss token (olvidó contraseña)
 * @author team ssoinnova4j
 *
 */

public class HashGenerator {

    public String getToken(){
        SecureRandom random = new SecureRandom();
        long longToken = Math.abs( random.nextLong() );
        String token = Long.toString( longToken, 32 );
        return token;
    }

    public String getPasswordTemp(){
        SecureRandom random = new SecureRandom();
        long longPass = Math.abs( random.nextLong() );
        String passwordTemp = Long.toString( longPass, 32 );
        return passwordTemp;
    }

    /* Retorna un hash a partir de un tipo y un texto */
    public String getHash(String txt, String hashType) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance(hashType);
            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /* Retorna un hash MD5 a partir de un texto */
    public String md5(String txt) {
        return this.getHash(txt, "MD5");
    }

    /* Retorna un hash SHA1 a partir de un texto */
    public String sha1(String txt) {
        return this.getHash(txt, "SHA1");
    }
}
