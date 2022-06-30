package es.ubu.lsi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.server.ChatServer;

/**
 * Cliente del objeto remoto.
 * 
 * @author Daniel Alonso Báscones
 *
 */
public class ChatClientStarter {

	private final int PORT = 1099;
	private final String HOSTNAME = "localhost";

	/**
	 * Constructor para la inicialización de la exportación del cliente remoto y la
	 * resolución en el servidor remoto. Recibe las entradas del cliente y actua en
	 * consecuencia llamndo a los métodos del servidor.
	 * 
	 * @param args (nickname, hostname).
	 */
	public ChatClientStarter(String[] args) {

		// Obtener nickname (OBLIGATORIO)
		if (args.length < 1 || args.length > 2) {
			System.err.println("> Uso: <nick name> <host name>");
		}

		else {

			String nickname = null;
			String hostname = null;

			// Obtener el nickname
			nickname = args[0];

			// Obtener el host
			if (args.length == 2) {
				hostname = args[1];
			} else {
				hostname = HOSTNAME;
			}

			// Declaracion de variables remotas
			Registry registry = null;
			ChatClient client = null;
			int clientId = -1;
			ChatServer servidor = null;

			// Instanciar los objetos remotos
			try {

				// Crear objeto cliente
				client = new ChatClientImpl(nickname, PORT, hostname);
				// Obtener el registro
				registry = LocateRegistry.getRegistry(hostname, PORT);
				// Obtener el servidor
				servidor = (ChatServer) registry.lookup("/Server");
				// Registrar al cliente
				clientId = servidor.checkIn(client);
				client.setId(clientId);

			} catch (RemoteException | NotBoundException e1) {
				e1.printStackTrace();
				finalizarEjecucion();
			}

			// Escuchar teclado
			boolean isKeyboardReady = true;
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			ChatMessage message;
			String userInput = "";

			// Prompt
			// El hilo se queda siempre a la espera de recibir mensajes por teclado
			while (isKeyboardReady == true) {

				// Lectura por teclado
				boolean statuss = false;
				try {
					statuss = stdIn.ready();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (statuss) {

					try {
						userInput = stdIn.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.out.print("> ");

					try {
						switch (userInput) {

						case "logout":
							servidor.logout(client);
							isKeyboardReady = false;
							break;

						case "shutdown":
							servidor.shutdown(client);
							isKeyboardReady = false;
							break;


						default:

							message = new ChatMessage(client.getId(), userInput);
							message.setNickname(nickname);
							servidor.publish(message);

						}
					} catch (RemoteException e) {
						e.printStackTrace();
						finalizarEjecucion();
					}
				}
			}

			finalizarEjecucion();
		}

	}

	private void finalizarEjecucion() {
		System.out.print("> Apagando el cliente...");
		System.exit(0);
	}
}