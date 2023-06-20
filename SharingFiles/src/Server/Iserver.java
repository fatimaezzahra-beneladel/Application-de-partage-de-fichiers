package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import sharing.Iclient;
import sharing.client;

public interface Iserver extends Remote{
	public String echo(String str) throws RemoteException;
	public void addClient(Iclient c)throws RemoteException;
	public String[] listFiles(String username) throws RemoteException;
	boolean uploadFile(String username,List<String> fileNames, List<byte[]> fileDataList) throws RemoteException;
	public byte[] downloadFile(String fileName) throws RemoteException;
	public String[] searchFiles(String query)throws RemoteException;
	public String[] deleteFile(String username,String selectedFile)throws RemoteException;
}
