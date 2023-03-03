package pack;

public class Riga {

	public String cognome,nome,luogoNascita,username,dataNascita,cf;
	public int campiExtra;
	public Riga(String username,String nome,String cognome,String cf,String dataNascita,String luogoNascita, int campiExtra) {
		this.cognome=cognome;
		this.nome=nome;
		this.username=username;
		this.cf=cf;
		this.luogoNascita=luogoNascita;
		this.dataNascita=dataNascita;
		this.campiExtra=campiExtra;
		
	}

	public int getCampiExtra() {
		return campiExtra;
	}

	public void setCampiExtra(int campiExtra) {
		this.campiExtra = campiExtra;
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

	public String getLuogoNascita() {
		return luogoNascita;
	}

	public void setLuogoNascita(String luogoNascita) {
		this.luogoNascita = luogoNascita;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

}
