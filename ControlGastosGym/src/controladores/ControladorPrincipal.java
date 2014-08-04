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
import java.util.Calendar;
import java.util.Date;
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
    private boolean nuevoApretado = false;
    private boolean modApretado = false;
    private File archivoBackup;
    private int selecEnviarBack = 0;
    private Modulo modulo;
    private EnvioEmailControlador envioEmailControlador;
    private CargarDatosEmail emailGui;
    private boolean nuevoAreaApretado = false;
    private boolean modAreaApretado = false;
    private String nombreArea;

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
        principal.getBoxCategoria().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                principal.getBoxArea().removeAllItems();
                changeValue();

            }
        });
    }

    private void changeValue() {
        abrirBase();
        Categoria c = Categoria.first("nombre = ?", principal.getBoxCategoria().getSelectedItem());
        LazyList<Dato> datos = Dato.where("categoria_id = ?", c.get("id"));
        Iterator<Dato> i = datos.iterator();
        while (i.hasNext()) {
            Dato d = i.next();
            principal.getBoxArea().addItem(d.get("descripcion"));
        }
        cerrarBase();
    }

    private void tablaMouseClicked(MouseEvent evt) {
        int r = tabla.getSelectedRow();
        abrirBase();
        Gasto gasto = Gasto.first("id = ?", tablaDefault.getValueAt(r, 5));
        Dato dato = Dato.first("id = ?", gasto.get("dato_id"));
        Categoria categ = Categoria.first("id = ?", dato.get("categoria_id"));
        principal.getBoxCategoria().setSelectedItem(categ.get("nombre"));
        principal.getBoxArea().setSelectedItem(dato.get("descripcion"));
        principal.getBoxTipo().setSelectedItem(tablaDefault.getValueAt(r, 3));
        principal.getTextMonto().setText(tablaDefault.getValueAt(r, 2).toString());
        principal.getFecha().setDate(gasto.getDate("fecha"));
        principal.getDesc().setText(gasto.getString("descrip"));
        principal.getBotMod().setEnabled(true);
        principal.getBotEliminar().setEnabled(true);
    }

    private void tablaMouseClickedArea(MouseEvent evt) {
        int r = areaGui.getTablaAreas().getSelectedRow();
        areaGui.getTextNombre().setText(areaGui.getTablaDefault().getValueAt(r, 0).toString());
        areaGui.getBoxCategoria().setSelectedItem(areaGui.getTablaDefault().getValueAt(r, 1).toString());
        areaGui.getBoxTipo().setSelectedItem(areaGui.getTablaDefault().getValueAt(r, 2).toString());
        areaGui.getBotModificar().setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == principal.getBotAgregarCat()) {
            addCat = new AddCategoriaGui(principal, true);
            this.addCat.setActionListener(this);
            addCat.setVisible(true);
        }
        if (e.getSource() == principal.getBotGestionarAreas()) {
            areaGui = new AreaGui(principal, true);
            areaGui.setActionListener(this);
            areaGui.getTableAreas().addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    tablaMouseClickedArea(evt);
                }
            });
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
            LazyList<Categoria> cats = Categoria.findAll();
            Iterator<Categoria> i = cats.iterator();
            while (i.hasNext()) {
                Categoria c = i.next();
                areaGui.getBoxCategoria().addItem(c.get("nombre"));
            }
            areaGui.setVisible(true);
        }
        if (e.getSource() == principal.getBotNuevo()) {
            if (!nuevoApretado && !modApretado) {
                principal.BotonesNuevo(false);
                principal.BloquearCampos(true);
                principal.getBotMod().setEnabled(false);
                principal.getBotEliminar().setEnabled(true);
                nuevoApretado = true;
                modApretado = false;
                principal.getBoxCategoria().setSelectedIndex(0);
                principal.getBoxArea().setSelectedIndex(0);
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                date.setDate(cal.getActualMinimum(Calendar.DAY_OF_MONTH));;
                principal.getFecha().setDate(date);
                principal.getTextMonto().setText("");
                principal.getDesc().setText("");
            } else {
                if (nuevoApretado) {
                    Gasto g = new Gasto();
                    abrirBase();
                    g.set("monto", principal.getTextMonto().getText().toString());
                    if (principal.getFecha().getCalendar() != null) {
                        g.set("fecha", dateToMySQLDate(principal.getFecha().getCalendar().getTime(), false));
                    }
                    g.set("descrip", principal.getDesc().getText());
                    Dato d = Dato.first("descripcion = ?", principal.getBoxArea().getSelectedItem());
                    g.set("dato_id", d.get("id"));
                    if (principal.getTextMonto().getText().equals("")) {
                        JOptionPane.showMessageDialog(principal, "Monto invalido.");
                    } else {
                        if (abmGastos.Alta(g)) {
                            JOptionPane.showMessageDialog(principal, "Movimiento registrado exitosamente.");
                            principal.BotonesNuevo(true);
                            principal.BloquearCampos(false);
                            principal.getBotMod().setEnabled(false);
                            principal.getBotEliminar().setEnabled(false);
                        } else {
                            JOptionPane.showMessageDialog(principal, "Ocurrio un error, no se pudo registrar el movimiento.");
                        }
                        principal.getTablaMovDefault().setRowCount(0);
                        LazyList gastos = Gasto.findAll();
                        Iterator<Gasto> it = gastos.iterator();
                        while (it.hasNext()) {
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
                        nuevoApretado = false;
                    }
                }
            }
            if (modApretado) {
                if (principal.getTextMonto().getText().equals("")) {
                    JOptionPane.showMessageDialog(principal, "Monto invalido.");
                } else {
                    int ret = JOptionPane.showConfirmDialog(principal, "¿Desea modificar el movimiento?", null, JOptionPane.YES_NO_OPTION);
                    if (ret == JOptionPane.YES_OPTION) {
                        int r = principal.getTablaGastos().getSelectedRow();
                        abrirBase();
                        Gasto g = Gasto.first("id = ?", principal.getTablaMovDefault().getValueAt(r, 5));
                        g.set("monto", principal.getTextMonto().getText().toString());
                        if (principal.getFecha().getCalendar() != null) {
                            g.set("fecha", dateToMySQLDate(principal.getFecha().getCalendar().getTime(), false));
                        }
                        g.set("descrip", principal.getDesc().getText());
                        Dato d = Dato.first("descripcion = ?", principal.getBoxArea().getSelectedItem());
                        g.set("dato_id", d.get("id"));
                        if (abmGastos.Modificar(g)) {
                            JOptionPane.showMessageDialog(principal, "Movimiento modificado exitosamente.");
                            principal.BotonesNuevo(true);
                            principal.BloquearCampos(false);
                            principal.getBotMod().setEnabled(false);
                            principal.getBotEliminar().setEnabled(false);
                            principal.getTablaMovDefault().setRowCount(0);
                            LazyList gastos = Gasto.findAll();
                            Iterator<Gasto> it = gastos.iterator();
                            while (it.hasNext()) {
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
                        } else {
                            JOptionPane.showMessageDialog(principal, "Ocurrio un error, no se pudo modificar el movimiento.");
                        }
                    }
                }
            }
        }
        if (e.getSource() == principal.getBotEliminar()) {
            if (nuevoApretado || modApretado) {
                principal.BloquearCampos(false);
                principal.BotonesNuevo(true);
                principal.getBotEliminar().setEnabled(false);
                principal.getBotMod().setEnabled(false);
                principal.getBotNuevo().setEnabled(true);
                principal.getBoxCategoria().setSelectedIndex(0);
                principal.getBoxArea().setSelectedIndex(0);
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                date.setDate(cal.getActualMinimum(Calendar.DAY_OF_MONTH));;
                principal.getFecha().setDate(date);
                principal.getTextMonto().setText("");
                principal.getDesc().setText("");
                nuevoApretado = false;
                modApretado = false;
            } else {
                int ret = JOptionPane.showConfirmDialog(principal, "¿Desea eliminar el movimiento?", null, JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    int r = principal.getTablaGastos().getSelectedRow();
                    abrirBase();
                    Gasto d = Gasto.first("id = ?", principal.getTablaMovDefault().getValueAt(r, 5));
                    if (d != null) {
                        if (abmGastos.Baja(d)) {
                            JOptionPane.showMessageDialog(principal, "Movimiento eliminado exitosamente.");
                            principal.getTablaMovDefault().setRowCount(0);
                            LazyList gastos = Gasto.findAll();
                            Iterator<Gasto> it = gastos.iterator();
                            while (it.hasNext()) {
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
                        } else {
                            JOptionPane.showMessageDialog(principal, "Ocurrio un error intente nuevamente.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(principal, "Ocurrio un error intente nuevamente.");
                    }

                }
                principal.getBotEliminar().setEnabled(false);
                principal.getBotMod().setEnabled(false);
                principal.getBoxCategoria().setSelectedIndex(0);
                principal.getBoxArea().setSelectedIndex(0);
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                date.setDate(cal.getActualMinimum(Calendar.DAY_OF_MONTH));;
                principal.getFecha().setDate(date);
                principal.getTextMonto().setText("");
                principal.getDesc().setText("");
            }

        }

        if (e.getSource() == principal.getBotMod()) {
            principal.BloquearCampos(true);
            principal.BotonesNuevo(false);
            principal.getBotNuevo().setEnabled(true);
            principal.getBotMod().setEnabled(false);
            principal.getBotEliminar().setEnabled(true);
            modApretado = true;
            nuevoApretado = false;
        }

        ///categoria gui controlador
        if (addCat != null) {
            if (e.getSource() == addCat.getjButton1()) { //boton agregar
                int ret = JOptionPane.showConfirmDialog(principal, "¿Desea agregar la categoria " + addCat.getjTextField1().getText() + "?", null, JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    Categoria c = new Categoria();
                    c.set("nombre", addCat.getjTextField1().getText().toUpperCase());
                    c.saveIt();
                    c = Categoria.first("nombre = ?", addCat.getjTextField1().getText().toUpperCase());
                    if (c != null) {
                        JOptionPane.showMessageDialog(principal, "Categoria creada exitosamente.");
                        addCat.dispose();
                        LazyList<Categoria> cats = Categoria.findAll();
                        Iterator<Categoria> i = cats.iterator();
                        while (i.hasNext()) {
                            Categoria ca = i.next();
                            principal.getBoxCategoria().addItem(ca.get("nombre"));
                        }
                    } else {
                        JOptionPane.showMessageDialog(principal, "Ocurrio un error intente nuevamente.");
                    }
                }
            }
        }
        ////
        ///AreaGui CONTROLADOR
        if (areaGui != null) {
            if (e.getSource() == areaGui.getBotNuevo()) {
                if (!nuevoAreaApretado && !modAreaApretado) {
                    areaGui.getTextNombre().setText("");
                    areaGui.getBoxCategoria().setSelectedIndex(0);
                    areaGui.getBoxTipo().setSelectedIndex(0);
                    areaGui.getTextNombre().setEnabled(true);
                    areaGui.getBoxCategoria().setEnabled(true);
                    areaGui.getBoxTipo().setEnabled(true);
                    areaGui.getBotModificar().setText("Cancelar");
                    areaGui.getBotModificar().setEnabled(true);
                    areaGui.getBotNuevo().setText("Guardar");
                    nuevoAreaApretado = true;
                    modAreaApretado = false;
                } else {
                    if(nuevoAreaApretado){
                    int ret = JOptionPane.showConfirmDialog(principal, "¿Desea agregar el servicio " + areaGui.getTextNombre().getText() + "?", null, JOptionPane.YES_NO_OPTION);
                    if (ret == JOptionPane.YES_OPTION) {
                        Dato d = new Dato();
                        d.set("descripcion", areaGui.getTextNombre().getText());
                        d.set("ingreso_egreso", areaGui.getBoxTipo().getSelectedItem());
                        abrirBase();
                        Categoria c = Categoria.first("nombre = ?", areaGui.getBoxCategoria().getSelectedItem());
                        d.set("categoria_id", c.get("id"));
                        d.saveIt();
                        Dato dato = new Dato();
                        dato = Dato.first("descripcion = ?", areaGui.getTextNombre().getText());
                        if (dato != null) {
                            JOptionPane.showMessageDialog(principal, "Servicio creado exitosamente.");
                            LazyList<Dato> areas = Dato.findAll();
                            areaGui.getTablaDefault().setRowCount(0);
                            if (!areas.isEmpty()) {
                                Iterator<Dato> it = areas.iterator();
                                while (it.hasNext()) {
                                    Dato dat = it.next();
                                    Object row[] = new Object[3];
                                    row[0] = dat.getString("descripcion");
                                    row[2] = dat.getString("ingreso_egreso");
                                    Categoria ca = Categoria.first("id = ?", dat.get("categoria_id"));
                                    row[1] = ca.getString("nombre");
                                    areaGui.getTablaDefault().addRow(row);
                                }
                                areaGui.getTextNombre().setEnabled(false);
                                areaGui.getBoxCategoria().setEnabled(false);
                                areaGui.getBoxTipo().setEnabled(false);
                                areaGui.getBotModificar().setText("Modificar");
                                areaGui.getBotNuevo().setText("Nuevo");
                                areaGui.getBotModificar().setEnabled(false);
                                nuevoAreaApretado = false;
                                modAreaApretado = false;
                            }

                        } else {
                            JOptionPane.showMessageDialog(principal, "Ocurrio un error intente nuevamente.");
                        }
                    }
                }
            
                if (modAreaApretado) {
                    if (areaGui.getTextNombre().getText().equals("")) {
                        JOptionPane.showMessageDialog(principal, "Servicio invalido.");
                    } else {
                       // Dato dato = Dato.first("descripcion = ?", areaGui.getTextNombre().getText());
                       // if (dato == null) {
                            Dato d = Dato.first("descripcion = ?", nombreArea);
                            d.set("descripcion", areaGui.getTextNombre().getText());
                            d.set("ingreso_egreso", areaGui.getBoxTipo().getSelectedItem());
                            Categoria ca = Categoria.first("nombre = ?", areaGui.getBoxCategoria().getSelectedItem());
                            d.set("categoria_id", ca.get("id"));
                            d.saveIt();
                            JOptionPane.showMessageDialog(principal, "Servicio modificado exitosamente.");
                            LazyList<Dato> areas = Dato.findAll();
                            Iterator<Dato> it = areas.iterator();
                            areaGui.getTablaDefault().setRowCount(0);
                            while (it.hasNext()) {
                                Dato dat = it.next();
                                Object row[] = new Object[3];
                                row[0] = dat.getString("descripcion");
                                row[2] = dat.getString("ingreso_egreso");
                                Categoria cat = Categoria.first("id = ?", dat.get("categoria_id"));
                                row[1] = cat.getString("nombre");
                                areaGui.getTablaDefault().addRow(row);
                            }
                            areaGui.getBotModificar().setEnabled(false);
                            areaGui.getBoxCategoria().setEnabled(false);
                            areaGui.getBoxTipo().setEnabled(false);
                            areaGui.getTextNombre().setEnabled(false);
                            areaGui.getBotModificar().setText("Modificar");
                            areaGui.getBotNuevo().setText("Nuevo");
                            modAreaApretado = false;
                       // } else {
                         //   JOptionPane.showMessageDialog(principal, "Servicio ya existente.");
                        //}
                    }

                }
                }
            }
            if (e.getSource() == areaGui.getBotModificar()) {
                if (nuevoAreaApretado || modAreaApretado) {
                    areaGui.getTextNombre().setText("");
                    areaGui.getBoxCategoria().setSelectedIndex(0);
                    areaGui.getBoxTipo().setSelectedIndex(0);
                    areaGui.getTextNombre().setEnabled(false);
                    areaGui.getBoxCategoria().setEnabled(false);
                    areaGui.getBoxTipo().setEnabled(false);
                    areaGui.getBotModificar().setText("Modificar");
                    areaGui.getBotModificar().setEnabled(false);
                    areaGui.getBotNuevo().setText("Nuevo");
                    areaGui.getBotNuevo().setEnabled(true);
                    nuevoAreaApretado = false;
                    modAreaApretado = false;
                } else {
                    areaGui.getTextNombre().setEnabled(true);
                    areaGui.getBoxCategoria().setEnabled(true);
                    areaGui.getBoxTipo().setEnabled(true);
                    areaGui.getBotModificar().setText("Cancelar");
                    areaGui.getBotNuevo().setText("Guardar");
                    areaGui.getBotModificar().setEnabled(true);
                    areaGui.getBotNuevo().setEnabled(true);
                    modAreaApretado = true;
                    nuevoAreaApretado = false;
                    nombreArea = areaGui.getTextNombre().getText();
                }
            }
        }
        ////

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

    public String dateToMySQLDate(Date fecha, boolean paraMostrar) {
        if (fecha != null) {
            if (paraMostrar) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                return sdf.format(fecha);
            } else {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(fecha);
            }
        } else {
            return "";
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
