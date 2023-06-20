package sharing;

import java.rmi.Naming;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class authentificationView extends Application{

	private static final String DB_URL = "jdbc:mysql://localhost:3306/sharing";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    

    
	 public void start(Stage primaryStage) {
	        primaryStage.setTitle("Authentification");

	        // Créer les éléments de l'interface
	        Label usernameLabel = new Label("Nom d'utilisateur:");
	        TextField usernameField = new TextField();
	        Label passwordLabel = new Label("Mot de passe:");
	        PasswordField passwordField = new PasswordField();
	        Button loginButton = new Button("Se connecter");
	        Button registerButton = new Button("S'inscrire");
	        
	     // Créer la grille pour organiser les éléments
	        GridPane gridPane = new GridPane();
	        gridPane.setPadding(new Insets(10));
	        gridPane.setVgap(8);
	        gridPane.setHgap(10);
	        gridPane.add(usernameLabel, 0, 0);
	        gridPane.add(usernameField, 1, 0);
	        gridPane.add(passwordLabel, 0, 1);
	        gridPane.add(passwordField, 1, 1);
	        gridPane.add(loginButton, 0, 2);
	        gridPane.add(registerButton, 1, 2);
	        
	     // Définir l'action du bouton de connexion
	        loginButton.setOnAction(e -> {
	            String username = usernameField.getText();
	            String password = passwordField.getText();
	            // Effectuer les vérifications d'authentification
	            boolean authenticated = authenticateUser(username, password);
	            if (authenticated) {
	            	 try {
	            		 client c= new client(username);
	            		 c.addClient((Iclient)c);
	            		 
	            		 sharingView view=new sharingView(c);
	            		 view.start(primaryStage);
	            		 
					} catch (Exception e1) {e1.printStackTrace();}
	                showNotification("Authentification réussie.");
	            } else {
	                showError("Nom d'utilisateur ou mot de passe incorrect.");
	            }
	        });
	        
	     // Définir l'action du bouton d'enregistrement
	        registerButton.setOnAction(e -> {
	        	primaryStage.hide();
	            openRegistrationWindow(primaryStage);
	        });
	        
	        // Créer la scène et afficher la fenêtre
	        Scene scene = new Scene(gridPane, 500, 200);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	    }
	 
	 private void openRegistrationWindow(Stage primaryStage) {
	        Stage registrationStage = new Stage();
	        registrationStage.setTitle("Inscription");

	        // Créer les éléments de la fenêtre d'inscription
	        Label usernameLabel = new Label("Nom d'utilisateur:");
	        TextField usernameField = new TextField();
	        Label passwordLabel = new Label("Mot de passe:");
	        PasswordField passwordField = new PasswordField();
	        Button registerButton = new Button("S'inscrire");
	        
	     // Créer la liste déroulante des rôles
	        ComboBox roleComboBox = new ComboBox<>();
	        Label roleLabel = new Label("Rôle : ");
	        roleComboBox.getItems().addAll("Lecture seule", "Modification", "Administrateur");
	        roleComboBox.setOnAction(e -> {
	            String selectedRole = (String) roleComboBox.getValue();
	            roleLabel.setText("Rôle : " + selectedRole);
	        });
	        
	        // Créer la grille pour organiser les éléments
	        GridPane gridPane = new GridPane();
	        gridPane.setPadding(new Insets(10));
	        gridPane.setVgap(8);
	        gridPane.setHgap(10);
	        gridPane.add(usernameLabel, 0, 0);
	        gridPane.add(usernameField, 1, 0);
	        gridPane.add(passwordLabel, 0, 1);
	        gridPane.add(passwordField, 1, 1);
	        gridPane.add(roleComboBox, 1, 2, 2, 1);
	        gridPane.add(roleLabel, 0, 2);
	        gridPane.add(registerButton, 0, 5, 3, 1);
	        
	        
	        
	     // Définir l'action du bouton d'enregistrement
	        registerButton.setOnAction(e -> {
	            String username = usernameField.getText();
	            String password = passwordField.getText();
	            String role = (String) roleComboBox.getValue();
	            // Effectuer l'enregistrement du compte
	            boolean success = createUserAccount(username, password,role);
	            if (success) {
	                registrationStage.close();
	                showNotification("Compte créé avec succès.");
	                primaryStage.show();
	            } else {
	                showError("Erreur lors de la création du compte.");
	            }
	        });
	        // Créer la scène de la fenêtre d'inscription et l'afficher
	        Scene registrationScene = new Scene(gridPane, 400, 250);
	        registrationStage.setScene(registrationScene);
	        registrationStage.show();

	 }
	 private boolean authenticateUser(String username, String password) {
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
	             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE name = ? AND password = ?")) {

	            statement.setString(1, username);
	            statement.setString(2, password);

	            ResultSet resultSet = statement.executeQuery();
	            return resultSet.next();
	        } catch (Exception e) {
	            showError("Erreur lors de l'authentification : " + e.getMessage());
	            return false;
	        }
	    }
	 
	 private boolean createUserAccount(String username, String password,String role) {
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
	             PreparedStatement statement = connection.prepareStatement("INSERT INTO user (name, password,role) VALUES (?, ? ,?)")) {

	            statement.setString(1, username);
	            statement.setString(2, password);
	            statement.setString(3, role);

	            int rowsAffected = statement.executeUpdate();
	            return rowsAffected > 0;
	        } catch (Exception e) {
	            showError("Erreur lors de la création du compte : " + e.getMessage());
	            return false;
	        }
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

	    public static void main(String[] args) {
	        launch(args);
	    }
	 
}

