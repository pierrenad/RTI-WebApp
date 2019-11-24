<%-- 
    Document   : NewClient
    Created on : 21 nov. 2019, 15:22:50
    Author     : Pierre
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%  HttpSession sess = (HttpSession)request.getAttribute("session"); 
    String problem = sess.getAttribute("problemRegister").toString();
%> 

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Register Page</title>
    </head>
    <body>
        <%
            if(!problem.equals("")){
                out.println(problem);
            } 
        %>
        <H3>*** Register ***</H3>
        <P>Please enter your details :
        <BR>&nbsp;
        
        <form method="POST" action="/RTI_Partie3/Servlet"> 
            <P>Name : <input type="text" name="name" size=20></P>
            <P>First Name : <input type="text" name="firstName" size=20></P>
            <P>Address : <input type="text" name="address" size=20></P>
            <P>eMail : <input type="text" name="email" size=20></P>
            <P>Country : <input type="text" name="country" size=20></P>
            <P><input type="submit" name="action" value="Register"></P>
        </form> 

    </body>
</html>
