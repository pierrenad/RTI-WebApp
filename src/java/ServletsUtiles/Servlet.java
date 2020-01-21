/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServletsUtiles;

import CHECKCARP.ReponseProtocolCard;
import CHECKCARP.RequeteProtocolCard;
import CHECKINAP.ReponseProtocol;
import CHECKINAP.RequeteProtocol;
import EBOOP.ReponseEBOOP;
import EBOOP.RequeteEBOOP;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import network.Network;

/**
 *
 * @author Pierre
 */
public class Servlet extends HttpServlet {

    private int nbreConnexions = 0; 
    private ObjectInputStream ois;
    private Socket cSock; 
    ArrayList<String> cartElements = new ArrayList<>(); 
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            ServletContext sc = getServletContext();
            String action = request.getParameter("action");

            if(action.equals("Authentication")) // client have a client number 
            { 
                setAuthentificationInProgress(request, true);
                MemberDataCenter mdc = new MemberDataCenter(request.getParameter("numCli")); // creating a member with the number client 
                HttpSession session = request.getSession(true); // creating session 
                session.setAttribute("member", mdc); // link member to the session 
                
                RequeteProtocol req = new RequeteProtocol(RequeteProtocol.LOGIN_WEB, request.getParameter("numCli"));

                Network n = new Network();
                cSock = n.Init(); 
                n.SendRequest(cSock, req); // ask for connection with the client number 

                ReponseProtocol rep = null;
                try {
                    ois = new ObjectInputStream(cSock.getInputStream()); 
                    rep = (ReponseProtocol)ois.readObject();
                    System.out.println("<Authentication> *** Reponse reçue");

                    if(rep.getCode() == ReponseProtocol.CLIENT_FOUND) { // client does exist
                        StringTokenizer st = new StringTokenizer(rep.getCharge(), "#");
                        String numcli = st.nextToken(); 
                        String name = st.nextToken(); 
                        String frstName = st.nextToken(); 
                        String adr = st.nextToken(); 
                        String mail = st.nextToken(); 
                        String country = st.nextToken(); 

                        mdc.setNom(name);
                        mdc.setPrenom(frstName);
                        mdc.setAdresse(adr); 
                        mdc.setPays(country); 
                        mdc.setEMail(mail);
                        mdc.setNouveau(false); 
                        session.setAttribute("member", mdc); // on a toutes les infos du client 

                        synchronized (this) { nbreConnexions++; } 
                        setAuthenticated(request, true);
                        
                        RequeteEBOOP req2 = new RequeteEBOOP(RequeteEBOOP.CROSSING, ""); 
                        Network n2 = new Network();
                        cSock = n2.Init(); 
                        n2.SendRequest(cSock, req2); // ask for all the crossings 

                        ReponseEBOOP rep2 = null;
                        try {
                            ois = new ObjectInputStream(cSock.getInputStream()); 
                            rep2 = (ReponseEBOOP)ois.readObject();
                            System.out.println("<Authentication> *** Reponse reçue");

                            if (rep2.getCode() == ReponseEBOOP.LISTE_OK) {
                                session.setAttribute("crossingList", rep2.getCharge()); // add list crossings to session 
                                session.setAttribute("problemBuy", ""); // problemBuy will be needed later 
                                session.setAttribute("boutonstring", "");
                                request.setAttribute("session", session); // send session to ChooseBooking 
                            }
                            cartElements.clear(); // we clear the chosen crossings 
                            RequestDispatcher rd = sc.getRequestDispatcher("/ChooseBooking.jsp");
                            rd.forward(request, response);
                        }
                        catch(Exception e) {
                            System.out.println("<Authentication> " + e.getMessage());
                        }
                    }
                    else { 
                        if(request.getParameter("numCli").equals("")) { // if nothing is write 
                            out.println("<p>No client number<p>"); 
                            out.println("<p>Please register to continue or retry with a client number<p>"); 
                        }
                        else {  // doesn't find client 
                            out.println("<p>Hello, this is the first time you connect !<p>"); 
                            out.println("<p>Please register to continue<p>"); 
                            out.println("<p>Client not found <p>");
                        }
                        out.println("<p><a href=\"/RTI_Partie3/NewClient.jsp\">Register</a> <p>"); 
                        out.println("<p><a href=\"/RTI_Partie3/Login.jsp\">Retry Login</a> <p>");
                    }
                }
                catch (Exception e) { 
                    System.err.println("<Authentication> " + e.getMessage()); 
                }
            } 
            else if (action.equals("Sign up")) // client don't have client number and want to sign up 
            { 
                setAuthentificationInProgress(request, true);
                MemberDataCenter mdc = new MemberDataCenter(); // creating a member 
                HttpSession session = request.getSession(true); // creating session 
                session.setAttribute("member", mdc); // link member to the session 
                session.setAttribute("problemRegister", ""); // will be needed later 
                request.setAttribute("session", session); // send session 
                RequestDispatcher rd = sc.getRequestDispatcher("/NewClient.jsp");
                rd.forward(request, response);
            }
            else if (action.equals("Register")) // confirm the registration 
            {
                if(isAuthentificationInProgress(request)) { 
                    if("".equals(request.getParameter("name")) || "".equals(request.getParameter("firstName")) || "".equals(request.getParameter("address")) || "".equals(request.getParameter("email")) || "".equals(request.getParameter("country"))) {
                        // if one of the information is missing  
                        HttpSession session = request.getSession(true); // creating session 
                        session.setAttribute("member", session.getAttribute("member")); // link member to the session 
                        session.setAttribute("problemRegister", "<h4>Please enter all information</h4>"); // message to show in NewClient 
                        request.setAttribute("session", session); // send session 
                        RequestDispatcher rd = sc.getRequestDispatcher("/NewClient.jsp");
                        rd.forward(request, response);
                    }
                    else {
                        RequeteEBOOP req = new RequeteEBOOP(RequeteEBOOP.REGISTER, request.getParameter("name").concat("#" + request.getParameter("firstName")  
                                + "#" + request.getParameter("address") + "#" + request.getParameter("email") + "#" + request.getParameter("country"))); 

                        Network n = new Network();
                        cSock = n.Init(); 
                        n.SendRequest(cSock, req); // ask to register in the db 
                        
                        ReponseEBOOP rep = null;
                        try {
                            ois = new ObjectInputStream(cSock.getInputStream()); 
                            rep = (ReponseEBOOP)ois.readObject();
                            System.out.println("<Register> *** Reponse reçue");
                            
                            String numCli = rep.getCharge(); 
                            if(rep.getCode() == ReponseEBOOP.AJOUT_OK) { // if client is well registered 
                                HttpSession session = request.getSession(true);
                                MemberDataCenter mdc = (MemberDataCenter)session.getAttribute("member"); // get the attribute member back 
                                mdc.setNumero(numCli); // add number client to the client in the session 
                                mdc.setNom(request.getParameter("name")); // add name to client 
                                mdc.setPrenom(request.getParameter("firstName")); // add first name to client 
                                mdc.setAdresse(request.getParameter("address")); // add address to client 
                                mdc.setEMail(request.getParameter("email")); // add mail to client 
                                mdc.setPays(request.getParameter("country")); // add country to client 
                                session.setAttribute("member", mdc); // add parameters to member and relink to the session 
                                session.setAttribute("problemRegister", ""); // we clear the message not to show it next time directly 
                                
                                synchronized (this) { nbreConnexions++; }
                                setAuthenticated(request, true);
                            }
                        }
                        catch(Exception e) {
                            System.out.println("<Register> " + e.getMessage());
                            out.println("<p>Connection to HappyFerryDB not established<p>"); 
                        }
                    }

                    if(isAuthenticated(request)) {
                        RequeteEBOOP req2 = new RequeteEBOOP(RequeteEBOOP.CROSSING, ""); 
                        Network n2 = new Network();
                        cSock = n2.Init(); 
                        n2.SendRequest(cSock, req2); // ask for all the crossings 

                        ReponseEBOOP rep2 = null;
                        try {
                            ois = new ObjectInputStream(cSock.getInputStream()); 
                            rep2 = (ReponseEBOOP)ois.readObject();
                            System.out.println("<Register> *** Reponse reçue");

                            if (rep2.getCode() == ReponseEBOOP.LISTE_OK) { // we get the crossings 
                                HttpSession session = request.getSession(true); 
                                session.setAttribute("crossingList", rep2.getCharge()); // add crossing list to session  
                                session.setAttribute("problemBuy", ""); // needed later 
                                session.setAttribute("boutonstring", "");
                                request.setAttribute("session", session); // sending session with request to ChooseBooking 
                            }
                            cartElements.clear();
                            RequestDispatcher rd = sc.getRequestDispatcher("/ChooseBooking.jsp");
                            rd.forward(request, response);
                        }
                        catch(Exception e) {
                            System.out.println("<Register> " + e.getMessage());
                        }
                    }
                }
            }
            else if(action.equals("Add to cart")) // ask to add element to the cart 
            { 
                if(isAuthenticated(request)) {
                    HttpSession session = request.getSession(true); // creating session 
                    MemberDataCenter mdc = (MemberDataCenter)session.getAttribute("member"); // get the client 

                    String test = request.getParameter("Id");System.out.println("test char 0 : " + test.charAt(0)); 
                    if("-".equals(test.charAt(0)))
                    {
                        RequeteEBOOP req = new RequeteEBOOP(RequeteEBOOP.REMOVE_FROM_CART, request.getParameter("Id")); 
                        Network n = new Network();
                        cSock = n.Init(); 
                        n.SendRequest(cSock, req); // ask to add chosen crossing to cart

                        ReponseEBOOP rep = null;
                        try {
                            ois = new ObjectInputStream(cSock.getInputStream()); 
                            rep = (ReponseEBOOP)ois.readObject();
                            System.out.println("<Add To Cart> *** Reponse reçue");

                            String StrBouton="";
                            if (rep.getCode() == ReponseEBOOP.REMOVE_OK) { 
                                cartElements.add(request.getParameter("Id")); // add chosen crossing to a list of chosen crossing (if we choose some others) 
                                for(int i=0; i<cartElements.size()-1;i++)
                                {
                                    StrBouton += cartElements.get(i)+"#"; //YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
                                }
                                session.setAttribute("boutonstring", StrBouton);
                                session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                                session.setAttribute("member", mdc);

                                session.setAttribute("problemBuy", ""); // nothing to say
                                request.setAttribute("session", session); // sending session with request to ChooseBooking 
                                RequestDispatcher rd = sc.getRequestDispatcher("/ChooseBooking.jsp"); 
                                rd.forward(request, response);
                            }
                        }
                        catch(Exception e) {
                            System.out.println("<Add To Cart> " + e.getMessage());
                        }
                    }
                    else
                    {
                        RequeteEBOOP req = new RequeteEBOOP(RequeteEBOOP.ADD_CART, request.getParameter("Id")); 
                        Network n = new Network();
                        cSock = n.Init(); 
                        n.SendRequest(cSock, req); // ask to add chosen crossing to cart 

                        ReponseEBOOP rep = null;
                        try {
                            ois = new ObjectInputStream(cSock.getInputStream()); 
                            rep = (ReponseEBOOP)ois.readObject();
                            System.out.println("<Add To Cart> *** Reponse reçue");

                            String StrBouton="";
                            if (rep.getCode() == ReponseEBOOP.ADD_CART_OK) { 
                                cartElements.add(request.getParameter("Id")); // add chosen crossing to a list of chosen crossing (if we choose some others) 
                                for(int i=0; i<cartElements.size()-1;i++)
                                {
                                    StrBouton += cartElements.get(i)+"#"; //YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
                                }
                                session.setAttribute("boutonstring", StrBouton);
                                session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                                session.setAttribute("member", mdc);

                                session.setAttribute("problemBuy", ""); // nothing to say
                                request.setAttribute("session", session); // sending session with request to ChooseBooking 
                                RequestDispatcher rd = sc.getRequestDispatcher("/ChooseBooking.jsp"); 
                                rd.forward(request, response);
                            }
                            else if (rep.getCode() == ReponseEBOOP.ADD_CART_PAS_OK) {
                                for(int i=0; i<cartElements.size()-1;i++)
                                {
                                    StrBouton += cartElements.get(i)+"#";
                                }
                                session.setAttribute("boutonstring", StrBouton); //YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
                                session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                                session.setAttribute("member", mdc);
                                session.setAttribute("problemBuy", "<h4>Add didn't work</h4>"); // message to show in ChooseBooking 
                                request.setAttribute("session", session); // sending session with request to ChooseBooking 
                                RequestDispatcher rd = sc.getRequestDispatcher("/ChooseBooking.jsp"); 
                                rd.forward(request, response);
                            }
                        }
                        catch(Exception e) {
                            System.out.println("<Add To Cart> " + e.getMessage());
                        }
                    }
                }
            }
            /*else if(action.equals("Add to cart")) // ask to add element to the cart 
            { 
                if(isAuthenticated(request)) {
                    HttpSession session = request.getSession(true); // creating session 
                    MemberDataCenter mdc = (MemberDataCenter)session.getAttribute("member"); // get the client 

                    RequeteEBOOP req = new RequeteEBOOP(RequeteEBOOP.ADD_CART, request.getParameter("Id")); 
                    Network n = new Network();
                    cSock = n.Init(); 
                    n.SendRequest(cSock, req); // ask to add chosen crossing to cart 

                    ReponseEBOOP rep = null;
                    try {
                        ois = new ObjectInputStream(cSock.getInputStream()); 
                        rep = (ReponseEBOOP)ois.readObject();
                        System.out.println("<Add To Cart> *** Reponse reçue");

                        if (rep.getCode() == ReponseEBOOP.ADD_CART_OK) { 
                            cartElements.add(request.getParameter("Id")); // add chosen crossing to a list of chosen crossing (if we choose some others) 
                            session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                            session.setAttribute("member", mdc);
                            session.setAttribute("problemBuy", ""); // nothing to say  
                            request.setAttribute("session", session); // sending session with request to ChooseBooking 
                            RequestDispatcher rd = sc.getRequestDispatcher("/ChooseBooking.jsp"); 
                            rd.forward(request, response);
                        }
                        else if (rep.getCode() == ReponseEBOOP.ADD_CART_PAS_OK) {
                            session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                            session.setAttribute("member", mdc);
                            session.setAttribute("problemBuy", "<h4>Add didn't work</h4>"); // message to show in ChooseBooking 
                            request.setAttribute("session", session); // sending session with request to ChooseBooking 
                            RequestDispatcher rd = sc.getRequestDispatcher("/ChooseBooking.jsp"); 
                            rd.forward(request, response);
                        }
                    }
                    catch(Exception e) {
                        System.out.println("<Add To Cart> " + e.getMessage());
                    }
                }
            }*/
            else if (action.equals("Buy")) // ask to see the cart 
            { 
                if(isAuthenticated(request)) {
                    if(cartElements.isEmpty()) { // if no element chosen 
                        HttpSession session = request.getSession(true); // creating session 
                        MemberDataCenter mdc = (MemberDataCenter)session.getAttribute("member");
                        session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                        session.setAttribute("member", mdc);
                        session.setAttribute("problemBuy", "<h4>You have nothing in your cart</h4>"); // message to show in ChooseBooking 
                        request.setAttribute("session", session); // sending session with request to ChooseBooking 
                        RequestDispatcher rd = sc.getRequestDispatcher("/ChooseBooking.jsp"); 
                        rd.forward(request, response);
                    }
                    else { 
                        HttpSession session = request.getSession(true); // creating session 
                        MemberDataCenter mdc = (MemberDataCenter)session.getAttribute("member");
                        session.setAttribute("problemBuy", ""); // nothing to say and clear the message 
                        String mess = ""; 
                        for(int i=0; i<cartElements.size(); i++) {
                            mess += cartElements.get(i)+"#"; // get all the chosen crossings 
                        }
                        
                        RequeteEBOOP req = new RequeteEBOOP(RequeteEBOOP.VIEW_CART, mess); 
                        Network n = new Network();
                        cSock = n.Init(); 
                        n.SendRequest(cSock, req); // ask crossings details (from, to, etc) 

                        ReponseEBOOP rep = null;
                        try {
                            ois = new ObjectInputStream(cSock.getInputStream()); 
                            rep = (ReponseEBOOP)ois.readObject();
                            System.out.println("<Buy> *** Reponse reçue");

                            if (rep.getCode() == ReponseEBOOP.VIEW_CART_OK) {
                                String message = rep.getCharge(); // get the details 

                                session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                                session.setAttribute("member", mdc);
                                session.setAttribute("chosenElements", message); // send details to Cart 
                                session.setAttribute("problemConfirm", ""); // needed later  
                                request.setAttribute("session", session); // sending session with request to ChooseBooking 
                                RequestDispatcher rd = sc.getRequestDispatcher("/Cart.jsp"); 
                                rd.forward(request, response);
                            }
                        }
                        catch(Exception e) {
                            System.out.println("<Buy> " + e.getMessage());
                        }
                    }
                }
            }
            else if (action.equals("Confirm")) // ask to see the cart 
            { 
                if(isAuthenticated(request)) {
                    if(request.getParameter("matricule").equals("") || request.getParameter("nbpass").equals("")) {
                        // if one of the information is missing  
                        HttpSession session = request.getSession(true); // creating session 
                        session.setAttribute("member", session.getAttribute("member")); // link member to the session 
                        session.setAttribute("chosenElements", session.getAttribute("chosenElements")); // send details to Cart 
                        session.setAttribute("problemConfirm", "<h4>Please enter all information</h4>"); // message to show in NewClient 
                        request.setAttribute("session", session); // send session 
                        RequestDispatcher rd = sc.getRequestDispatcher("/Cart.jsp");
                        rd.forward(request, response);
                    }
                    else { 
                        HttpSession session = request.getSession(true); // creating session 
                        MemberDataCenter mdc = (MemberDataCenter)session.getAttribute("member");
                        String mess = ""; 

                        float prixtot = Float.parseFloat(request.getParameter("prixtot"));
                        prixtot *= Integer.parseInt(request.getParameter("nbpass"));

                        mess += prixtot + "#";
                        mess += request.getParameter("matricule");

                        try {
                            session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                            session.setAttribute("member", mdc);
                            session.setAttribute("PrixEtMatricule", mess); // send price and matricule 
                            session.setAttribute("problemConfirm", ""); // clear message 
                            session.setAttribute("problemFinalise", ""); // needed later  
                            request.setAttribute("session", session); // sending session with request to Buy 
                            RequestDispatcher rd = sc.getRequestDispatcher("/Buy.jsp"); 
                            rd.forward(request, response);
                        }
                        catch(Exception e) {
                            System.out.println("<Confirm> " + e.getMessage());
                        }
                    }
                }
            }
            else if (action.equals("Finalise")) // ask to see the cart 
            { 
                if(isAuthenticated(request)) {
                    if(request.getParameter("num").equals("") || request.getParameter("code").equals("")) { 
                        HttpSession session = request.getSession(true); // creating session 
                        MemberDataCenter mdc = (MemberDataCenter)session.getAttribute("member");
                        session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                        session.setAttribute("member", mdc);
                        session.setAttribute("PrixEtMatricule", session.getAttribute("PrixEtMatricule")); 
                        session.setAttribute("problemFinalise", "<h4>Please enter all information</h4>"); // message to show in Buy 
                        request.setAttribute("session", session); // sending session with request to Buy 
                        RequestDispatcher rd = sc.getRequestDispatcher("/Buy.jsp"); 
                        rd.forward(request, response);
                    }
                    else { 
                        HttpSession session = request.getSession(true); // creating session 
                        MemberDataCenter mdc = (MemberDataCenter)session.getAttribute("member");
                        String mess = ""; 
                        mess+=request.getParameter("num")+"#";
                        mess+=request.getParameter("code")+"#"; 
                        mess+=request.getParameter("date")+"#";               
                        mess+=request.getParameter("prixtot")+"#";
                        
                        /*RequeteEBOOP req = new RequeteEBOOP(RequeteEBOOP.BUY_CART, mess); 
                        Network n = new Network();
                        cSock = n.Init(); 
                        n.SendRequest(cSock, req); // ask to purchase for crossing(s) */
                        RequeteProtocolCard req3 = new RequeteProtocolCard(RequeteProtocolCard.CHECK_CARD2, mess); 
                        Network n3 = new Network();
                        cSock = n3.InitOnDemand(); 
                        n3.SendRequest(cSock, req3); // ask crossings details (from, to, etc) 
                        
                        ReponseProtocolCard rep3 = null; 
                        //ReponseEBOOP rep = null;
                        try {
                            /*ois = new ObjectInputStream(cSock.getInputStream()); 
                            rep = (ReponseEBOOP)ois.readObject(); 
                            System.out.println("<Finalise> *** Reponse reçue");*/
                            ois = new ObjectInputStream(cSock.getInputStream()); 
                            rep3 = (ReponseProtocolCard)ois.readObject();
                            System.out.println("<Buy> *** Reponse reçue");

                            if (rep3.getCode() == ReponseProtocolCard.CARTEPASOK){ //(rep.getCode() == ReponseEBOOP.BUY_CART_BAD_INFO) {
                                String message = rep3.getChargeUtile(); // get message to send 

                                session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                                session.setAttribute("member", mdc);
                                session.setAttribute("problemFinalise", ""); // clear message 
                                session.setAttribute("idRes", "");
                                session.setAttribute("result", message); // send message received with rep 
                                request.setAttribute("session", session); // sending session with request to Result 
                                RequestDispatcher rd = sc.getRequestDispatcher("/Result.jsp"); 
                                rd.forward(request, response);
                            }
                            if (rep3.getCode() == ReponseProtocolCard.CARTEOK){ //(rep.getCode() == ReponseEBOOP.BUY_CART_OK) {
                                String message = rep3.getChargeUtile(); 
                                String mess2 = ""; 

                                String elem = session.getAttribute("chosenElements").toString(); // get the liste of chosen crossing 
                                MemberDataCenter md = (MemberDataCenter)session.getAttribute("member"); // get client 
                                String mat = session.getAttribute("PrixEtMatricule").toString(); // get price and matricule 
                                
                                StringTokenizer s = new StringTokenizer(mat, "#"); 
                                s.nextToken(); // don't need price here 
                                mat = s.nextToken(); // get the matricule 
                                //System.out.println("elem : " + elem + " num : " + md.getNumero() + " matricule : " + mat);
                                mess2 += elem+";"; 
                                mess2 += md.getNumero()+";"; 
                                mess2 += mat; 
                                
                                RequeteEBOOP req2 = new RequeteEBOOP(RequeteEBOOP.RESERV, mess2); 
                                Network n2 = new Network();
                                cSock = n2.Init(); 
                                n2.SendRequest(cSock, req2); // ask to add a booking with id of crossing, client number and matricule 

                                ReponseEBOOP rep2 = null;
                                try {
                                    ois = new ObjectInputStream(cSock.getInputStream()); 
                                    rep2 = (ReponseEBOOP)ois.readObject(); 
                                    System.out.println("<Finalise> *** Reponse reçue");

                                    if (rep2.getCode() == ReponseEBOOP.RESERV_OK) { 
                                        session.setAttribute("idRes", rep2.getCharge()); // get the id of the reservation to show in Result 
                                    }
                                    else {
                                        session.setAttribute("idRes", ""); // nothing happened so no message 
                                    }
                                }
                                catch(Exception ex) {
                                    System.err.println("<Finalise> " + ex.getMessage());
                                }
                                
                                session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                                session.setAttribute("member", mdc);
                                session.setAttribute("problemFinalise", ""); // clear message 
                                session.setAttribute("result", message); // send message received with rep 
                                request.setAttribute("session", session); // sending session with request to Result 
                                RequestDispatcher rd = sc.getRequestDispatcher("/Result.jsp"); 
                                rd.forward(request, response);                            
                            }
                            if (rep3.getCode()== ReponseProtocolCard.CODEPASOK){ //(rep.getCode() == ReponseEBOOP.BUY_CART_BAD_MONEY) {
                                String message = rep3.getChargeUtile(); 

                                session.setAttribute("crossingList", session.getAttribute("crossingList")); // add list to session 
                                session.setAttribute("member", mdc);
                                session.setAttribute("problemFinalise", ""); // clear message 
                                session.setAttribute("idRes", "");
                                session.setAttribute("result", message); // send message received with rep 
                                request.setAttribute("session", session); // sending session with request to Result 
                                RequestDispatcher rd = sc.getRequestDispatcher("/Result.jsp"); 
                                rd.forward(request, response);
                            }
                        }
                        catch(Exception e) {
                            System.err.println("<Finalise> " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
    
    private boolean isAuthenticated (HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        // Object existe = session.getValue("UserValid");
        Object existe = session.getAttribute("UserValid");
        return existe!=null;
    }

    private boolean isAuthentificationInProgress (HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        // Object existe = session.getValue("UserValid");
        Object existe = session.getAttribute("ValidationInProgress");
        return existe!=null;
    }

    private void setAuthenticated (HttpServletRequest request, boolean b)
    {
        HttpSession session = request.getSession(true);
        if (b) session.setAttribute("UserValid", "Ok");
        else session.removeAttribute("UserValid");
    }

    private void setAuthentificationInProgress (HttpServletRequest request, boolean b)
    {
        HttpSession session = request.getSession(true);
        if (b) session.setAttribute("ValidationInProgress", "Ok");
        else session.removeAttribute("ValidationInProgress");
    } 


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
                
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet exemple de traitement d'un formulaire";
    }// </editor-fold>

}
