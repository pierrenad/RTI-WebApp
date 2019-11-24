<%-- 
    Document   : index
    Created on : 9 nov. 2019, 16:02:31
    Author     : Pierre
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
    </head>
    <body>
        <H3>*** Connection ***</H3>
        <P>Please identify :
        <BR>&nbsp; <!-- fait un paragraphe vide --> 
        
        <form method="POST" action="/RTI_Partie3/Servlet"> 
            <p>Authentication or Register ?</p>
            <SELECT name="action">
            <OPTION>Authentication
            <OPTION>Sign up
            </SELECT></P> 

            <P>Client number : <input type="number" name="numCli" size=20></P> 
            <P><input type="submit" value="Action"></P>
        </form>
    </body>
</html>
