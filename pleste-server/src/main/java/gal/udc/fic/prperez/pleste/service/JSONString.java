package gal.udc.fic.prperez.pleste.service;

//This is just a wrapper class for solving https://github.com/dropwizard/dropwizard/issues/231
public class JSONString {
	private String content;

	public JSONString() {}

	public JSONString(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String toString() {
		return content;
	}
}
