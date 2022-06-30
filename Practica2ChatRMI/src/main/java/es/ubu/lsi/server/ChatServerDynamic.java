package es.ubu.lsi.server;

import java.rmi.server.RMIClassLoader;

/**
 * Dynamic server. 
 * 
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 * @author Daniel Alonso Báscones
 */

public class ChatServerDynamic {

	public static void main(String args[]) {

		try {

			if (System.getSecurityManager() == null)
				System.setSecurityManager(new SecurityManager());

			// Leer el codebase
			String url = System.getProperties().getProperty("java.rmi.server.codebase");
			// Cargador de clases, instanciamos el objeto ChatServerStarter
			Class<?> serverClass = RMIClassLoader.loadClass(url, "es.ubu.lsi.server.ChatServerStarter");
			serverClass.newInstance();

			System.out.println("> Inicializando servidor...");
			System.out.println("> Codebase url: " + url);

		} catch (Exception e) {
			System.err.println("> Excepcion en arranque del servidor:\n" + e.toString());
		}
	}

} // ServidorDinamico