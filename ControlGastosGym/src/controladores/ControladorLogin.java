/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import ABMs.ManejoUsuario;
import interfaz.Impresion2;
import interfaz.LoginGui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import modelos.Categoria;
import modelos.Dato;
import modelos.Gasto;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

/**
 *
 * @author alan
 */
public class ControladorLogin implements ActionListener{
      private LoginGui login;
      private ControladorPrincipal principalControl;
      private Impresion2 principalGui;
      
          private String user;
    private char[] pass;
    private ManejoUsuario mu;
    public ControladorLogin() {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme("Default");
            UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        login = new LoginGui();
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        principalGui = new Impresion2();
         principalGui.setExtendedState(JFrame.MAXIMIZED_BOTH);
            principalControl = new ControladorPrincipal(principalGui);
        
        this.login.setActionListener(this);
         mu = new ManejoUsuario();
        abrirBase();
        mu.crearUsuario();
        login.getUser().requestFocus();
        login.getPass().addKeyListener(new KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    login.getPass().setText("");
                    login.getPass().requestFocus();
                }
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    user = login.getUser().getText();
                    pass = login.getPass().getPassword();
                    abrirBase();
                    if (mu.login(user, pass)) {
          
            abrirBase();
            LazyList<Categoria> cats = Categoria.findAll();
            Iterator<Categoria> i = cats.iterator();
            while(i.hasNext()){
                Categoria c = i.next();
                principalGui.getBoxCategoria().addItem(c.get("nombre"));
            }
            principalGui.getTablaMovDefault().setRowCount(0);
            principalGui.cargarGastos();
            principalGui.setVisible(true);
            login.dispose();
                        EmailThread emailThread = new EmailThread();
                        emailThread.run();
                    } else {
                        login.getPass().setText("");
                        JOptionPane.showMessageDialog(principalGui, "INTENTEee NUEVAMENTE", "¡DATOS INCORRECTOS!", JOptionPane.ERROR_MESSAGE);
                    }
                    
                }
            }
        });
    }
   
    
    public static void main(String[] args) throws InterruptedException, Exception {
            abrirBase();
            ControladorLogin app = new ControladorLogin();
        

    }
public static void abrirBase() {
        if (!Base.hasConnection()) {
            Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/gestionGym", "root", "root");
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login.getBotEntrar()) {
             abrirBase();
           if (mu.login(user, pass)) {
           
            LazyList<Categoria> cats = Categoria.findAll();
            Iterator<Categoria> i = cats.iterator();
            while(i.hasNext()){
                Categoria c = i.next();
                principalGui.getBoxCategoria().addItem(c.get("nombre"));
            }
            principalGui.getTablaMovDefault().setRowCount(0);
            principalGui.cargarGastos();
            principalGui.setVisible(true);
            login.dispose();
                        EmailThread emailThread = new EmailThread();
                        emailThread.run();
           }else {
                        login.getPass().setText("");
                        JOptionPane.showMessageDialog(principalGui, "INTENTE NUEVAMENTE", "¡DATOS INCORRECTOS!", JOptionPane.ERROR_MESSAGE);
                    }
        }
    }
    
}
