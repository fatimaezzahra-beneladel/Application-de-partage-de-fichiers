package sharing;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import Server.server;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class sharingView extends Application{
	 private server fileSharingServer;
	 private ObservableList<String> fileList = FXCollections.observableArrayList();
	 private client c;
	 ListView<String> listView;

	 public sharingView(client c) {
		this.c=c;
	    listView = new ListView<>();
	}

	public void start(Stage primaryStage) throws Exception {
		 
		
	        primaryStage.setTitle("Application de partage de fichiers");
	        VBox root = new VBox(10);
	        HBox rootH = new HBox(10);
	        root.setPadding(new Insets(10));
	        rootH.setPadding(new Insets(10));
	        
	        Label scenetitle = new Label("Bonjour "+c.getUsername()+"!");
	     // Liste des fichiers
	        
	        listView.setItems(fileList);
	        
	     // Champ de recherche
	        TextField searchField = new TextField();
	        searchField.setPromptText("Rechercher un fichier");
	        searchField.setPrefWidth(500);

	        // Bouton de recherche
	        Button searchButton = new Button("Rechercher");
	        searchButton.setOnAction(event -> {
	            String query = searchField.getText();
	            if (!query.isEmpty()) {
	                c.searchFiles(query);
	            } else {
	                try {
						updateFileList();
						} catch (RemoteException e) {e.printStackTrace();}
				}
	        });
	        
	        
	     // Bouton de téléchargement
	        Button downloadButton = new Button("Download File");
	        downloadButton.setOnAction(event -> {
	            String selectedFile = listView.getSelectionModel().getSelectedItem();
	            if (selectedFile != null) {
	                if(c.downloadFile(selectedFile)) {
	                	showNotification("Le fichier a été téléchargé avec succès : " + selectedFile);
	                }else showError("Le fichier demandé n'existe pas : " + selectedFile);
	                
	            }
	        });
	        
	        
	        // Bouton de suppression
	        Button deleteButton = new Button("Delete File");
	        deleteButton.setOnAction(event -> {
	            String selectedFile = listView.getSelectionModel().getSelectedItem();
	            if (selectedFile != null) {
	               try {
					deleteFile(selectedFile);
				} catch (RemoteException e) {e.printStackTrace();}
	            }
	        });
	        
	        
	        // Bouton de téléversement
	        Button uploadButton = new Button("Upload File");
	        uploadButton.setOnAction(event -> {
	            FileChooser fileChooser = new FileChooser();
	            fileChooser.setTitle("Sélectionner des fichiers");
	            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

	            if (selectedFiles != null && !selectedFiles.isEmpty()) {
	                try {
						if(c.uploadFile(selectedFiles)) {
							showNotification("File Uploaded");
						}else showError("Vous n'avez pas le droit de télécharger le fichier ou une erreur systéme");
						updateFileList();
					} catch (RemoteException e) {e.printStackTrace();}
	            }
	            
	        });
	        rootH.getChildren().addAll(searchField, searchButton);
	        root.getChildren().addAll(scenetitle,listView, rootH,  downloadButton, uploadButton,deleteButton);

	     // Affichage de la fenêtre
	        primaryStage.setScene(new Scene(root, 700, 600));
	        primaryStage.show();
	        
	     // Mise à jour de la liste des fichiers
	        updateFileList();
	        
		}
	private void deleteFile(String selectedFile) throws RemoteException {
		fileList=c.deleteFile(selectedFile);
		if(fileList==null) {
			System.out.println("impossi");
			showError("Impossible de supprimer le fichier : " + selectedFile);
		}else {
			System.out.println("done");
			showNotification("Le fichier a été supprimé avec succès : " + selectedFile);
		listView.setItems(fileList);
		}
	}

	public void updateFileList() throws RemoteException {
		fileList=c.updateFileList();
		listView.setItems(fileList);
	}
	 private void showNotification(String message) {
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Notification");
	        alert.setHeaderText(null);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }
	
	 private void showError(String message) {
	        Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Erreur");
	        alert.setHeaderText(null);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }
}

