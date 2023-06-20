package Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sharing.Iclient;
import sharing.client;



public class server extends UnicastRemoteObject implements Iserver{
	static private ArrayList<Iclient> clients;
	private Map<String, byte[]> fileStorage;
	private static final String DB_URL = "jdbc:mysql://localhost:3306/sharing";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
	
	public server() throws RemoteException{
		super();
		clients= new ArrayList<>();
		fileStorage = new HashMap<>();
	}
	
	public String echo(String str) throws RemoteException{
		System.out.println(" server's response "+str);
		return "Le serveur Repond : "+str;
	}
	
	@Override
	public void addClient(Iclient c) throws RemoteException {
	        clients.add(c);

	}

	public static void main(String[] args) throws RemoteException, MalformedURLException {
		LocateRegistry.createRegistry(1099);
		server od =new server();
		Naming.rebind("rmi://localhost/irisi", od);
	}

	@Override
	public String[] listFiles(String username) throws RemoteException {
          return fileStorage.keySet().toArray(new String[0]);
            }
	
	@Override
	public boolean uploadFile(String username, List<String> fileNames, List<byte[]> fileDataList) throws RemoteException {
		String autorisation=autorisationUser(username);
		if(!autorisation.equalsIgnoreCase("Lecture seule")) {
			if (fileNames.size() != fileDataList.size()) {
		        return false; // Les listes de noms de fichiers et de données de fichiers doivent avoir la même taille
		    }
			for (int i = 0; i < fileNames.size(); i++) {
		        String fileName = fileNames.get(i);
		        byte[] fileData = fileDataList.get(i);
		        fileStorage.put(fileName, fileData);
		    }
			return true;
		}else return false;
	
		}
	
	public byte[] downloadFile(String fileName) throws RemoteException {
      
            return fileStorage.get(fileName);

    }
	
	public String[] searchFiles(String query) throws RemoteException {

           return fileStorage.keySet().stream()
                   .filter(fileName -> fileName.contains(query))
                   .toArray(String[]::new);

	}
	
	public String autorisationUser(String username) {
		 try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
	             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE name = ? ")) {
			     statement.setString(1, username);
	            ResultSet resultSet = statement.executeQuery();
	            resultSet.next();
	            return resultSet.getNString("role");
	        } catch (Exception e) {
	            return null;
	        }
	}

	@Override
	public String[] deleteFile(String username,String selectedFile) throws RemoteException {
		String autorisation=autorisationUser(username);
		if(!autorisation.equalsIgnoreCase("Lecture seule")) {
		fileStorage.remove(selectedFile);
		return fileStorage.keySet().toArray(new String[0]);}
		else
		return null;
		
	}

}
