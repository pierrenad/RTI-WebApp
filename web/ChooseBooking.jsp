<%-- 
    Document   : Principal
    Created on : 9 nov. 2019, 23:42:35
    Author     : Pierre
--%>
<%@page import="ServletsUtiles.MemberDataCenter"%>
<%@page import="jdk.jfr.Timespan"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.ArrayList"%>
<%@page import="javax.servlet.http.HttpServletRequest"%> 
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%  HttpSession sess = (HttpSession)request.getAttribute("session"); 
    String li = sess.getAttribute("crossingList").toString(); 
    String libouton = sess.getAttribute("boutonstring").toString();
    MemberDataCenter member = (MemberDataCenter)sess.getAttribute("member"); 
    String numCli = member.getNumero(); 
    String problem = sess.getAttribute("problemBuy").toString();
    
    StringTokenizer st = new StringTokenizer(li, "\n"); // get the crossings in a list 
    ArrayList<String> list = new ArrayList<>(); 
    while(st.hasMoreElements()) { 
        list.add(st.nextToken()); 
    }     

    StringTokenizer st2 = new StringTokenizer(libouton, "#"); // get the crossings in a list 
    ArrayList<String> listbouton = new ArrayList<>(); 
    while(st2.hasMoreElements()) { 
        listbouton.add(st2.nextToken()); 
    }
%> 

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Crossing choice</title>
    </head>
    <body>
        <%
            if(!problem.equals("")){
                out.println(problem);
            } 
        %>
        <h2>Client number : <%=numCli%> </h2> 
        <h1>Ticket reservation</h1> 
        <h3>Choose a crossing</h3>

        <form method="POST" action="/RTI_Partie3/Servlet"> 
                <% 
                    String id, portDep, portDest, nameBoat;
                    String date; 
                    int placeFile, placePrise, nbTicket; 
                    float prix; 
                    int bouton = 0; 
                    
                    for(int j=0; j<list.size(); j++) {
                        StringTokenizer strtok = new StringTokenizer(list.get(j),"#"); 
                        id = strtok.nextToken(); 
                        date = strtok.nextToken(); 
                        portDep = strtok.nextToken(); 
                        portDest = strtok.nextToken();
                        nameBoat = strtok.nextToken();
                        placeFile = Integer.parseInt(strtok.nextToken());
                        placePrise = Integer.parseInt(strtok.nextToken()); 
                        nbTicket = Integer.parseInt(strtok.nextToken());
                        prix = Float.parseFloat(strtok.nextToken()); 
                        StringTokenizer strtokdate = new StringTokenizer(date," "); 
                        String dateStr = strtokdate.nextToken(); 
                        Date datetrav = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                        Date datenow = new Date();
                        long diff = (datetrav.getTime() - datenow.getTime()) / (1000 * 60 * 60 * 24);
                        
                        if(diff<2)
                        {
                            out.println("<p><b><u>Date</u></b> : " + date + " <b><u>Boat name</u></b> : " + nameBoat + " <b><u>From</u></b> : " + portDep + " <b><u>To</u></b> : " + portDest + " <b><u>LAST MINUTE Price</u></b> : " + prix*0.75 + "euro/pers.");  
                            //out.println("<button type=\"submit\" name=\"Id\" value=\"" + id + "\">Add to cart</button>");
                        }
                        else
                        {
                            out.println("<p><b><u>Date</u></b> : " + date + " <b><u>Boat name</u></b> : " + nameBoat + " <b><u>From</u></b> : " + portDep + " <b><u>To</u></b> : " + portDest + " <b><u>Price</u></b> : " + prix + "euro/pers.");  
                            //out.println("<button type=\"submit\" name=\"Id\" value=\"" + id + "\">Add to cart</button>");
                        }  
                        bouton=0;
                        for(int i=0; i<listbouton.size()-1;i++)
                        {
                            if(id.equals(listbouton.get(i)))
                            {
                                bouton=1;
                            }
                        }
                        if(bouton==0) {
                            out.println("<button type=\"submit\" name=\"Id\" value=\"" + id + "\">Add to cart</button>");
                        }

                        else {
                            out.println("<button type=\"submit\" name=\"Id\" value=\"-" + id + "\">Remove from cart</button>");
                        }
                        /*out.println("<p><b><u>Date</u></b> : " + date + " <b><u>Boat name</u></b> : " + nameBoat + " <b><u>From</u></b> : " + portDep + " <b><u>To</u></b> : " + portDest + " <b><u>Price</u></b> : " + prix + "euro/pers.");  
                        out.println("<button type=\"submit\" name=\"Id\" value=\"" + id + "\">Add to cart</button>");*/
                    }
                    out.println("<input type=\"hidden\" name=\"action\" value=\"Add to cart\">");
                %>
        </form>
        <form method="POST" action="/RTI_Partie3/Servlet"> 
            <p><input type="submit" name="action" value="Buy"> 
        </form>
    </body>
</html>
