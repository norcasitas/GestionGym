/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import interfaz.Impresion2;
import interfaz.LoginGui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.JFrame;
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
    public ControladorLogin(){
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            com.jtattoo.plaf.mcwin.McWinLookAndFeel.setTheme("Pink");
            UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
        login = new LoginGui();
        login.setVisible(true);
        this.login.setActionListener(this);
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
            principalGui = new Impresion2();
            principalControl = new ControladorPrincipal(principalGui);
            abrirBase();
            LazyList<Categoria> cats = Categoria.findAll();
            Iterator<Categoria> i = cats.iterator();
            while(i.hasNext()){
                Categoria c = i.next();
                principalGui.getBoxCategoria().addItem(c.get("nombre"));
            }
            principalGui.getTablaMovDefault().setRowCount(0);
            abrirBase();
            LazyList gastos = Gasto.findAll();
            Iterator<Gasto> it = gastos.iterator();
            while(it.hasNext()){
                Gasto gasto = it.next();
                Dato dato = Dato.first("id = ?", gasto.get("dato_id"));
                Categoria c = Categoria.first("id = ?", dato.get("categoria_id"));
                Object row[] = new Object[6];
                row[0] = c.get("nombre");
                row[1] = dato.get("descripcion");
                row[2] = gasto.get("monto");
                row[3] = dato.get("ingreso_egreso");
                row[4] = gasto.get("fecha");
                row[5] = gasto.get("id");
                principalGui.getTablaMovDefault().addRow(row);
            }
            principalGui.setVisible(true);
            login.dispose();
                        EmailThread emailThread = new EmailThread();
                        emailThread.run();
        }
    }
    
}
