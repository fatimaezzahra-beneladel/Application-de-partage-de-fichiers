package sharing;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Iclient extends Remote{

	public void addClient(Iclient c) throws RemoteException;

	public String getUsername()throws RemoteException;
	
	public boolean uploadFile(List<File> selectedFiles)throws RemoteException;

}
