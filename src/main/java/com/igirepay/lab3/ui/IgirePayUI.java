package com.igirepay.lab3.ui;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab1.model.Transaction;
import com.igirepay.lab2.db.SchemaSetup;
import com.igirepay.lab3.service.AccountService;
import com.igirepay.lab3.service.CustomerService;
import com.igirepay.lab3.service.TransactionService;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class IgirePayUI extends Application {
    private static final String PINK="#E91E8C",PINK_LIGHT="#FCE4F3",PINK_DARK="#C2185B",WHITE="#FFFFFF",RED="#D32F2F";
    private final CustomerService cSvc=new CustomerService();
    private final AccountService aSvc=new AccountService();
    private final TransactionService tSvc=new TransactionService();
    private Stage stage;
    private Customer me;

    @Override public void start(Stage s){stage=s;SchemaSetup.createTables();loginScreen();}
    public static void main(String[] args){launch(args);}

    private void loginScreen(){
        TextField phone=new TextField();phone.setPromptText("Phone number");
        PasswordField pin=new PasswordField();pin.setPromptText("5-digit PIN");
        Label err=new Label();err.setStyle("-fx-text-fill:"+RED+";");
        VBox root=vbox();
        root.getChildren().addAll(big("IgirePay"),lbl("Phone Number"),phone,lbl("PIN"),pin,err,btn("Login",()->{
            Customer c=cSvc.login(phone.getText().trim(),pin.getText().trim());
            if(c!=null){me=c;dashboardScreen();}else err.setText("Invalid phone or PIN.");
        }),outline("No account? Register",()->registerScreen()));
        show(root,420,400,"Login");
    }

    private void registerScreen(){
        TextField name=new TextField();name.setPromptText("Full name");
        TextField email=new TextField();email.setPromptText("Email");
        TextField phone=new TextField();phone.setPromptText("Phone number");
        PasswordField pin=new PasswordField();pin.setPromptText("5-digit PIN");
        Label err=new Label();err.setStyle("-fx-text-fill:"+RED+";");
        VBox root=vbox();
        root.getChildren().addAll(big("Create Account"),lbl("Full Name"),name,lbl("Email"),email,lbl("Phone"),phone,lbl("5-digit PIN (numbers only)"),pin,err,btn("Register",()->{
            String p=pin.getText().trim();
            if(!p.matches("\\d{5}"))err.setText("PIN must be 5 numeric digits.");
            else if(cSvc.register(name.getText().trim(),email.getText().trim(),phone.getText().trim(),p)){alert("Registered! Please login.");loginScreen();}
            else err.setText("Failed. Phone or email may exist.");
        }),outline("Back to Login",()->loginScreen()));
        show(root,420,510,"Register");
    }

    private void dashboardScreen(){
        Label nameLabel=new Label("👋 "+me.getFullName());nameLabel.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:"+PINK_DARK+";");
        Button logoutBtn=new Button("Logout");logoutBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:"+RED+";-fx-border-color:"+RED+";");logoutBtn.setOnAction(e->{me=null;loginScreen();});
        HBox top=new HBox(nameLabel,logoutBtn);HBox.setHgrow(nameLabel,Priority.ALWAYS);top.setAlignment(Pos.CENTER_LEFT);
        VBox cards=new VBox(6);
        List<Account> accounts=aSvc.getAccountsByCustomer(me.getId());
        if(accounts.isEmpty())cards.getChildren().add(small("No accounts yet."));
        else for(Account a:accounts){
            Label t=new Label((a.getAccountType().equals("WALLET")?" ":" ")+a.getAccountType()+" #"+a.getId());t.setStyle("-fx-font-weight:bold;-fx-text-fill:"+PINK_DARK+";");
            Label b=new Label("RWF "+String.format("%,.2f",a.getBalance()));b.setStyle("-fx-text-fill:"+PINK+";-fx-font-size:14px;-fx-font-weight:bold;");
            HBox card=new HBox(12,t,b);card.setPadding(new Insets(10));card.setStyle("-fx-background-color:"+WHITE+";-fx-background-radius:8;-fx-border-color:"+PINK+";");
            cards.getChildren().add(card);
        }
        VBox root=vbox();
        root.getChildren().addAll(top,new Separator(),small("MY ACCOUNTS"),cards,new Separator(),
                btn("Create Account",()->createAccountScreen()),btn("Deposit Money",()->txScreen("DEPOSIT")),
                btn("Withdraw Money",()->txScreen("WITHDRAW")),btn("Transfer Money",()->txScreen("TRANSFER")),
                btn("Transaction History",()->historyScreen()),outline("Change PIN",()->changePinScreen()));
        show(root,420,640,"Dashboard");
    }

    private void createAccountScreen(){
        ToggleGroup g=new ToggleGroup();
        RadioButton w=radio("Wallet — instant, no fees",g,true);
        RadioButton sv=radio("Savings — 2% fee, min 500 RWF",g,false);
        Label err=new Label();err.setStyle("-fx-text-fill:"+RED+";");
        VBox root=vbox();
        root.getChildren().addAll(big("Create Account"),lbl("Select Account Type"),w,sv,err,btn("Create",()->{
            boolean ok=w.isSelected()?aSvc.createWallet(me.getId()):aSvc.createSavings(me.getId());
            if(ok){alert("Account created!");dashboardScreen();}else err.setText("Failed.");
        }),outline("Back",()->dashboardScreen()));
        show(root,420,320,"Create Account");
    }

    private void txScreen(String mode){
        ComboBox<String> from=accountCombo();
        TextField amount=new TextField();amount.setPromptText("Amount in RWF");
        TextField toPhone=new TextField();toPhone.setPromptText("Recipient phone");
        Label err=new Label();err.setStyle("-fx-text-fill:"+RED+";");
        String title=mode.equals("DEPOSIT")?"Deposit":mode.equals("WITHDRAW")?"Withdraw":"Transfer";
        VBox root=vbox();
        root.getChildren().addAll(big(title),lbl("From Account"),from);
        if(mode.equals("TRANSFER"))root.getChildren().addAll(lbl("Recipient Phone"),toPhone);
        root.getChildren().addAll(lbl("Amount (RWF)"),amount,err,btn("Confirm",()->{
            try{
                if(from.getValue()==null){err.setText("Select account.");return;}
                double amt=Double.parseDouble(amount.getText().trim());
                String refId=tSvc.generateRefId();
                int id=Integer.parseInt(from.getValue().split("#")[1].split("—")[0].trim());
                boolean ok=mode.equals("DEPOSIT")?tSvc.deposit(id,amt,refId):mode.equals("WITHDRAW")?tSvc.withdraw(id,amt,refId):tSvc.transferByPhone(id,toPhone.getText().trim(),amt,refId);
                if(ok){alert(mode+" of RWF "+String.format("%,.2f",amt)+" done.\nRef: "+refId);dashboardScreen();}
                else err.setText("Failed.");
            }catch(Exception ex){err.setText(ex.getMessage());}
        }),outline("Back",()->dashboardScreen()));
        int h=mode.equals("TRANSFER")?460:390;
        show(root,420,h,title);
    }

    private void historyScreen(){
        ComboBox<String> combo=accountCombo();
        TableView<Transaction> table=new TableView<>();table.setPrefHeight(260);

        table.getColumns().clear();

        TableColumn<Transaction,String> col1=new TableColumn<>("Type");
        col1.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getTransactionType()));
        col1.setPrefWidth(80);

        TableColumn<Transaction,String> col2=new TableColumn<>("RWF");
        col2.setCellValueFactory(d->new SimpleStringProperty(String.format("%,.2f",d.getValue().getAmount())));
        col2.setPrefWidth(100);

        TableColumn<Transaction,String> col3=new TableColumn<>("Reference");
        col3.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getReferenceId()));
        col3.setPrefWidth(120);

        TableColumn<Transaction,String> col4=new TableColumn<>("Date");
        col4.setCellValueFactory(d->new SimpleStringProperty(d.getValue().getCreatedAt()!=null?d.getValue().getCreatedAt().toString().replace("T"," "):"—"));
        col4.setPrefWidth(150);

        table.getColumns().addAll(col1,col2,col3,col4);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox root=vbox();
        root.getChildren().addAll(big("History"),lbl("Select Account"),combo,btn("Load",()->{
            if(combo.getValue()!=null){
                int id=Integer.parseInt(combo.getValue().split("#")[1].split("—")[0].trim());
                table.setItems(FXCollections.observableArrayList(tSvc.getHistory(id)));
            }
        }),table,outline("Back",()->dashboardScreen()));
        show(root,520,550,"History");
    }
    private void changePinScreen(){
        PasswordField old=new PasswordField();old.setPromptText("Current PIN");
        PasswordField nw=new PasswordField();nw.setPromptText("New 5-digit PIN");
        PasswordField conf=new PasswordField();conf.setPromptText("Confirm PIN");
        Label err=new Label();err.setStyle("-fx-text-fill:"+RED+";");
        VBox root=vbox();
        root.getChildren().addAll(big("Change PIN"),lbl("Current PIN"),old,lbl("New PIN"),nw,lbl("Confirm PIN"),conf,err,btn("Save",()->{
            if(!nw.getText().equals(conf.getText()))err.setText("PINs don't match.");
            else if(!nw.getText().matches("\\d{5}"))err.setText("PIN must be 5 digits.");
            else if(cSvc.changePin(me.getId(),old.getText(),nw.getText())){alert("PIN changed!");dashboardScreen();}
            else err.setText("Current PIN incorrect.");
        }),outline("Back",()->dashboardScreen()));
        show(root,420,400,"Change PIN");
    }


    private VBox vbox(){VBox b=new VBox(10);b.setPadding(new Insets(26));b.setStyle("-fx-background-color:"+PINK_LIGHT+";");return b;}
    private void show(VBox root,int w,int h,String title){stage.setScene(new Scene(root,w,h));stage.setTitle("IgirePay "+title);stage.show();}
    private Label big(String t){Label l=new Label(t);l.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:"+PINK_DARK+";");return l;}
    private Label small(String t){Label l=new Label(t);l.setStyle("-fx-font-size:12px;-fx-text-fill:"+PINK+";");return l;}
    private Label lbl(String t){Label l=new Label(t);l.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:"+PINK_DARK+";");return l;}
    private Button btn(String text,Runnable action){Button b=new Button(text);b.setMaxWidth(Double.MAX_VALUE);b.setStyle("-fx-background-color:"+PINK+";-fx-text-fill:"+WHITE+";");b.setOnAction(e->action.run());return b;}
    private Button outline(String text,Runnable action){Button b=new Button(text);b.setMaxWidth(Double.MAX_VALUE);b.setStyle("-fx-background-color:"+WHITE+";-fx-border-color:"+PINK+";-fx-text-fill:"+PINK+";");b.setOnAction(e->action.run());return b;}
    private RadioButton radio(String text,ToggleGroup g,boolean selected){RadioButton r=new RadioButton(text);r.setToggleGroup(g);r.setSelected(selected);r.setStyle("-fx-font-size:13px;-fx-text-fill:"+PINK_DARK+";");return r;}
    private ComboBox<String> accountCombo(){ComboBox<String> c=new ComboBox<>();for(Account a:aSvc.getAccountsByCustomer(me.getId()))c.getItems().add(a.getAccountType()+" #"+a.getId()+" — RWF "+String.format("%,.2f",a.getBalance()));if(!c.getItems().isEmpty())c.getSelectionModel().selectFirst();return c;}
    private void alert(String msg){Alert a=new Alert(Alert.AlertType.INFORMATION);a.setHeaderText(null);a.setContentText(msg);a.showAndWait();}
}