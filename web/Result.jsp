<%-- 
    Document   : Result
    Created on : 24 nov. 2019, 12:06:44
    Author     : Pierre
--%>

<%@page import="ServletsUtiles.MemberDataCenter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%  HttpSession sess = (HttpSession)request.getAttribute("session"); 
    String result = sess.getAttribute("result").toString();
    MemberDataCenter member = (MemberDataCenter)sess.getAttribute("member"); 
    String numCli = member.getNumero(); 
    String idRes = session.getAttribute("idRes").toString(); 
%> 

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Result Page</title>
    </head>
    <body>
        <h2>Client number : <%=numCli%> </h2> 
        <h1>Result of the purchase </h1>
        <h4><%= result %> 
            <% 
                if(idRes.equals("")) {
                    out.print("</h4>");
                }
                else{
                    out.print(": booking number " + idRes + "</h4>"); 
                }
            %>
        <a href="/RTI_Partie3/Login.jsp">Return to login</a>
    </body>
</html>
