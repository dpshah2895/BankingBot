/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
/**
 *
 * @author Student
 */
public class OTP {
    private byte[] OneTimePassword;
    private byte[] getHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-512");        
        byte[] passBytes = input.getBytes();
        byte[] passHash = sha.digest(passBytes);
        return passHash;    
    }
    private String getOTP() {
        final String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int n = ThreadLocalRandom.current().nextInt(10, 15);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++) {
            int pos = ThreadLocalRandom.current().nextInt(0, 62);
            sb.append(s.charAt(pos));
        }
        final String otp = sb.toString();
        try {
            OneTimePassword = getHash(otp);
        }
        catch(NoSuchAlgorithmException e) { e.printStackTrace(); }
        return otp;
    }
    public String generateOTP() {
        return getOTP();
    }
    public boolean verify(String clientInput) throws NoSuchAlgorithmException {
        byte[] client = getHash(clientInput);
        return Arrays.equals(OneTimePassword, client);
    }
    public static void main(String[] args) throws Exception {
        OTP o = new OTP();
        System.out.println(o.generateOTP());
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String x = r.readLine();
        System.out.println(o.verify(x));
    }
}
