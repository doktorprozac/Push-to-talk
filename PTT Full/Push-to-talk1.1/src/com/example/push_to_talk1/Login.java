package com.example.push_to_talk1;

public class Login {
	
	private String login;
	private String password;
	
	public Login(String login, String password) {
		setLogin(login);
		setPassword(password);
	}

	public Login() {
		this("", "");
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login)
	{
		this.login = login;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}	
}
