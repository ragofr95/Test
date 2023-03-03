package pack;

public class User implements Comparable<User> {
	
	public String cognome,nome,età,username,motivoRitorno,cf;
	
	public User(String username,String nome,String cognome,String cf,String età,String motivoRitorno) {
		this.cognome=cognome;
		this.nome=nome;
		this.username=username;
		this.cf=cf;
		this.età=età;
		this.motivoRitorno=motivoRitorno;
		
	}

	

	public String getCognome() {
		return cognome;
	}



	public void setCognome(String cognome) {
		this.cognome = cognome;
	}



	public String getNome() {
		return nome;
	}



	public void setNome(String nome) {
		this.nome = nome;
	}



	public String getEtà() {
		return età;
	}



	public void setEtà(String età) {
		this.età = età;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getMotivoRitorno() {
		return motivoRitorno;
	}



	public void setMotivoRitorno(String motivoRitorno) {
		this.motivoRitorno = motivoRitorno;
	}



	public String getCf() {
		return cf;
	}



	public void setCf(String cf) {
		this.cf = cf;
	}
/*
	public int compareTo (User s) {

	    if (this.cognome.equals(s.cognome)) {
	        return this.nome.compareTo(s.nome);
	    } else {
	        return this.cognome.compareTo(s.cognome);
	    }
	}*/


	@Override
	public int compareTo(User s) {
		if (this.cognome.equals(s.cognome)) {
	        return this.nome.compareTo(s.nome);
	    } else {
	        return this.cognome.compareTo(s.cognome);
	    }
	}
}
