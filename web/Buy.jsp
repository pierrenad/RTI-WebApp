<%-- 
    Document   : buy
    Created on : 23 nov. 2019, 18:13:35
    Author     : Pierre
--%>
<%@page import="ServletsUtiles.MemberDataCenter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.StringTokenizer"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%  HttpSession sess = (HttpSession)request.getAttribute("session"); 
    String elements = sess.getAttribute("PrixEtMatricule").toString();
    MemberDataCenter member = (MemberDataCenter)sess.getAttribute("member"); 
    String numCli = member.getNumero(); 
    boolean nouveau = member.getNouveau(); 
    String problem = sess.getAttribute("problemFinalise").toString();
    
    StringTokenizer st = new StringTokenizer(elements, "#"); // get the crossings in a list 
    float prix = Float.parseFloat(st.nextToken());
    String matricule = st.nextToken();
%> 

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Buy Page</title>
    </head>
    <body>
        <%
            if(!problem.equals("")){
                out.println(problem);
            } 
        %>
        <H3>*** Buy ***</H3>
        <h2>Client number : <%=numCli%> </h2> 
        <P>Please enter your details :
        <BR>&nbsp;
        
        <form method="POST" action="/RTI_Partie3/Servlet"> 
            <%
                if(!nouveau) {
                    prix -= (prix*5/100); 
                    out.println("<u>5 euros reduction</u>");
                }
                out.println("<input type=\"hidden\" name=\"prixtot\" value=\"" + prix + "\">");
                out.println("<input type=\"hidden\" name=\"matricule\" value=\"" + matricule + "\">");
                out.println("<p><b><u>Total price :</u></b> " + prix + " <b><u> euros.</u></b> : ");  
            %>
            <P>Card Number : <input type="text" name="num" size=20></P>
            <P>Expiration Date : <input type="text" name=date" size=20></P>
            <P>Code : <input type="number" name="code" size=20></P>
            <P><input type="submit" name="action" value="Finalise"></P>
        </form> 
        
    </body>
</html>
