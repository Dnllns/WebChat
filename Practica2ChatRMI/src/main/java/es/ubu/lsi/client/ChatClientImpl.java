package es.ubu.lsi.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import es.ubu.lsi.common.ChatMessage;

/**
 * 
 * Implementacion del cliente de un chat usando la tecnologia RMI
 * @author Daniel Alonso Báscones
 *
 */
public class ChatClientImpl extends UnicastRemoteObject implements ChatClient {

	private static final long serialVersionUID = 1L;
	private String nickname;
	private String hostname;
	private int id;
	private int port;
	private static SimpleDateFormat DATE_MAKER = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
	private static String mensajeSalida = "EXIT";
	private HashSet<Integer> banList;

	/**
	 * Constructor
	 * @param nickname el nombre del usuario
	 * @param port el puerto usado
	 * @param hostname el nombre del host
	 * @throws RemoteException excepcion RMI
	 */
	public ChatClientImpl(String nickname, int port, String hostname) throws RemoteException {
		super();
		setNickName(nickname);
		setPort(port);
		setHostname(hostname);
		banList = new HashSet<Integer>();
		System.out.println("> Cliente inicializado correctamente");

	}

	@Override
	public int getId() throws RemoteException {
		return this.id;
	}

	@Override
	public void setId(int id) throws RemoteException {
		this.id = id;

	}

	@Override
	public void receive(ChatMessage msg) throws RemoteException {

		if (!msg.getMessage().equals(mensajeSalida))
			System.out.print(msg.getMessage() + "\n> ");
		else {
			System.out.println("Apagando cliente");
			System.exit(0);
		}

	}

	@Override
	public String getNickName() throws RemoteException {
		return this.nickname;
	}


	@Override
	public void setNickName(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Obtener el hostname asociado al cliente.
	 * 
	 * @return hostname. el nombre del host
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Establecer el hostname del cliente.
	 * 
	 * @param hostname. el nombre del host
	 */
	private void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Obtener el puerto de conexión.
	 * 
	 * @return port. el puerto de conexion
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Establecer el puerto para un cliente.
	 * 
	 * @param port. el puerto de conexion
	 */
	private void setPort(int port) {
		this.port = port;
	}

	/**
	 * Obtener el objeto formateador de fechas establecido en la clase.
	 * 
	 * @return DATE_MAKER
	 */
	public static SimpleDateFormat getSimpleDatef() {
		return DATE_MAKER;
	}

	/**
	 * Obtiene la lista de baneos
	 * @return banList un set con los usuarios baneados
	 */
	public HashSet<Integer> getBanList() {
		return banList;
	}

	/**
	 * Establece la lista de baneos
	 * @param banList un set con los usuarios baneados
	 */
	public void setBanList(HashSet<Integer> banList) {
		this.banList = banList;
	}

}