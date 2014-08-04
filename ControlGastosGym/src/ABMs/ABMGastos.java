/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ABMs;

import modelos.Gasto;
import org.javalite.activejdbc.Base;

/**
 *
 * @author alan
 */
public class ABMGastos {
    
    public boolean Alta(Gasto gasto){
        Base.openTransaction();
        Gasto g = Gasto.create("descrip", gasto.get("descrip"),"dato_id",gasto.get("dato_id"),"fecha", gasto.get("fecha"), "monto", gasto.get("monto"));
        g.saveIt();
        Base.commitTransaction();
        return true;
    }
    
    public boolean Modificar(Gasto gasto){
        Base.openTransaction();
        gasto.saveIt();
        Base.commitTransaction();
        return true;
    }
    
    public boolean Baja(Gasto g){
        Base.openTransaction();
        g.delete();
        Base.commitTransaction();
        return true;
    }
    
    
}
