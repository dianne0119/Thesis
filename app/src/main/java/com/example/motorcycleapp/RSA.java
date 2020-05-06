package com.example.motorcycleapp;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class RSA {

    private BigInteger n, d, e;

    private int bitlen = 1024;

    public RSA(BigInteger newn, BigInteger newe) {
        n = newn;
        e = newe;
    }

    /** Create an instance that can both encrypt and decrypt. */
    public RSA(int bits) {
        bitlen = bits;

        int p_1 = 163;
        int q_1 = 139;

        System.out.println("\n++++++++++++++ RSA ++++++++++++++");

        SecureRandom r = new SecureRandom();
        // BigInteger p = new BigInteger(bitlen / 2, 100, r);
        BigInteger p = BigInteger.valueOf(p_1);
        System.out.print("p: " + p);
        // BigInteger q = new BigInteger(bitlen / 2, 100, r);
        BigInteger q = BigInteger.valueOf(q_1);
        System.out.print(", q: " + q);
        n = p.multiply(q);
        System.out.print(", n: " + n);
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q
                .subtract(BigInteger.ONE));
        e = new BigInteger("79");
        while (m.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }
        System.out.print(", e: " + e);
        d = e.modInverse(m);
        System.out.println(", d: " + d);
    }

    /** Encrypt the given plaintext message. */
    public synchronized String encrypt(String message) {
        return (new BigInteger(message.getBytes())).modPow(e, n).toString();
    }

    /** Encrypt the given plaintext message. */
    public synchronized BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n);
    }

    /** Decrypt the given ciphertext message. */
    public synchronized String decrypt(String message) {
        return new String((new BigInteger(message)).modPow(d, n).toByteArray());
    }

    /** Decrypt the given ciphertext message. */
    public synchronized BigInteger decrypt(BigInteger message) {
        return message.modPow(d, n);
    }

    /** Generate a new public and private key set. */
    public synchronized void generateKeys() {
        SecureRandom r = new SecureRandom();
        BigInteger p = new BigInteger(bitlen / 2, 100, r);
        BigInteger q = new BigInteger(bitlen / 2, 100, r);
        n = p.multiply(q);
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q
                .subtract(BigInteger.ONE));
        e = new BigInteger("3");
        while (m.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }
        d = e.modInverse(m);
    }

    /** Return the modulus. */
    public synchronized BigInteger getN() {
        return n;
    }

    /** Return the public key. */
    public synchronized BigInteger getE() {
        return e;
    }

//====================== M A I N

    public static String[] Send(String text) {
        RSA rsa = new RSA(1048);

        //LENGTH OF ARRAY = 32
        String[] encrypted_aes = text.split("");
        int length = encrypted_aes.length;
        BigInteger plaintext1;
        BigInteger ciphertext1;
        String toString1;
        String[] convert = new String[length - 1];

        for (int i = length - 1; i > 0; i--) {
            plaintext1 = new BigInteger(encrypted_aes[length - i].getBytes());
            ciphertext1 = (rsa.encrypt(plaintext1));
            toString1 = ciphertext1.toString();
            convert[(length - i) - 1] = toString1 + ",";
        }

        System.out.println("CipherText [ RSA Cipher ]: " + Arrays.toString(convert));

        return convert;
    }
}
