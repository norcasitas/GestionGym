/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaz;

import controladores.ControladorJReport;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelos.Categoria;
import modelos.Dato;
import modelos.Gasto;
import net.sf.jasperreports.engine.JRException;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

/**
 *
 * @author nico
 */
public class Impresion extends javax.swing.JDialog {

    DefaultTableModel tablaMovDefault;
        ControladorJReport reporte ;

    /**
     * Creates new form Impresion
     */
    public Impresion(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        tablaMovDefault = (DefaultTableModel) tablaGastos.getModel();

        abrirBase();
        LazyList<Categoria> categoriasBase = Categoria.findAll();
        Iterator<Categoria> it = categoriasBase.iterator();
        categorias.addItem("Todos");
        while (it.hasNext()) {
            Categoria cate = it.next();
            categorias.addItem(cate.get("nombre"));
        }
        categorias.setSelectedItem("Todos");
        cerrarBase();
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        date.setDate(cal.getActualMinimum(Calendar.DAY_OF_MONTH));;
        desde.setDate(date);
        Date dateH = cal.getTime();
        dateH.setDate(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        hasta.setDate(dateH);
        try {
            reporte = new ControladorJReport(("contadora.jasper"));
        } catch (JRException ex) {
            Logger.getLogger(Impresion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Impresion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Impresion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desde = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        hasta = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaGastos = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        categorias = new javax.swing.JComboBox();
        cargar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        total = new javax.swing.JLabel();
        label13 = new javax.swing.JLabel();
        imprimir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Movimientos");

        desde.setToolTipText("Ver movimientos desde la fecha");

        jLabel1.setFont(new java.awt.Font("Century Schoolbook L", 1, 14)); // NOI18N
        jLabel1.setText("Desde");

        jLabel2.setFont(new java.awt.Font("Century Schoolbook L", 1, 14)); // NOI18N
        jLabel2.setText("Hasta");

        hasta.setToolTipText("Ver movimientos hasta la fecha");

        tablaGastos.setAutoCreateRowSorter(true);
        tablaGastos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Categoria", "Descripcion", "Monto", "Tipo de movimiento", "Fecha"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablaGastos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tablaGastos);
        if (tablaGastos.getColumnModel().getColumnCount() > 0) {
            tablaGastos.getColumnModel().getColumn(0).setResizable(false);
            tablaGastos.getColumnModel().getColumn(1).setResizable(false);
            tablaGastos.getColumnModel().getColumn(2).setResizable(false);
            tablaGastos.getColumnModel().getColumn(3).setResizable(false);
            tablaGastos.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel3.setText("Ver movimientos de ");

        categorias.setMaximumRowCount(20);

        cargar.setText("Cargar gastos");
        cargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Century Schoolbook L", 1, 24)); // NOI18N
        jLabel4.setText("TOTAL");

        total.setFont(new java.awt.Font("Century Schoolbook L", 1, 24)); // NOI18N
        total.setForeground(new java.awt.Color(0, 87, 218));

        label13.setFont(new java.awt.Font("Century Schoolbook L", 1, 24)); // NOI18N
        label13.setForeground(new java.awt.Color(0, 87, 218));
        label13.setText("$");

        imprimir.setText("Imprimir movimientos");
        imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(236, 236, 236)
                        .addComponent(jLabel4)
                        .addGap(14, 14, 14)
                        .addComponent(label13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                        .addComponent(imprimir))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(categorias, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cargar))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(desde, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hasta, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(imprimir))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(hasta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(desde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(categorias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cargar))
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarActionPerformed
        abrirBase();
        LazyList<Gasto> gastos = null;
        tablaMovDefault.setRowCount(0);
        LinkedList<Gasto> gastosLinked = new LinkedList<>();
        if (categorias.getSelectedItem().toString().equals("Todos")) {
            gastos = Gasto.where("fecha >= ? and fecha <= ? ", dateToMySQLDate(desde.getDate(), false), dateToMySQLDate(hasta.getDate(), false));
            Iterator<Gasto> it = gastos.iterator();
            Gasto gasto;
            while (it.hasNext()) {
                gasto = it.next();
                Object row[] = new Object[5];
                Dato padre = gasto.parent(Dato.class);
                Categoria papaDato = padre.parent(Categoria.class);
                row[0] = papaDato.getString("nombre");
                row[1] = padre.getString("descripcion");
                row[2] = gasto.getString("monto");
                row[3] = padre.getString("ingreso_egreso");
                    row[4] = dateToMySQLDate(gasto.getDate("fecha"), true);

                tablaMovDefault.addRow(row);
            }
        } else {
            Categoria cat = Categoria.findFirst("nombre =?", categorias.getSelectedItem().toString());
            LazyList<Dato> datos = Dato.where("categoria_id =?", cat.getId());
            Iterator<Dato> itDatos = datos.iterator();
            while (itDatos.hasNext()) {
                Dato dato = itDatos.next();
                gastosLinked.addAll(dato.getAll(Gasto.class));
            }
            Iterator<Gasto> it = gastosLinked.iterator();
            Gasto gasto;
            while (it.hasNext()) {
                gasto = it.next();
                if (gasto.getDate("fecha").after(desde.getDate()) && gasto.getDate("fecha").before(hasta.getDate())) {
                    Object row[] = new Object[5];
                    Dato padre = gasto.parent(Dato.class);
                    Categoria papaDato = padre.parent(Categoria.class);
                    row[0] = papaDato.getString("nombre");
                    row[1] = padre.getString("descripcion");
                    row[2] = gasto.getString("monto");
                    row[3] = padre.getString("ingreso_egreso");
                    row[4] = dateToMySQLDate(gasto.getDate("fecha"), true);

                    tablaMovDefault.addRow(row);
                }
            }

        }
        DecimalFormat df = new DecimalFormat("0.00########");
String result = df.format(sumar());
        total.setText(result);

    }//GEN-LAST:event_cargarActionPerformed

    private void imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imprimirActionPerformed
        try {
            reporte.mostrarReporte(desde.getDate(), hasta.getDate());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Impresion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Impresion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JRException ex) {
            Logger.getLogger(Impresion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_imprimirActionPerformed
    /*va true si se quiere usar para mostrarla por pantalla es decir 12/12/2014 y false si va 
     para la base de datos, es decir 2014/12/12*/

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

    private double sumar(){
        int i =0;
        double ret=0;
        while(i<tablaGastos.getRowCount()){
           ret= ret+Double.valueOf((String)tablaGastos.getValueAt(i, 2));
           i++;
        }
        return ret;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Impresion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Impresion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Impresion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Impresion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Impresion dialog = new Impresion(new javax.swing.JFrame(), false);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cargar;
    private javax.swing.JComboBox categorias;
    private com.toedter.calendar.JDateChooser desde;
    private com.toedter.calendar.JDateChooser hasta;
    private javax.swing.JButton imprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label13;
    private javax.swing.JTable tablaGastos;
    private javax.swing.JLabel total;
    // End of variables declaration//GEN-END:variables
}
