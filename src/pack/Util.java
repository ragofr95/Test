package pack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Util {
	public Properties pro = new Properties();
	public int username,nome,cognome,cf,luogoNascita,numeroCampi,dataDiNascita;
	public String usernameS="",nomeS="",cognomeS="",cfS="",luogoNascitaS="",dataDiNascitaS="";
	public int nomeOutput,usernameOutput,cognomeOutput,cfOutput,etaOutput,motivoOutput;
	public String headerFinale,valoreSeparatore;
	public int campiExtra;
	public ArrayList<Riga>listaRighe=new ArrayList<Riga>();
	public ArrayList<User>listaRigheAnalizzate=new ArrayList<User>();
	public String motivoRitorno="",ritornoData="";
	public int età=0;
	
	public Connection connection;
	public Util(Connection connection) {
		this.connection=connection;
	}

	public Util() {
		
	}

	public void leggiFileXML(String posizioneFile) {
		//aggiornaDB();
		try  
		{  
			File doc=new File(posizioneFile);
		    leggiProperties();
			FileInputStream fis = new FileInputStream(doc);   //obtaining bytes from the file  
			//creating Workbook instance that refers to .xlsx file  
			XSSFWorkbook wb = new XSSFWorkbook(fis);   
			XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
			Iterator<Row> itr = sheet.iterator();    //iterating over excel file  
			int i=0;
			int contatore=0;
			int rigaFinale=0;
			while (itr.hasNext()){	
				Row row = itr.next();	
				if(row.getRowNum()>0) {
					contatore++;
					rigaFinale=row.getRowNum();
					Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column  				
					while (cellIterator.hasNext()){		
						Cell cell = cellIterator.next();
						if(cell.getCellType()==CellType.STRING) {
							int indiceColonna=cell.getColumnIndex();
							if(indiceColonna==username){
								usernameS=cell.getStringCellValue().toString();
							}
							else if(indiceColonna==cognome) {
								cognomeS=cell.getStringCellValue().toString();
								
							}
							else if(indiceColonna==nome) {
								nomeS=cell.getStringCellValue().toString();
							}
							else if(indiceColonna==cf) {
								cfS=cell.getStringCellValue().toString();
							}
							else if(indiceColonna==dataDiNascita) {
								dataDiNascitaS=cell.getStringCellValue().toString();
							}
							else if(indiceColonna==luogoNascita) {
								luogoNascitaS=cell.getStringCellValue().toString();
							}else {
								campiExtra=1;
							}
							 
						}
						if(cell.getCellType()==CellType.NUMERIC) {
							int indiceColonna=cell.getColumnIndex();
							if(indiceColonna==username) {
								usernameS=String.valueOf((cell.getNumericCellValue()));
							}
							else if(indiceColonna==nome){
								nomeS=String.valueOf(cell.getNumericCellValue());
							}
							else if(indiceColonna==cognome) {
								cognomeS=String.valueOf(cell.getNumericCellValue());
							}
							else if(indiceColonna==cf) {
								cfS=String.valueOf(cell.getNumericCellValue());
							}
							else if(indiceColonna==dataDiNascita) {
								 Date javaDate= DateUtil.getJavaDate((double)cell.getNumericCellValue());
								 String dateToString=convertiData(javaDate);
								 dataDiNascitaS=dateToString;
							     
							}
							else if(indiceColonna==luogoNascita) {
								luogoNascitaS=String.valueOf(cell.getNumericCellValue());
							}
							else {
								campiExtra=0;
							}
						}
						if(cell.getCellType()==CellType.BLANK){
							//System.out.println(" sssssss");
						}
					}
					Riga riga=new Riga(usernameS,nomeS,cognomeS,cfS,dataDiNascitaS,luogoNascitaS,campiExtra);
					
					resetVariabili();
					listaRighe.add(riga);
				}
				
				
			}
			int righeBianche=rigaFinale-contatore;
			aggiungiRighe(righeBianche);
			verificaValidazioni(listaRighe);
			ScritturaFile sc=new ScritturaFile();
			sc.avvia(listaRigheAnalizzate);
		}catch(Exception e)  {
			System.out.println(Costanti.ErroreLetturaFile);
			System.exit(-1);
		} 
	}
	
	private void verificaValidazioni(ArrayList<Riga>lista) {
		
		lista.forEach((n)->{
			if(n.getCampiExtra()==0) {
				if(n.getNome()==""||n.getUsername()==""||n.getCognome()==""||n.getCf()==""||n.getDataNascita()==""||n.getLuogoNascita()=="") {
					motivoRitorno=Costanti.CampiObbligatori;
				}else {
					motivoRitorno=analizzaUsername(n.getUsername(),motivoRitorno);
					motivoRitorno=analizzaNome(n.getNome(),motivoRitorno);
					motivoRitorno=analizzaCognome(n.getCognome(),motivoRitorno);
					
					motivoRitorno=analizzaCodiceFiscale(n.getCf(),motivoRitorno);
					
					ritornoData=analizzaDataNascita(n.getDataNascita(),motivoRitorno);
					
					motivoRitorno=ritornoData;
					
					motivoRitorno=analizzaLuogoNascita(n.getLuogoNascita(),motivoRitorno);
					if(motivoRitorno=="") {
		        		try {
		        			
							//Statement statement = connection.createStatement();
							boolean result=verificaUsernameIntoDB(n.getUsername());
							if(result==false) {
								Connection con=Main.connectToDatabase();
								Statement stat=con.createStatement();
								String inserimento= "VALUES ('"+n.getUsername()+"', '"+n.getNome()+"', '"+n.getCognome()+"', '"+n.getCf()+"', '"+n.getDataNascita()+"','"+n.getLuogoNascita()+"')";
								stat.executeUpdate("INSERT INTO persona " +inserimento);
								Main.chiudiConnessione(con);
							}
							//String inserimento= "VALUES ('"+n.getUsername()+"', '"+n.getNome()+"', '"+n.getCognome()+"', '"+n.getCf()+"', '"+n.getLuogoNascita()+"','"+n.getLuogoNascita()+"')";
							//statement.executeUpdate("INSERT INTO persona " +inserimento);
						} catch (SQLException e) {
							e.printStackTrace();
						}
		        	}
					
				}
				
			}else {
				motivoRitorno=Costanti.RigaNonConforme;
			}
			
        	if(ritornoData==""&&n.getDataNascita()!="") {
        		età=calcolaEtà(n.getDataNascita());
        		
        	} 	  
        
			User u=new User(n.getUsername(),n.getNome(),n.getCognome(),n.getCf(),String.valueOf(età),motivoRitorno);
			listaRigheAnalizzate.add(u);
			età=0;
			//System.out.println(n.getUsername()+","+n.getNome()+","+n.getCognome()+","+n.getCf()+", età"+","+motivoRitorno);
			motivoRitorno="";
		}
		);	
	}
	public boolean verificaUsernameIntoDB( String username) {
		Connection con=Main.connectToDatabase();
		
		boolean result=false;
		try {
			Statement statement=con.createStatement();
			ResultSet rs=statement.executeQuery("SELECT * FROM persona WHERE username = '"+username+"'");	
			if(rs.next()==true)
			{
				result=true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Main.chiudiConnessione(con);
		return result;
	}
	private void aggiungiRighe(int righeBianche) {
		resetVariabili();
		for(int k=0;k<righeBianche;k++) {
			Riga riga=new Riga(usernameS,nomeS,cognomeS,cfS,dataDiNascitaS,luogoNascitaS,campiExtra);
			listaRighe.add(riga);
		}
	}
	private String convertiData(Date javaDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String format = formatter.format(javaDate);
		return format;
	  }
	

	private void resetVariabili() {
		usernameS="";nomeS="";cognomeS="";
		cfS="";dataDiNascitaS="";luogoNascitaS="";
		campiExtra=0;
	}
	
	private void leggiProperties() {
		FileInputStream in1;
		try {
			//in1 = new FileInputStream("C:/Users/RagoFr/Exercise/Esercizio/src/pack/file.properties");
			//String currentPath=new java.io.File(".").getAbsolutePath(); 
			//System.out.println(currentPath);
			in1 = new FileInputStream("file.properties");
			pro.load(in1);
			nome=Integer.parseInt(pro.getProperty(Costanti.nomeProperties).trim());
        	cognome=Integer.parseInt(pro.getProperty(Costanti.cognomeProperties).trim());
        	username=Integer.parseInt(pro.getProperty(Costanti.usernameProperties).trim());
        	cf=Integer.parseInt(pro.getProperty(Costanti.cfProperties).trim());
        	dataDiNascita=Integer.parseInt(pro.getProperty(Costanti.dataNascitaProperties).trim());
        	luogoNascita=Integer.parseInt(pro.getProperty(Costanti.luogoNascitaProperties).trim());
        	numeroCampi=Integer.parseInt(pro.getProperty(Costanti.numeroCampi).trim());
        	nomeOutput=Integer.parseInt(pro.getProperty(Costanti.nomeOutputProperties).trim());
        	usernameOutput=Integer.parseInt(pro.getProperty(Costanti.usernameOutputProperties).trim());
        	cfOutput=Integer.parseInt(pro.getProperty(Costanti.cfOutputProperties).trim());
        	etaOutput=Integer.parseInt(pro.getProperty(Costanti.etaOutputProperties).trim());
        	motivoOutput=Integer.parseInt(pro.getProperty(Costanti.motivoOutputProperties));
        	headerFinale=pro.getProperty(Costanti.headerFinale);
        	valoreSeparatore=pro.getProperty(Costanti.valoreSeparatore);
        	if(headerFinale==null||valoreSeparatore==null) {
        		System.out.println(Costanti.ControllaPropertiesCampo);
        		System.exit(-1);
        	}
		} catch (FileNotFoundException e1) {
			System.out.println(Costanti.ControllaFileProperties);
			System.exit(-1);
		} catch (IOException e) {
			System.out.println(Costanti.ControllaFileProperties);
			System.exit(-1);
		}catch(NumberFormatException e){
			System.out.println(Costanti.ControllaPropertiesNumber);
			System.out.println();
			System.out.println("username: "+pro.getProperty(Costanti.usernameProperties));
			System.out.println("nome: "+pro.getProperty(Costanti.nomeProperties));
			System.out.println("cognome: "+pro.getProperty(Costanti.cognomeProperties));
			System.out.println("cf: "+pro.getProperty(Costanti.cfProperties));
			System.out.println("data di nascita: "+pro.getProperty(Costanti.dataNascitaProperties));
			System.out.println("luogo di nascita: "+pro.getProperty(Costanti.luogoNascitaProperties));
			System.out.println("numero campi: "+pro.getProperty(Costanti.numeroCampi));
			System.out.println("numerod split: "+pro.getProperty(Costanti.numeroSplit));
			System.out.println("età: "+pro.getProperty(Costanti.etaProperties));
			System.out.println("motivo di errore: "+pro.getProperty(Costanti.motivoErroreProperties));
			System.out.println("nome output: "+pro.getProperty(Costanti.nomeOutputProperties));
			System.out.println("username output: "+pro.getProperty(Costanti.usernameOutputProperties));
			System.out.println("età output: "+pro.getProperty(Costanti.etaOutputProperties));
			System.out.println("cf output: "+pro.getProperty(Costanti.cfOutputProperties));
			System.out.println("motivo errore output: "+pro.getProperty(Costanti.motivoOutputProperties));
			System.exit(-1);
		}catch(NullPointerException e) {
			System.out.println(Costanti.ControllaPropertiesCampo);
			System.out.println();
			System.out.println("username: "+pro.getProperty(Costanti.usernameProperties));
			System.out.println("nome: "+pro.getProperty(Costanti.nomeProperties));
			System.out.println("cognome: "+pro.getProperty(Costanti.cognomeProperties));
			System.out.println("cf: "+pro.getProperty(Costanti.cfProperties));
			System.out.println("data di nascita: "+pro.getProperty(Costanti.dataNascitaProperties));
			System.out.println("luogo di nascita: "+pro.getProperty(Costanti.luogoNascitaProperties));
			System.out.println("numero campi: "+pro.getProperty(Costanti.numeroCampi));
			System.out.println("numerod split: "+pro.getProperty(Costanti.numeroSplit));
			System.out.println("età: "+pro.getProperty(Costanti.etaProperties));
			System.out.println("motivo di errore: "+pro.getProperty(Costanti.motivoErroreProperties));
			System.out.println("nome output: "+pro.getProperty(Costanti.nomeOutputProperties));
			System.out.println("username output: "+pro.getProperty(Costanti.usernameOutputProperties));
			System.out.println("età output: "+pro.getProperty(Costanti.etaOutputProperties));
			System.out.println("cf output: "+pro.getProperty(Costanti.cfOutputProperties));
			System.out.println("motivo errore output: "+pro.getProperty(Costanti.motivoOutputProperties));
			System.exit(-1);
		}
		
	}

	private int calcolaEtà(String dataNascita) {
		String[] formatoData=ricostruisciData(dataNascita);
		int anno=Integer.parseInt(formatoData[2]);
		Calendar calendar = GregorianCalendar.getInstance();
		int annoCorrente= calendar.get( Calendar.YEAR );
		return annoCorrente-anno;
	}
	private String analizzaCodiceFiscale(String codiceFiscale,String motivoRifiuto) {
		boolean errore=true;
		if(codiceFiscale.length()!=16) {
			if(motivoRifiuto==null) {
				motivoRifiuto=Costanti.CodiceFiscale;
			}
			else {
				motivoRifiuto+=Costanti.CodiceFiscale;
			}
		}else {
			boolean verifica=verificaLettere(codiceFiscale);
			if(!verifica) {
				if(motivoRifiuto==null) {
					motivoRifiuto=Costanti.CodiceFiscale;
				}
				else {
					motivoRifiuto+=Costanti.CodiceFiscale;
				}
			}
		}
		return motivoRifiuto;
	}
	private boolean verificaLettere(String codiceFiscale) {
		boolean ok=true;
		for(int i=0;i<6;i++) {
			char c = codiceFiscale.charAt (i);
			if(!((c >= 'a' && c <= 'z') ||(c >= 'A' && c <= 'Z'))) {
				ok=false;
				return ok;
			}
		}
		return ok;
	}
	private String analizzaDataNascita(String data,String motivoRifiuto) {
		boolean errore=true;
		boolean altriControlli=true;
		if(data!="") {
		for(int n=0;n<data.length();n++) {
			char c = data.charAt (n);
			if(((c >= '0' && c <= '9')||(c=='/')||(c==' '))) {
				errore=false;	
			}else {
				altriControlli=false;
				errore=true;
				if(motivoRifiuto==null) {
					motivoRifiuto=Costanti.DataNascita;
				}
				else {
					motivoRifiuto+=Costanti.DataNascita;
				}
				return motivoRifiuto;
			}
		}
		if(altriControlli) {
			String[] formatoData=ricostruisciData(data);
			int giorno=Integer.parseInt(formatoData[0]);
			int mese=Integer.parseInt(formatoData[1]);
			int anno=Integer.parseInt(formatoData[2]);
			boolean controllo=controllaData(giorno,mese,anno);
			if(!controllo) {
				if(motivoRifiuto==null) {
					motivoRifiuto=Costanti.DataNascita;
				}
				else {
					motivoRifiuto+=Costanti.DataNascita;
				}
			}
		}}
		
		return motivoRifiuto;
	}
	private boolean controllaData(int giorno,int mese,int anno) {
		boolean ok=true;
		if(mese<1||mese>12) {
			ok=false;
		}
		else if((mese==4||mese==6||mese==9||mese==11)) {
			if(giorno>30) {
				ok=false;
			}
		}
		else if(mese==2) {
			if(annoBisestile(anno)) {
				if(giorno>29) {
					ok=false;
				}
			}else {
				if(giorno>28) {
					ok=false;
				}
			}
		}
		return ok;
	}
	private boolean annoBisestile(int anno) {
		boolean annoBisestile=false; 
		if ( ( ( anno % 4 == 0 ) && ( anno % 100 != 0 ) ) || ( anno % 400 == 0 ) ) {
			annoBisestile=true;
		 }
		return annoBisestile;
	}
	private String[] ricostruisciData(String data) {
		String[]newStr=data.split("/",3);
		return newStr;
	}
	private String analizzaLuogoNascita(String luogo,String motivoRifiuto) {
		boolean errore=true;
		for (int n =0;n< luogo.length ();  n ++) {
			char c = luogo.charAt (n); 
			if(c==' '||c=='('||c==')'||(c >= 'a' && c <= 'z') ||(c >= 'A' && c <= 'Z')) {
				errore=true;	
			}else {
				errore=false;
				if(motivoRifiuto==null) {
					motivoRifiuto=Costanti.LuogoNascita;
				}
				else {
					motivoRifiuto+=Costanti.LuogoNascita;
				}
				return motivoRifiuto;
			}
		}
		return motivoRifiuto;
	}
	private String analizzaNome(String nome, String motivoRifiuto ) {
		
		boolean errore=true;
		for (int n =0;n< nome.length ();  n ++) {
			char c = nome.charAt (n); 
			if(c==' '||(c >= 'a' && c <= 'z') ||(c >= 'A' && c <= 'Z')) {
				errore=true;	
			}else {
				errore=false;
				if(motivoRifiuto==null) {
					motivoRifiuto=Costanti.Nome;
				}
				else {
					motivoRifiuto+=Costanti.Nome;
				}
				return motivoRifiuto;
			}
		}
		//System.out.println("DOPO: "+motivoRifiuto);
		return motivoRifiuto;
	}
	private String analizzaCognome(String cognome, String motivoRifiuto ) {
		boolean errore=true;
		for (int n =0;n< cognome.length ();  n ++) {
			char c = cognome.charAt (n); 
			if(c==' '||(c >= 'a' && c <= 'z') ||(c >= 'A' && c <= 'Z')) {
				errore=true;	
			}else {
				errore=false;
				if(motivoRifiuto==null) {
					motivoRifiuto=Costanti.Cognome;
				}
				else {
					motivoRifiuto+=Costanti.Cognome;
				}
				return motivoRifiuto;
			}
		}
		return motivoRifiuto;
	}

	private String verificaCampiObbligatori(String line,String[]newStr) {
        
        String motivoRifiuto=null;
        for(int i=0;i<newStr.length;i++) {
        	
        	if(newStr[i]=="") {
        		motivoRifiuto=Costanti.CampiObbligatori;
        	}       	
        }
        return motivoRifiuto;
        //analizzaUsername(newStr[0],line,motivoRifiuto);
	}
	//RICONTROLLA
	private String analizzaUsername(String user, String motivoRifiuto) {
		//USERNAME può contenere solo lettere, numeri e -_ (no spazi per esempio);
		boolean errore=true;
		for (int n =0;n< user.length ();  n ++) {
			char c = user.charAt (n); 
			if((c >= 'a' && c <= 'z') ||(c >= 'A' && c <= 'Z') ||(c >= '0' && c <= '9')||c=='-'||c=='_') {
				errore=true;	
			}else {
				errore=false;
				if(motivoRifiuto==null) {
					motivoRifiuto=Costanti.Username;
				}
				else {
					motivoRifiuto+=Costanti.Username;
				}
				return motivoRifiuto;
			}
		}
		return motivoRifiuto;
	}
}
