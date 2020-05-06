package com.example.motorcycleapp;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.*;


public class AES {

    public static String Alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz" + "0123456789";

    public static byte[] hexStringToByteArray(String hexInputString){
        byte[] bts = new byte[hexInputString.length() / 2];

        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hexInputString.substring(2*i, 2*i+2), 16);
        }

        return bts;
    }

    public static String byteArrayToString(byte[] byteArray) {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < byteArray.length; i++) {
            str.append((char) byteArray[i]);
        }

        return str.toString();
    }

    public static String byteArrayToHexString(byte[] arg) {
        int l = arg.length * 2;
        return String.format("%0"+l+"x", new BigInteger(1, arg));
    }

    public static byte[] encrypt(byte[] key1, byte[] key2, byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2);
            SecretKeySpec skeySpec = new SecretKeySpec(key1, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/NOPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value);

            return encrypted;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(byte[] key1, byte[] key2, byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(key2);
            SecretKeySpec skeySpec = new SecretKeySpec(key1, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/NOPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(encrypted);

            return original;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String toHex(String arg) {
        int l = arg.length() * 2;

        return String.format("%0"+l+"x", new BigInteger(1, arg.getBytes()));
    }

    public static String HexStringToString (String arg) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < arg.length(); i+=2) {
            String str = arg.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String Key(String keycode) {

//        StringBuilder key = new StringBuilder(16);
//        for (int i = 0; i < 16; i++) {
//            int index = (int)(Alphanumeric.length() * Math.random());
//            key.append(Alphanumeric.charAt(index));
//        }

        String message = toHex(keycode);
//        String key1    = toHex(key.toString());
        String key1    = toHex("GImMisCN0CK3uAv7");
        String iv      = toHex("FvMmjOq5kQdelW9u");

        System.out.println("\n++++++++++++++ A E S ++++++++++++++");
        System.out.print("message (hex):         "); System.out.print(message); System.out.print("  "); System.out.print(hexToAscii(message)); System.out.println("  "); //System.out.println(hexStringToByteArray(message));
        System.out.print("key (hex):             "); System.out.print(key1); System.out.print("  "); System.out.print(hexToAscii(key1)); System.out.println("    "); //System.out.println(hexStringToByteArray(key1));
        System.out.print("iv (hex):              "); System.out.print(iv); System.out.print("  "); System.out.print(hexToAscii(iv)); System.out.println("  "); //System.out.println(hexStringToByteArray(iv));
        System.out.println();

        byte[] enc_message_ba = encrypt(hexStringToByteArray(key1), hexStringToByteArray(iv), hexStringToByteArray(message));
        System.out.print("Encrypted (hex):       "); System.out.print(byteArrayToHexString(enc_message_ba)); System.out.print("  "); System.out.println(byteArrayToString(enc_message_ba));
        System.out.println();

//        return (key1+byteArrayToHexString(enc_message_ba));
        return (byteArrayToHexString(enc_message_ba));
    }
}
