/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServletsUtiles;

/**
 *
 * @author Pierre
 */
public class MemberDataCenter {

    private String nom;
    private String prenom;
    private String numero;
    private String eMail;
    private String adresse;
    private String pays; 
    private boolean nouveau; 

    public MemberDataCenter()
    {
        nom = prenom = numero = eMail = adresse = pays = null; nouveau=true; 
    }
    public MemberDataCenter(String num)
    {
        numero = num; 
        nom = prenom = eMail = adresse = pays = null; nouveau=false; 
    }

    public boolean equals(MemberDataCenter mdc)
    {
        return ( nom.equals(mdc.nom) && prenom.equals(mdc.prenom) && numero.equals(mdc.numero) && adresse.equals(mdc.adresse) && eMail.equals(mdc.eMail) && pays.equals(mdc.pays) && nouveau==mdc.nouveau); 
    }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNumero() { return numero; }
    public String getEMail() { return eMail; }
    public String getAdresse() { return adresse; }
    public String getPays() { return pays; }
    public boolean getNouveau() { return nouveau; } 
    

    public void setNom(String x) { nom = x; }
    public void setPrenom(String x) { prenom = x; }
    public void setNumero(String x) { numero = x; }
    public void setEMail(String x) { eMail = x; } 
    public void setAdresse(String x) { adresse = x; }
    public void setPays(String x) { pays = x; }
    public void setNouveau(boolean  x) { nouveau = x; }
    
}
