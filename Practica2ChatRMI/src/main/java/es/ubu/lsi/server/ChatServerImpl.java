package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import es.ubu.lsi.client.ChatClient;
import es.ubu.lsi.common.ChatMessage;

/**
 * 
 * @author Daniel Alonso Báscones
 *
 */
public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

	//Declaracion de variables
	private static final long serialVersionUID = 1L;
	private static int SERVER_ID = 0;
	private static int CLIENT_ID = 1;
	private static String EXIT_KEY = "EXIT";
	private final static String SERVER_NAME = "Servidor";
	private Map<Integer, User> userList;

	/**
	 * Constructor.
	 * 
	 * @throws RemoteException excepcion.
	 */
	public ChatServerImpl() throws RemoteException {
		super();
		setUserList(new HashMap<Integer, User>());
		System.out.println("> " + ChatMessage.getSimpleDate().format(new Date()) + " | Servidor registrado.");
	}

	@SuppressWarnings("finally")
	@Override
	public int checkIn(ChatClient client) {
		int clientId = getNextClientId();
		try {

			// Registrar un nuevo usuario
			getUserList().put(clientId, new User(clientId, client));
			publish(new ChatMessage(clientId, "Sesion iniciada"));

			String messageData = "> " + ChatMessage.getSimpleDate().format(new Date())
					+ " | Servidor: Bienvenido a la sala de chat " + client.getNickName();

			// Enviar el mensaje
			client.receive(new ChatMessage(clientId, messageData));

		} catch (RemoteException e) {
			System.err.print(ChatMessage.getSimpleDate().format(new Date())
					+ " | Error al insertar un cliente de la lista de clientes.\n> ");
		} finally {
			return clientId;
		}
	}

	@Override
	public void logout(ChatClient client) {
		try {
			// Actualizar el registro
			int id = client.getId();
			String nickname = client.getNickName();

			if (getUserList().containsKey(id)) {

				publish(new ChatMessage(id, "El usuario " + nickname + " ha salido de la sesión"));
				getUserList().remove(id);
				client.receive(new ChatMessage(id, ChatMessage.getSimpleDate().format(new Date())
						+ " | Servidor: adios " + nickname + ". Se procede a desconectarte del chat"));

			} else {
				System.err.println(ChatMessage.getSimpleDate().format(new Date())
						+ " | Error al hacer logout para un cliente. El cliente no esta registrado");
			}
		} catch (RemoteException e) {
			System.err.print(ChatMessage.getSimpleDate().format(new Date())
					+ " | Error al eliminar un cliente de la lista de clientes");
		}
	}

	@Override
	public void shutdown(ChatClient client) {

		try {

			publish(new ChatMessage(getServerId(), "El usuario " + client.getNickName() + " ha cerrado de la sesión"));
			publish(new ChatMessage(getServerId(), EXIT_KEY));
			System.out.print(genMsg(SERVER_NAME, "Apagando el servidor"));
			System.exit(0);

		} catch (RemoteException e) {

			System.err.print(genMsg(SERVER_NAME, "Error en la finalizacón del servidor"));
		}
	}

	@Override
	public void publish(ChatMessage msg) {

		int senderId = msg.getId();
		User senderUser = this.getUserList().get(senderId);
		String senderNick = senderUser.getNickName();
		String data = msg.getMessage();
		String textMessage;

		// Evaluar baneo
		String banMsg = setBanUnban(senderUser, data);

		// y obtener usuario

		if (senderId != 0) {

			// Salida en el servidor
			textMessage = ChatMessage.getSimpleDate().format(new Date()) + " | " + senderNick + ": " + msg.getMessage();
			System.out.println("> " + textMessage);

		} else {

			if (!msg.getMessage().equals(EXIT_KEY)) {
				textMessage = ChatMessage.getSimpleDate().format(new Date()) + " | " + SERVER_NAME + ": " + msg.getMessage();
				System.out.println("> " + textMessage);
			} else {
				textMessage = EXIT_KEY;
			}
		}

		ChatMessage message = new ChatMessage(senderId, textMessage);

		for (Entry<Integer, User> entrada : getUserList().entrySet()) {

			ChatClient cliente = entrada.getValue().getChatClient();
			String receiverNickName = entrada.getValue().getNickName();
			HashSet<String> senderUserBanList = senderUser.getBanList();
			
			// Notificar al baneador de su baneo
			if (entrada.getKey() == senderId && entrada.getKey() != 0) {
				if (!banMsg.equals("")) {
					try {
						cliente.receive(new ChatMessage(senderId, banMsg));
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}

			// Enviar el mensaje a todos los usuarios menos el y su baneos
			if (entrada.getKey() != senderId && !isBan(receiverNickName, senderUserBanList)) {
				
					
				try {
					if(banMsg.equals("")) {
						cliente.receive(message);
					}
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	
	/**
	 * Obtiene un string formateado como mensaje
	 * @param sender
	 * @param textData
	 * @return
	 */
	private String genMsg(String sender, String textData) {

		return "> " + ChatMessage.getSimpleDate().format(new Date()) + " | " + sender + ": " + textData;

	}
	
	/**
	 * Obtiene si un usuario esta baneado
	 * @param receiverNickName
	 * @param senderUserBanList
	 * @return
	 */
	private boolean isBan(String receiverNickName, HashSet<String> senderUserBanList) {
		Iterator<String> it = senderUserBanList.iterator();
		while (it.hasNext()) {
			String s = (String) it.next();
			if (s.equals(receiverNickName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Establece un baneo o unbaneo
	 * @param senderUser
	 * @param data
	 * @return
	 */
	private String setBanUnban(User senderUser, String data) {

		String banCommand = "";
		String bannedUser = "";
		String msg = "";
		try {
			banCommand = data.substring(0, data.indexOf(" "));
			bannedUser = data.substring(data.indexOf(" ") + 1, data.length());
		} catch (Exception e) {
		}

		// Check for ban
		if (banCommand.equals("ban")) {

			senderUser.getBanList().add(bannedUser);
			msg = ChatMessage.getSimpleDate().format(new Date()) + " | " + SERVER_NAME +  ": Se ha baneado al usuario " + bannedUser;
			System.out.println(">" + msg);


		}
		// Unban
		else if (banCommand.equals("unban")) {

			senderUser.getBanList().remove(bannedUser);
			msg = ChatMessage.getSimpleDate().format(new Date()) + " | " + SERVER_NAME +  ": Se ha unbaneado al usuario " + bannedUser;
			System.out.println(">" + msg);

		}

		return msg;

	}

	/**
	 * Obtener el id del servidor.
	 * 
	 * @return clientId.
	 */
	public static int getServerId() {
		return SERVER_ID;
	}

	/**
	 * Obtener el id de cliente actual establecido en la clase.
	 * 
	 * @return clientId.
	 */
	public static int getClientId() {
		return CLIENT_ID;
	}

	/**
	 * Obtener el siguiente id de cliente consecutivo establecido en la clase.
	 * 
	 * @return clientId.
	 */
	public static int getNextClientId() {
		return CLIENT_ID++;
	}

	/**
	 * Obtener el conjunto de usuarios registrados.
	 * 
	 * @return userRegistration.
	 */
	public Map<Integer, User> getUserList() {
		return userList;
	}

	/**
	 * Obtener el conjunto de usuarios registrados.
	 * 
	 */
	private void setUserList(Map<Integer, User> userlist) {
		userList = userlist;
	}

	/**
	 * Clase interna para almacenar los datos de los usuarios
	 * 
	 * @author Daniel Alonso Báscones
	 *
	 */
	class User {

		private int id;
		private String nickName;
		private Date dateConect;
		private ChatClient chatClient;
		private HashSet<String> banList;

		/**
		 * Constructor.
		 * 
		 * @param id.
		 * @param chatClient.
		 */
		public User(int id, ChatClient chatClient) {
			setId(id);
			setChatClient(chatClient);
			setDateConect(new Date());
			banList = new HashSet<String>();
			try {
				setNickName(chatClient.getNickName());
			} catch (RemoteException e) {
				System.err.print("Error al crear el User: " + e.toString());
			}
		}

		/**
		 * Obtener el id del usuario
		 * 
		 * @return id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Establecer la id del user.
		 * 
		 * @param id
		 */
		private void setId(int id) {
			this.id = id;
		}

		/**
		 * Obtener el nickname del user.
		 * 
		 * @return nickname.
		 */
		public String getNickName() {
			return nickName;
		}

		/**
		 * Establecer el nickname del user.
		 * 
		 * @param nickName
		 */
		private void setNickName(String nickName) {
			this.nickName = nickName;
		}

		/**
		 * Obtener la fecha de conexión del user.
		 * 
		 * @return fecha.
		 */
		public Date getDateConect() {
			return dateConect;
		}

		/**
		 * Establecer la fecha de conexion del user.
		 * 
		 * @param dateConect.
		 */
		private void setDateConect(Date dateConect) {
			this.dateConect = dateConect;
		}

		/**
		 * Obtener el chatclient del user.
		 * 
		 * @return chatClient.
		 */
		public ChatClient getChatClient() {
			return chatClient;
		}

		/**
		 * Establecer el chatclient de un user.
		 * 
		 * @param chatClient.
		 */
		private void setChatClient(ChatClient chatClient) {
			this.chatClient = chatClient;
		}

		/**
		 * Obtiene la lista de baneos
		 * 
		 * @returnlista de baneos
		 */
		public HashSet<String> getBanList() {
			return banList;
		}

		/**
		 * Establece la lista de baneos
		 * 
		 * @param banList la lista de baneos
		 */
		public void setBanList(HashSet<String> banList) {
			this.banList = banList;
		}

	}


}