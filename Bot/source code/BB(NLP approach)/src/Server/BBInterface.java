/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;
import java.rmi.RemoteException;
/**
 *
 * @author user
 */
public interface BBInterface {
    public String accessNormalizer(String text) throws RemoteException;
    public void accessPOSTagger(String s[]) throws RemoteException;
    public void accessParser(String s[]) throws RemoteException;
}
