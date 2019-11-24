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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%  HttpSession sess = (HttpSession)request.getAttribute("session"); 
    String li = sess.getAttribute("crossingList").toString(); 
    MemberDataCenter member = (MemberDataCenter)sess.getAttribute("member"); 
    String numCli = member.getNumero(); 
    String problem = sess.getAttribute("problemBuy").toString();
    
    StringTokenizer st = new StringTokenizer(li, "\n"); // get the crossings in a list 
    ArrayList<String> list = new ArrayList<>(); 
    while(st.hasMoreElements()) { 
        list.add(st.nextToken()); 
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
                        out.println("<p><b><u>Date</u></b> : " + date + " <b><u>Boat name</u></b> : " + nameBoat + " <b><u>From</u></b> : " + portDep + " <b><u>To</u></b> : " + portDest + " <b><u>Price</u></b> : " + prix + "euro/pers.");  
                        out.println("<button type=\"submit\" name=\"Id\" value=\"" + id + "\">Add to cart</button>");
                    }
                    out.println("<input type=\"hidden\" name=\"action\" value=\"Add to cart\">");
                %>
        </form>
        <form method="POST" action="/RTI_Partie3/Servlet"> 
            <p><input type="submit" name="action" value="Buy"> 
        </form>
    </body>
</html>
