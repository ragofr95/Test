package pack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
	public static void main(String[] args) {
		Connection connection=connectToDatabase();
		String posizioneFile=null;
		try
        {
            InputStreamReader in = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(in);
            
            System.out.println("Inserisci il path del file: ");
            posizioneFile = br.readLine();
        }
        catch(Exception e){
        }
		Util util=new Util(connection);
		util.leggiFileXML(posizioneFile);
		
		
			//state=connection.createStatement();
			Scanner sc=new Scanner(System.in);
			System.out.println("Inserisci username che vuoi verificare se si trova sul Db: ");
			String verificaUser=sc.next();
			boolean result=util.verificaUsernameIntoDB(verificaUser);
			System.out.println("Esiste già: "+result);
			System.out.println("Inserisci username (ritorna nome e cognome se presenti): ");
			String nomeCognome=sc.next();
			String r=trovaNomeCognome(nomeCognome);
			System.out.println(r);
			System.out.println("Inserisci Nome: ");
			String nome=sc.next();
			System.out.println("Inserisci Cognome: ");
			String cognome=sc.next();
			System.out.println("Inserisci data di nascita (dd/MM/yyyy): ");
			String dataNascita=sc.next();
			String u=trovaUsername(nome,cognome,dataNascita);
			System.out.println("username corrispondente: "+u+"\n");
			System.out.println("\n\nLISTA DI TUTTE LE PERSONE PRESENTI SUL DB: ");
			System.out.println();
			ArrayList<String>list=listaTuttePersone();			
			while(list.size()!=0) {
				System.out.println(list.get(0));
				list.remove(0);
				
			}
		
		
		//chiudiConnessione(connection);
	}
	private static ArrayList<String> listaTuttePersone() {
		Connection c=Main.connectToDatabase();
		ArrayList<String>lista=new ArrayList<String>();
		try {
			Statement state=c.createStatement();
			ResultSet rs=state.executeQuery("SELECT * FROM persona");
			while(rs.next()==true) {
				String riga=rs.getString("username")+" ";
				riga+=rs.getString("nome")+" ";
				riga+=rs.getString("cognome")+" ";
				riga+=rs.getString("codiceFiscale")+" ";
				riga+=rs.getString("dataNascita")+" ";
				riga+=rs.getString("luogoNascita")+" ";
				lista.add(riga);
			}
		}catch(SQLException sql) {
			System.out.println(Costanti.ControllaQuery);
			System.exit(-1);
		}
		Main.chiudiConnessione(c);
		return lista;
	}
	private static String trovaUsername(String nome,String cognome, String dataNascita) {
		Connection c=Main.connectToDatabase();
		String username="";
		try {
			Statement state=c.createStatement();
			ResultSet rs=state.executeQuery("SELECT username FROM persona WHERE nome = '"+nome+"' and cognome= '"+cognome+"' and dataNascita = '"+dataNascita+"'");
			while(rs.next()==true) {
				username+=rs.getString("username")+" ";
			}
		}catch(SQLException e) {
			System.out.println(Costanti.ControllaQuery);
			System.exit(-1);
		}
		if(username=="") {
			username="NESSUNO";
		}
		Main.chiudiConnessione(c);
		return username;
	}
	private static String trovaNomeCognome( String username) {
		Connection c=Main.connectToDatabase();
		String ritorno="Controlla username...nessuna corrispondenza trovata";
		try {
			Statement state=c.createStatement();
			ResultSet rs=state.executeQuery("SELECT nome,cognome FROM persona WHERE username = '"+username+"'");
			if(rs.next()==true) {
				ritorno=rs.getString("nome");
				ritorno+=" ";
				ritorno+=rs.getString("cognome");
			}
			
		} catch (SQLException e) {
			System.out.println(Costanti.ControllaQuery);
			System.exit(-1);
		}
		Main.chiudiConnessione(c);
		return ritorno;
	}
	public static void chiudiConnessione(Connection con) {
		
		      try {
		        if (con != null) {
		            con.close();
		        }
		      } catch (SQLException ex) {
		    	  
		          System.out.println(Costanti.ControllaChiusuraConnessioneDB);
		          System.exit(-1);
		      }
		    }
	
	public static Connection connectToDatabase() {
		Connection con = null;
	    String url = Costanti.url;
	    String username = Costanti.user;
	    String password = Costanti.password;
	    try {
	      Class.forName(Costanti.DriverDB);
	      con = DriverManager.getConnection(url, username, password);
	    }catch(ClassNotFoundException e) {
	    } catch (SQLException ex) {
	    	System.out.println(Costanti.ControllaConnessioneDB);
	        System.exit(-1);
	    } 
	    return con;
	}
}
