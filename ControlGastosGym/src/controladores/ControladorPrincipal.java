/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import ABMs.ABMGastos;
import interfaz.AddCategoriaGui;
import interfaz.AreaGui;
import interfaz.EnviarManualGui;
import interfaz.Impresion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelos.Categoria;
import modelos.Dato;
import modelos.Gasto;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

/**
 *
 * @author alan
 */
public class ControladorPrincipal implements ActionListener {

    private Impresion2 principal;
    private AddCategoriaGui addCat;
    private AreaGui areaGui;
    private DefaultTableModel tablaDefault;
    private JTable tabla;
    private ABMGastos abmGastos;
    
        private File archivoBackup;
    private int selecEnviarBack = 0;
    private Modulo modulo;
    private EnvioEmailControlador envioEmailControlador;
        private CargarDatosEmail emailGui;


    public ControladorPrincipal(Impresion2 p) {
        this.principal = p;
        this.principal.setActionListener(this);
        principal.setVisible(true);
        abmGastos = new ABMGastos();
        
        tabla = principal.getTablaGastos();
        tablaDefault = principal.getTablaMovDefault();
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaMouseClicked(evt);
            }

        });
        principal.getBoxCategoria().addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                principal.getBoxArea().removeAllItems();
                changeValue();
                
            }
        });
    }
    
    private void changeValue(){
        abrirBase();
        Categoria c = Categoria.first("nombre = ?", principal.getBoxCategoria().getSelectedItem());
        LazyList<Dato> datos = Dato.where("categoria_id = ?", c.get("id"));
        Iterator<Dato> i = datos.iterator();
        while(i.hasNext()){
            Dato d = i.next();
            principal.getBoxArea().addItem(d.get("descripcion"));
        }
        cerrarBase();
    }
    
    
   private void tablaMouseClicked(MouseEvent evt) {
       int r = tabla.getSelectedRow();
       Gasto gasto = Gasto.first("id = ?", tablaDefault.getValueAt(r, 5));
       Dato dato = Dato.first("id = ?", gasto.get("dato_id"));
       Categoria categ = Categoria.first("id = ?", dato.get("categoria_id"));
       principal.getBoxCategoria().setSelectedItem(categ.get("nombre"));
       principal.getBoxArea().setSelectedItem(dato.get("descripcion"));
       principal.getBoxTipo().setSelectedItem(tablaDefault.getValueAt(r, 3));
       principal.getTextMonto().setText(tablaDefault.getValueAt(r, 2).toString());
       principal.getFecha().setDate(gasto.getDate("fecha"));
   }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == principal.getBotAgregarCat()) {
            addCat = new AddCategoriaGui(principal, true);
            addCat.setVisible(true);
        }
        if (e.getSource() == principal.getBotGestionarAreas()) {
            areaGui = new AreaGui(principal, true);
            abrirBase();
            LazyList<Dato> areas = Dato.findAll();
            areaGui.getTablaDefault().setRowCount(0);
            if (!areas.isEmpty()) {
                Iterator<Dato> it = areas.iterator();
                while (it.hasNext()) {
                    Dato d = it.next();
                    Object row[] = new Object[3];
                    row[0] = d.getString("descripcion");
                    row[2] = d.getString("ingreso_egreso");
                    Categoria c = Categoria.first("id = ?", d.get("categoria_id"));
                    row[1] = c.getString("nombre");
                    areaGui.getTablaDefault().addRow(row);
                }
            }
            areaGui.setVisible(true);
        }
        if (e.getSource() == principal.getBotNuevo()) {
            Gasto g = new Gasto();
            g.set("monto", principal.getTextMonto().getText().toString());
            //g.set("fecha", principal.getFecha().getCalendar().getTime());
            //d.set("descrip", principal.getTextDescripcion().getText());
            Dato d = Dato.first("descripcion = ?", principal.getBoxArea().getSelectedItem());
            g.set("dato_id", d.get("id"));
            abrirBase();
            if(abmGastos.Alta(g)){
                JOptionPane.showMessageDialog(principal,"Movimiento registrado exitosamente.");
            }else{
                JOptionPane.showMessageDialog(principal,"Ocurrio un error, no se pudo registrar el movimiento.");
            }
            principal.getTablaMovDefault().setRowCount(0);
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
                principal.getTablaMovDefault().addRow(row);
            }
        }

        
     
        
        if (principal.getCambiosEmail() == e.getSource()) {
            emailGui = new CargarDatosEmail(principal, true);
            emailGui.setLocationRelativeTo(principal);
            emailGui.setVisible(true);
        }
        
        if (principal.getEnviar() == e.getSource()) {

            JFileChooser chooser = new JFileChooser();
            chooser.setApproveButtonText("Enviar");
            chooser.addChoosableFileFilter(new Modulo.SQLFilter());
            chooser.showOpenDialog(null);
            if (chooser.getSelectedFile() != null) {
                archivoBackup = chooser.getSelectedFile();
                selecEnviarBack = 1;
            } else if (chooser.getSelectedFile() == null) {
                selecEnviarBack = 0;
            }
            if (selecEnviarBack == 1) {
                envioEmailControlador = new EnvioEmailControlador();
                EnviarManualGui enviarGui = new EnviarManualGui(principal, true, archivoBackup.getAbsolutePath());
                enviarGui.setLocationRelativeTo(principal);
                enviarGui.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No se seleccionó ningun archivo!");
            }

        }
        
            if (e.getSource() == principal.getCrearBackup()) {
            modulo = new Modulo();
            modulo.conectar();
            modulo.GuardarRutaBackup();
            modulo.CrearBackup();
            String dir = (new File(System.getProperty("user.dir")).getAbsolutePath());
            System.out.println(dir);
        }
        if (e.getSource() == principal.getCargarBackup()) {
            int confirmado = JOptionPane.showConfirmDialog(null, "¿Confirmas la restauración de la Base de Datos?");
            if (JOptionPane.OK_OPTION == confirmado) {
                modulo = new Modulo();
                modulo.conectarMySQL();
                modulo.AbrirRutaBackup();
                try {
                    modulo.RestaurarBackup();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ControladorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //final de restaurar backup//
        }
    }

    public void abrirBase() {
        if (!Base.hasConnection()) {
            Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/gestionGym", "root", "root");
        }
    }

    private void cerrarBase() {
        if (Base.hasConnection()) {
            Base.close();
        }
    }
}
