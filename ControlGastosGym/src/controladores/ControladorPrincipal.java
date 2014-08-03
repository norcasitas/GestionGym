/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import ABMs.ABMGastos;
import interfaz.AddCategoriaGui;
import interfaz.AreaGui;
import interfaz.Impresion2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
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
        Categoria c = Categoria.first("nombre = ?", principal.getBoxCategoria().getSelectedItem());
        LazyList<Dato> datos = Dato.where("categoria_id = ?", c.get("id"));
        Iterator<Dato> i = datos.iterator();
        while(i.hasNext()){
            Dato d = i.next();
            principal.getBoxArea().addItem(d.get("descripcion"));
        }
    }
    
    
   private void tablaMouseClicked(MouseEvent evt) {
       int r = tabla.getSelectedRow();
       Gasto gasto = Gasto.first("id = ?", tablaDefault.getValueAt(r, 6));
       Dato dato = Dato.first("id = ?", gasto.get("dato_id"));
       Categoria categ = Categoria.first("id = ?", dato.get("categoria_id"));
       principal.getBoxCategoria().setSelectedItem(categ.get("nombre"));
       principal.getBoxArea().setSelectedItem(dato.get("descripcion"));
       principal.getBoxTipo().setSelectedItem(tablaDefault.getValueAt(r, 4));
       principal.getTextMonto().setText(tablaDefault.getValueAt(r, 3).toString());
       //principal.getFecha().set
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