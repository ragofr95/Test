package pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ScritturaFile {

	public Properties pro = new Properties();
	public int numero,usernam,nom,cognom,cf,et,motiv;
	public String headerFinale,valoreSeparatore;
	public ArrayList<User>listaOrdinata=new ArrayList<User>();
	public ScritturaFile() {
		
	}
	
	public void avvia(ArrayList<User>lista) {
		leggiProperties();
		listaOrdinata=ordinaLista(lista);
		creaFileExcel(Costanti.pathFinale,listaOrdinata);
		
	}
	private void creaFileExcel(String p,ArrayList<User>lista) {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet1 = wb.createSheet("new sheet");
		CreationHelper createHelper = wb.getCreationHelper();
		System.out.println("HEADER: "+headerFinale);
		String[]newStr=headerFinale.split(valoreSeparatore);
		
		Row row = sheet1.createRow((short)0);
		for(int i=0;i<newStr.length;i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(newStr[i]);
		}
		int i=1;
		while(listaOrdinata.size()!=0) {
			Row rigaTesto = sheet1.createRow((short)i);
			User u= listaOrdinata.get(0);
			//System.out.println(u.getUsername()+" "+u.getNome());
			listaOrdinata.remove(0);
			String[]vettore=new String[newStr.length];
			vettore[usernam]=u.getUsername();
			vettore[nom]=u.getNome();
			vettore[cognom]=u.getCognome();
			vettore[cf]=u.getCf();
			vettore[et]=u.getEtà();
			vettore[motiv]=u.getMotivoRitorno();
			//System.out.println(vettore[0]);
			for(int k=0;k<vettore.length;k++) {
				Cell cella = rigaTesto.createCell(k);
				cella.setCellValue(vettore[k]);
			}
			i++;
		}
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(p);
			 wb.write(fileOut);
			 fileOut.close();
		} catch (FileNotFoundException e) {
			System.out.println(Costanti.ErroreScritturaFile);
			System.exit(-1);
		}catch(IOException e) {
			
		}
		
	}
	private void creaFile(String p) {
		
		 try {
		      File myObj = new File(p);
		      if (myObj.createNewFile()) {
		        System.out.println(Costanti.CreazioneOK + myObj.getName());
		      } else {
		        System.out.println(Costanti.FileEsiste);
		      }
		    } catch (IOException e) {
		      System.out.println(Costanti.ErroreCreazioneFile);
		      System.exit(-1);
		    }
	}
	private  ArrayList<User> ordinaLista(ArrayList<User>lista) {
		Collections.sort(lista, new Comparator<User>() {
		        public int compare(User o1, User o2) {
		            return o1.compareTo(o2);
		        } 
		    });
		
	
		return lista;
	}
	
	public void leggiProperties() {
		FileInputStream in1;
		try {
			in1 = new FileInputStream("file.properties");
			pro.load(in1);
			numero=Integer.parseInt(pro.getProperty(Costanti.numeroSplit));
			usernam=Integer.parseInt(pro.getProperty(Costanti.usernameOutputProperties));
			nom=Integer.parseInt(pro.getProperty(Costanti.nomeOutputProperties));
			cognom=Integer.parseInt(pro.getProperty(Costanti.cognomeOutputProperties));
			et=Integer.parseInt(pro.getProperty(Costanti.etaOutputProperties));
			motiv=Integer.parseInt(pro.getProperty(Costanti.motivoOutputProperties));
			headerFinale=pro.getProperty(Costanti.headerFinale);
			valoreSeparatore=pro.getProperty(Costanti.valoreSeparatore);
			if(headerFinale==null||valoreSeparatore==null) {
				System.out.println(Costanti.ControllaPropertiesCampo);
				System.exit(-1);
			}
			cf=Integer.parseInt(pro.getProperty(Costanti.cfOutputProperties));
		} catch (FileNotFoundException e1) {
			System.out.println(Costanti.ControllaFileProperties);
			System.exit(-1);
		} catch (IOException e) {
			System.out.println(Costanti.ControllaFileProperties);
			System.exit(-1);
		}catch(NumberFormatException e) {
			System.out.println(Costanti.ControllaPropertiesNumber);
			System.exit(-1);
		}catch(NullPointerException e) {
			System.out.println(Costanti.ControllaPropertiesCampo);
			System.exit(-1);
		} 
	}
	
}
