/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServletsUtiles;

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
import db.facilities.GestionBD; 
import java.io.ObjectInputStream;
import java.net.Socket;
import rti_partie2.*; 
import java.sql.*;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import network.Network;
import rti_Windows.CustomerWindow;

/**
 *
 * @author Pierre
 */
public class Servlet extends HttpServlet {

    private boolean servOn = false; 
    private int nbreConnexions = 0; 
    private ObjectInputStream ois;
    private Socket cSock; 
    
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
                
        /*try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Page</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<HTML><HEAD><TITLE>");
            out.println("Réponse de la servlet à l'accès client");
            out.println("</TITLE></HEAD><BODY>"); 
            out.println("<h1>Servlet Servlet at " + request.getContextPath() + "</h1>");
            out.println("<p><h3>Bonjour toi ;-) !</h3><p>"); 
            
            out.println("<H1>Accueil d'un nouveau client</H1>");
            out.println("<p>Bonjour cher " + request.getParameter("prenom") + " "
            + request.getParameter("nom") + " !!!");

            synchronized (this) { nbreConnexions++; }
            out.println("<P>Vous etes notre client numéro " +
            Integer.toString(nbreConnexions) + " ...");
            out.println("<p>Méthode utilisée = " + request.getMethod() + "<p>"); 
            
            out.println("</body>");
            out.println("</html>");
            out.close();
        }*/
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

        if(request.getParameter("address") == null) { // s'il s'agit d'une demande de connection 
            RequeteProtocol req = new RequeteProtocol(RequeteProtocol.LOGIN_WEB, request.getParameter("numCli"));

            Network n = new Network();
            cSock = n.Init(); 
            n.SendRequest(cSock, req); 

            ReponseProtocol rep = null;
            try {
                ois = new ObjectInputStream(cSock.getInputStream()); 
                rep = (ReponseProtocol)ois.readObject();
                System.out.println("<doGet> *** Reponse reçue");

                if(rep.getCode() == ReponseProtocol.CLIENT_FOUND) {
                    StringTokenizer st = new StringTokenizer(rep.getCharge(), "#");
                    String numcli = st.nextToken(); 
                    String frstName = st.nextToken(); 
                    String name = st.nextToken(); 
                    String adr = st.nextToken(); 
                    String country = st.nextToken(); 
                    out.println("<p>Hello dear " + frstName + " " + name + "<p>"); 
                    synchronized (this) { nbreConnexions++; }
                    out.println("<P>You are the customer number " + Integer.toString(nbreConnexions)); 
                    //out.println("<p>Method used = " + request.getMethod() + "<p>"); 
                    //out.println("<p>Client found <p>");
                    out.println("<p>" + frstName + " ; " + name + " ; " + adr + " ; " + country + "<p>"); 
                    out.println("<a href=\"FormPageBooking.html\">Continue</a> "); 
                }
                else {
                    out.println("<p>Hello, this is the first time you connect !<p>"); 
                    out.println("<p>Please register to continue<p>"); 
                    //out.println("<p>Method used = " + request.getMethod() + "<p>"); 
                    out.println("<p>Client not found <p>");
                    out.println("<p><a href=\"NewClient.html\">Register</a> <p>"); 
                    out.println("<p><a href=\"FormPageLogin.html\">Retry Login</a> <p>"); 
                }
            }
            catch (Exception e) { 
                System.err.println("<doGet> " + e.getMessage()); 
            }
        }
        else { // s'il s'agit d'une demande pour s'enregistrer 
            RequeteEBOOP req = new RequeteEBOOP(RequeteEBOOP.REGISTER, request.getParameter("name").concat("#" + request.getParameter("firstName")  
                    + "#" + request.getParameter("address") + "#" + request.getParameter("email") + "#" + request.getParameter("country")));// + "#" + request.getParameter("country"))); 

            Network n = new Network();
            cSock = n.Init(); 
            n.SendRequest(cSock, req); 

            ReponseEBOOP rep = null;
            try {
                ois = new ObjectInputStream(cSock.getInputStream()); 
                rep = (ReponseEBOOP)ois.readObject();
                System.out.println("<doGet> *** Reponse reçue");

                out.println("<p>You have the client number " + rep.getCharge() + "!!!<p>");
                synchronized (this) { nbreConnexions++; }
                out.println("<P>You are the customer number " + Integer.toString(nbreConnexions)); 
                out.println("<p>Method used = " + request.getMethod() + "<p>"); 
                out.println("<a href=\"FormPageBooking.html\">Continue</a> ");
            }
            catch(Exception e) {
                System.out.println("<doGet> " + e.getMessage());
                out.println("<p>Connection to HappyFerryDB not established<p>"); 
            } 
        }
        
        //processRequest(request, response);
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
        out.println("<HTML><HEAD><TITLE>");
        out.println("Answer Post Request");
        out.println("</TITLE></HEAD><BODY>"); 
        out.println("<H1>Ticket reservation</H1>"); 
        out.println(request.getParameter("cross")); 
        //out.println("<p>Method used = " + request.getMethod() + "<p>"); 
        
        //processRequest(request, response);
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
