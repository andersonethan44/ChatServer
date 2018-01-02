/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EthanChatServer;

/**
 *
 * @author Ethan
 */
public class UserProfile
{
    private String username;
    private String password;
    public UserProfile(String username, String password){
        this.username = username;
        this.password = password;
        
    }
    @Override
    public String toString(){
        
        return username + " "+ password + System.lineSeparator();
    }
    public String getUserName(){
        
        return this.username;
    }
    public String getPassword(){
        
        return this.password;
    }
    
    
}
