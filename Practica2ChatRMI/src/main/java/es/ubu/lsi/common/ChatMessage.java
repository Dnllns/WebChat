package es.ubu.lsi.common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Message in chat system with RMI.
 * 
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 *
 */
public class ChatMessage implements Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 3568394256872435476L;

	/** Nickname. */
	private String nickname;

	/** Text. */
	private String message;

	/** Client id. */
	private int id;

	private static SimpleDateFormat DATE_MAKER = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

	/**
	 * Constructor.
	 * 
	 * @param id.
	 * @param message.
	 */
	public ChatMessage(int id, String message) {
		this.setId(id);
		this.setMessage(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param id.
	 * @param nickname.
	 * @param message.
	 */
	public ChatMessage(int id, String nickname, String message) {
		this.setNickname(nickname);
	}

	/**
	 * Gets message.
	 * 
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets message.
	 * 
	 * @param message message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets id.
	 * 
	 * @return sender id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets sender id.
	 * 
	 * @param id sender id.
	 * 
	 */
	private void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets nickname.
	 * 
	 * @return the nickname.
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Sets nickname.
	 * 
	 * @param nickname nickname.
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Obtener el objeto formateador de fechas establecido en la clase.
	 * 
	 * @return sdf.
	 */
	public static SimpleDateFormat getSimpleDate() {
		return DATE_MAKER;
	}

	public static String genMsg(String sender, String textData) {

		return "> " + getSimpleDate().format(new Date()) + " | " + sender + ": " + textData;

	}

}