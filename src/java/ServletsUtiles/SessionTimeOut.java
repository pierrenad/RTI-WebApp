/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServletsUtiles;

import EBOOP.RequeteEBOOP;
import java.net.Socket;
import java.util.StringTokenizer;
import network.*; 
import javax.servlet.http.*;

/**
 *
 * @author Pierre
 */
public class SessionTimeOut implements HttpSessionListener {

    private Socket cSock; 
    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) { System.out.println("timeout");
        HttpSession session = se.getSession(); 
        String idTrav = session.getAttribute("chosenElements").toString();
        String mess ="";
        StringTokenizer st = new StringTokenizer(idTrav, "\n"); 
        while(st.hasMoreTokens())
        {
            String temp = st.nextToken();
            StringTokenizer st2 = new StringTokenizer(temp, "#");
            st2.nextToken();
            st2.nextToken();
            st2.nextToken();
            st2.nextToken();
            mess+=st2.nextToken()+"#";
        }
        
        RequeteEBOOP req = new RequeteEBOOP(RequeteEBOOP.DELETE_CART, ""); 
        Network n = new Network(); 
        cSock = n.Init();
        n.SendRequest(cSock, req); 
        
    }    
    
}
