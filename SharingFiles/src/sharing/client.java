package sharing;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Server.Iserver;
import Server.server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class client extends UnicastRemoteObject implements Iclient{
	public Iserver od;
	private String username;
	
	private ObservableList<String> fileList = FXCollections.observableArrayList();
	
	public client(String username) throws RemoteException{
		remote();
		this.setUsername(username);
	}
	
	public void remote() throws RemoteException{
		String url="rmi://localhost/irisi";
		try {
			 od=(Iserver) Naming.lookup(url);
			System.out.println(od.echo("Message du Client "));
		}catch(Exception e) {
			System.out.println("Serveur introuvable");
		}
	}	
	
	
	public void addClient(Iclient c) throws RemoteException {
		od.addClient(c);
		
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getUsername() throws RemoteException{
		return username;
	}

	public boolean uploadFile(List<File> files)throws RemoteException {
		try {
	        List<String> fileNames = new ArrayList<>();
	        List<byte[]> fileDataList = new ArrayList<>();

	        for (File file : files) {
	            String fileName = file.getName();
	            byte[] fileData = Files.readAllBytes(file.toPath());

	            fileNames.add(fileName);
	            fileDataList.add(fileData);
	        }
	        System.out.println("upload client");
	        boolean uploaded=od.uploadFile(this.username,fileNames, fileDataList);
	        if (uploaded) {System.out.println("upload client done ");return true;}
	        else return false;
	    } catch (Exception e) {return false; }
		
		
	}
	
	public ObservableList<String> updateFileList() throws RemoteException {
		String[] files=od.listFiles(username);
        fileList.clear();
        fileList.addAll(Arrays.asList(files));
        return fileList;
      
	}

	public void searchFiles(String query) {
		  try {
	             String[] searchResults = od.searchFiles(query);
	            fileList.clear();
	            fileList.addAll(Arrays.asList(searchResults));
	        } catch (Exception e) {
	        }
		
	}

	public boolean downloadFile(String fileName) {
		 try {
	            // Appeler la méthode distante pour télécharger le fichier
	            byte[] fileData = od.downloadFile(fileName);
	            // Code pour enregistrer le fichier localement
	            if (fileData != null) {
	            FileOutputStream fos = new FileOutputStream("C:\\Users\\fatimzzahra\\Desktop\\"+ fileName);
	            fos.write(fileData);
	            fos.close(); return true;
	        } else {
	        	return false; }
	    } catch (Exception e) { return false;}
		
		
		
	}

	public ObservableList<String> deleteFile(String selectedFile) throws RemoteException {
		String[] files=od.deleteFile(username,selectedFile);
		if(files!=null) {
			fileList.clear();
	        fileList.addAll(Arrays.asList(files));
	        return fileList;
		}else
		return null;
	
	}
}

