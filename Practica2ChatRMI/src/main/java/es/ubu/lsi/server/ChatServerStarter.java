package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Inicia el proceso de exportación del servidor remoto y su registro en RMI.
 * 
 * @author Daniel Alonso Báscones
 *
 */
public class ChatServerStarter {

	private final int PORT = 1099;


	/**
	 * Constructor de la clase
	 * @throws RemoteException excepcion de RMI
	 */
	public ChatServerStarter() throws RemoteException {
		try {
			Registry registro = LocateRegistry.getRegistry(PORT);
			registro.rebind("/Server", new ChatServerImpl());
		} catch (RemoteException e) {
			System.err.println("Error al crear el servidor\n" + e);
		}
	}

}