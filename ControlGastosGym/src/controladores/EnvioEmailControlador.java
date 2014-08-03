/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
import modelos.Email;
import modelos.Envio;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

/**
 *
 * @author nico
 */
public class EnvioEmailControlador {

    public String bd = "gestionGym";
    public static String login = "root";
    public static String password = "root";
    public String url = "jdbc:mysql://localhost/" + bd;
    public String urlcero = "jdbc:mysql://localhost/";
    public Connection conn = null;
    String mail = "";
    String passwo = "";
    public static int conectadoMySQL = 0;
    private Email emailModel;

    public void conectarMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, login, password);
            if (conn != null) {
                conectadoMySQL = 1;
            }
        } catch (SQLException ex) {
            conectadoMySQL = 0;
        } catch (ClassNotFoundException ex) {
            conectadoMySQL = 0;
        }
    }

    public boolean guardarDatos(String email, String pass) throws SQLException {
        emailModel = new Email();
        abrirBase();
        emailModel.set("email", email, "password", pass);
        Base.openTransaction();
        Email.deleteAll();
        boolean guardado = emailModel.saveIt();
        Base.commitTransaction();
        cerrarBase();
        if (guardado) {
            JOptionPane.showMessageDialog(null, "Los datos han sido Guardados Correctamente!");
        } else {
            JOptionPane.showMessageDialog(null, "No se han realizado los cambios, ocurrió un error");
        }
        return guardado;




    }

    public boolean enviarMail(String email, String passw, boolean envio) throws MessagingException {
        boolean ret = false;
        abrirBase();
        LazyList<Email> emailsModel = Email.findAll();

        if (!emailsModel.isEmpty() || !envio) {
            cerrarBase();
            if (envio) {
                abrirBase();
                emailModel = emailsModel.get(0);
                this.mail = emailModel.getString("email");
                String contraEncrip = emailModel.getString("password");
                char arrayD[] = contraEncrip.toCharArray();
                for (int i = 0; i < arrayD.length; i++) {
                    arrayD[i] = (char) (arrayD[i] - (char) 5);
                }
                this.passwo = String.valueOf(arrayD);
                cerrarBase();
            } else {
                this.mail = email;
                this.passwo = passw;
            }
            // se obtiene el objeto Session. La configuración es para
            // una cuenta de gmail.
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.live.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.user", mail);
            props.setProperty("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props, null);
            // session.setDebug(true);

            // Se compone la parte del texto
            BodyPart texto = new MimeBodyPart();
            texto.setText("Texto del mensaje");

            // Se compone el adjunto con la imagen
            BodyPart adjunto = new MimeBodyPart();
            String dir = (new File(System.getProperty("user.dir")).getAbsolutePath());

            adjunto.setDataHandler(
                    new DataHandler(new FileDataSource(dir + "/backupEmail.sql")));
            adjunto.setFileName("backupGestionDeGastos.sql");

            // Una MultiParte para agrupar texto e imagen.
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            multiParte.addBodyPart(adjunto);

            // Se compone el correo, dando to, from, subject y el
            // contenido.
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail));
            message.addRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(mail));
            message.setSubject("BackUp mensual Gestion Costa Norte");
            message.setContent(multiParte);

            // Se envia el correo.
            Transport t = session.getTransport("smtp");
            try {
                t.connect(mail, passwo);
            } catch (javax.mail.AuthenticationFailedException ex) {
                JOptionPane.showMessageDialog(null, "¡Datos incorrectos, no se ha establecido la conexión!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            ret = t.isConnected();
            if (envio) {
                try {
                    t.sendMessage(message, message.getAllRecipients());
                } catch (javax.mail.MessagingException ex) {
                    ret = false;

                }
            }
            if (envio && ret) {
                abrirBase();
                Envio enviarModel = new Envio();
                Base.openTransaction();
                Envio.deleteAll();
                enviarModel.set("fecha", convertirFechaString());
                enviarModel.setBoolean("enviado", true);
                enviarModel.saveIt();
                Base.commitTransaction();
                cerrarBase();
            }
            t.close();
        } else {
            JOptionPane.showMessageDialog(null, "No hay cofiguración de email guardada, por favor cargue los datos para habilitar el backup mensual");
        }
        return ret;

    }

    private void abrirBase() {
        if (!Base.hasConnection()) {
            try{             Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/"+bd, password, login);             }catch(Exception e){                 JOptionPane.showMessageDialog(null, "Ocurrió un error, no se realizó la conexión con el servidor, verifique la conexión \n "+e.getMessage(),null,JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void cerrarBase() {
        if (Base.hasConnection()) {
            Base.close();
        }
    }

    public static String convertirFechaString() {
        String fechaString;
        Calendar fechaCalendar = Calendar.getInstance();
        String anio = Integer.toString(fechaCalendar.get(Calendar.YEAR));
        String mes = Integer.toString(fechaCalendar.get(Calendar.MONTH) + 1);
        String dia = Integer.toString(fechaCalendar.get(Calendar.DAY_OF_MONTH));
        fechaString = anio + "/" + mes + "/" + dia;
        return fechaString;
    }

    public boolean enviarMailManual(String email, String passw, String dir, String para) throws MessagingException {
        boolean ret = false;

        this.mail = email;
        this.passwo = passw;

        // se obtiene el objeto Session. La configuración es para
        // una cuenta de gmail.
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.live.com");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.user", mail);
        props.setProperty("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props, null);
        // session.setDebug(true);

        // Se compone la parte del texto
        BodyPart texto = new MimeBodyPart();
        texto.setText("Texto del mensaje");

        // Se compone el adjunto con la imagen
        BodyPart adjunto = new MimeBodyPart();
        adjunto.setDataHandler(
                new DataHandler(new FileDataSource(dir)));
        adjunto.setFileName("backupGestionDeGastos.sql");

        // Una MultiParte para agrupar texto e imagen.
        MimeMultipart multiParte = new MimeMultipart();
        multiParte.addBodyPart(texto);
        multiParte.addBodyPart(adjunto);

        // Se compone el correo, dando to, from, subject y el
        // contenido.
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mail));
        message.addRecipient(
                Message.RecipientType.TO,
                new InternetAddress(para));
        message.setSubject("BackUp mensual Gestion Costa Norte");
        message.setContent(multiParte);

        // Se envia el correo.
        Transport t = session.getTransport("smtp");
        try {
            t.connect(mail, passwo);
        } catch (javax.mail.AuthenticationFailedException ex) {
            JOptionPane.showMessageDialog(null, "¡Datos incorrectos, no se ha establecido la conexión!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (t.isConnected()) {
            t.sendMessage(message, message.getAllRecipients());
            ret = t.isConnected();
        }

        t.close();

        return ret;

    }
}
