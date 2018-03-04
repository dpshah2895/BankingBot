/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Student
 */
public class RegistrationVerification {
    private byte[] getHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-1");        
        byte[] passBytes = input.getBytes();
        byte[] passHash = sha256.digest(passBytes);
        return passHash;    
    }
    public boolean verifyDetails(String username, String email, String resphone, String country_code, String mobphone, String bank_ac) {
        try {
            String localuser = "", localemail, localresphone, localcc, localmobphone, localbankac;
            Matcher m;
            // search if the username already exists
            if(username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: User name is invalid!", "", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bankingbotdb", "root", "");
            Statement myStatement1 = con.createStatement();
            ResultSet rs1 = myStatement1.executeQuery("select * from account where UserName='" + username + "'");
            //System.out.println(username);
            if(!rs1.first()) localuser = username;
            else {
                JOptionPane.showMessageDialog(null, "Error: User name already exists!", "", JOptionPane.ERROR_MESSAGE);
                return false;
            } System.out.println(localuser);
            // validate email syntax
            if(email.isEmpty()) localemail = "";
            else {
                localemail = email;
                Pattern pat1 = Pattern.compile("^[a-z0-9]+[.]?[a-z0-9]+?[@][a-z]+([.][a-z]+)+$");
                m = pat1.matcher(localemail);
                if(!m.find()) { 
                    System.out.println("EMAIL"); 
                    return false; 
                }
            }
            // validate residential phone 
            if(resphone.isEmpty()) localresphone = "";
            else {
                localresphone = resphone;
                Pattern pat2 = Pattern.compile("^[1-9][0-9]{7}$|^[1-9][0-9]{9}$");
                m = pat2.matcher(localresphone);
                if(!m.find()) { 
                    System.out.println("RESPHONE"); 
                    return false; 
                }
            }
            // validate mobile number
            if(country_code.isEmpty()) localcc = "+91"; // default country is India
            else localcc = country_code;
            if(mobphone.isEmpty()) localmobphone = "";
            else {
                localmobphone = mobphone;
                Pattern pat3 = Pattern.compile("^[+][1-9][0-9]{1,2}$");
                Pattern pat4 = Pattern.compile("^[1-9][0-9]{9}$");
                Matcher m1 = pat3.matcher(country_code);
                m = pat4.matcher(localmobphone);
                if(!m1.find() || !m.find()) { 
                    System.out.println("CC + MOB"); 
                    return false; 
                }
            }
            // validate bank account number
            if(bank_ac.isEmpty()) localbankac = "";
            else {
                localbankac = bank_ac;
                Pattern pat5 = Pattern.compile("^[1-9][0-9]{10}$");
                m = pat5.matcher(localbankac);
                if(!m.find()) { 
                    System.out.println("BANKAC");
                    return false; 
                }
            }
            // compute hash of some details to verify correct user
            byte[] computedHash = getHash(localemail + "::" + localresphone + "::" + localcc + localmobphone + "::" + localbankac);
            // if computedHash matches any of the hashes in the database, then register succesfull
            // else display error message
            String hex = String.format("%040x", new BigInteger(1, computedHash));
            //System.out.println(hex);
            Statement myStatement = con.createStatement();
            ResultSet rs = myStatement.executeQuery("select * from accountholder"); //where RegHash='" + hex + "'");
            /*while(rs.next()) {
                System.out.println(rs.getRow());
            }*/
            if(rs.getRow() > 0) return false; // if hash does not exist
            else {
                Statement myStatement2 = con.createStatement();
                myStatement2.executeUpdate("update account set UserName='" + localuser + "' where AcNo=" + localbankac);
                //System.out.println(rs2.getFetchSize());
            }
            /*while(rs.next()) {
                System.out.println(rs.getString("FName") + " " + rs.getString("LName") + " " + rs.getString("Email"));
            }*/ 
        }
        catch(NoSuchAlgorithmException e) { e.printStackTrace(); }
        catch(Exception e) { e.printStackTrace(); }
        return true;
    }
}
