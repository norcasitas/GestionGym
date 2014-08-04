/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ABMs;

import java.util.Arrays;
import modelos.Usuario;
import org.javalite.activejdbc.Base;

/**
 *
 * @author jacinto
 */
public class ManejoUsuario {

    public void crearUsuario() {
        if (Usuario.findAll().isEmpty()) {
            Usuario.createIt();
        }
    }

    public void modificarNombre(String nombre) {
        Usuario u = Usuario.findById(1);
        Base.openTransaction();
        u.set("nombre", nombre);
        u.save();
        Base.commitTransaction();
    }

    public void modificarPass(String pass) {
        Usuario u = Usuario.findById(1);
        Base.openTransaction();
        u.set("pass", pass);
        u.save();
        Base.commitTransaction();
    }

    public boolean modificarDatos(String user, String pass) {
        Usuario u = Usuario.findById(1);
        Base.openTransaction();
        u.set("nombre", user, "pass", pass);
        boolean ret = u.save();
        Base.commitTransaction();
        return ret;
    }

    public boolean login(String user, char[] pass) {
        Usuario u = Usuario.first("nombre = ?", user);
        if (u != null) {
            char[] correct = u.getString("pass").toCharArray();
            if (user.equals(u.getString("nombre")) && Arrays.equals(pass, correct)) {
                return true;
            }
        }
        return false;
    }
}
