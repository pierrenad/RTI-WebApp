<%-- 
    Document   : Principal
    Created on : 9 nov. 2019, 23:42:35
    Author     : Pierre
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Choix traversée</title>
    </head>
    <body>
        <H1>Ticket reservation</H1> 
        out.println(request.getParameter("cross")); 
        <!--<h1>Welcome to this website!</h1>
        <h3>Choose a crossing</h3>-->
        <!--<a href="FormPageBooking.html">Booking</a> --> 
        <!--<label>Crossing list</label>
        <BR>&nbsp;
        <form method="POST" action="/RTI_Partie3/Servlet"> 
            <select id="crossing" name="cross"> 
                <optgroup label="Crossing"> 
                    <option selected="selected">From: Alaènne - To: Ohelpéos-saink - Date: 2019-09-15 18:10</option> 
                    <option>From: Ohelpéos-saink - To: Aupaivédeu - Date: 2019-09-16 07:11</option> 
                    <option>From: Aupaivédeu - To: Obai-Ixhe - Date: 2019-09-17 20:12</option> 
                    <option>From: Obai-Ixhe - To: Alaènne - Date: 2019-09-18 09:13</option> 
                    <option>From: Alaènne - To: Aupaivédeu - Date: 2019-09-19 22:14</option> 
                    <option>From: Ohelpéos-saink - To: Obai-Ixhe - Date: 2019-09-20 11:15</option> 
                </optgroup>
            </select>
            <P><input type="submit" value="Confirm crossing"></P>
        </form> -->
    </body>
</html>
