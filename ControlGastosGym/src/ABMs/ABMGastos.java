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
        Gasto g = Gasto.create("dato_id",gasto.get("dato_id"),"fecha", gasto.get("fecha"), "monto", gasto.get("monto"));
        g.saveIt();
        Base.commitTransaction();
        return true;
    }
    
}
