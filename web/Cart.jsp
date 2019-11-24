<%-- 
    Document   : cart
    Created on : 23 nov. 2019, 13:51:34
    Author     : Pierre
--%>

<%@page import="ServletsUtiles.MemberDataCenter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.StringTokenizer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%  HttpSession sess = (HttpSession)request.getAttribute("session"); 
    String elements = sess.getAttribute("chosenElements").toString();
    MemberDataCenter member = (MemberDataCenter)sess.getAttribute("member"); 
    String numCli = member.getNumero(); 
    String problem = sess.getAttribute("problemConfirm").toString(); 
    
    StringTokenizer st = new StringTokenizer(elements, "\n"); // get the crossings in a list 
    ArrayList<String> list = new ArrayList<>(); 
    while(st.hasMoreElements()) { 
        list.add(st.nextToken()); 
    } 
%> 

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Cart Page</title>
    </head>
    <body>
        <%
            if(!problem.equals("")){
                out.println(problem);
            } 
        %>
        <h2>Client number : <%=numCli%> </h2> 
        <h1>Cart resume</h1> 

        <form method="POST" action="/RTI_Partie3/Servlet"> 
                <% 
                    String portDep, portDest;
                    String date; 
                    float prix; 
                    float prixtotal=0;
                    
                    for(int j=0; j<list.size(); j++) {
                        StringTokenizer strtok = new StringTokenizer(list.get(j),"#"); 
                        portDep = strtok.nextToken(); 
                        portDest = strtok.nextToken();
                        date = strtok.nextToken(); 
                        prix = Float.parseFloat(strtok.nextToken()); 
                        strtok.nextToken(); // id mais pas besoin ici
                        prixtotal+=prix; //System.out.println("<cart> prixtot : " + prixtotal);
                        out.println("<p><b><u>Date</u></b> : " + date + " <b><u>From</u></b> : " + portDep + " <b><u>To</u></b> : " + portDest + " <b><u>Price</u></b> : " + prix + "euro/pers.");  
                    } 
                    out.println("<input type=\"hidden\" name=\"action\" value=\"Confirm\">");
                %>               
            <P>Matricule : <input type="text" name="matricule" size=20></P>          
            <P>Number of passengers : <input type="text" name="nbpass" size=20></P>
            <p><button type="submit" name="prixtot" value=<%=prixtotal%>>Confirm</button> 
        </form>
    </body>
</html>
