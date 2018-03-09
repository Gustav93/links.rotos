package main;

import link.Link;
import utilidades.enums.EstadoProcesamiento;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Vector;

import static utilidades.Test.chequear404;

public class Ventana
{
    private JPanel panel1;
    private JButton abrirButton;
    private JButton verificarButton;
    private JTextArea textArea1;
    private JLabel direccionArchivoLabel;
    private JLabel errorLabel;
    private JTable table1;
    private File archivo;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Links Rotos");
        frame.setContentPane(new Ventana().panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Ventana()
    {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Url");
        modelo.addColumn("Estado");

        table1 = new JTable();
        table1.setModel(modelo);

        errorLabel.setForeground(Color.red);
        verificarButton.setEnabled(false);

        abrirButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirArchivo();
            }
        });
        verificarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if(verificarButton.isEnabled())
                {
                    textArea1.setText("");
                    List<Link> linksRotos;

                    if(archivo != null)
                    {
                        try
                        {
                            linksRotos = chequear404(archivo);
                            textArea1.setText(mostrarLinks(linksRotos));
                        }
                        catch (IllegalArgumentException ex)
                        {
                            errorLabel.setText("Archivo incorrecto");
                        }
                    }
                }
            }
        });

    }

    private void abrirArchivo()
    {
        String pathArchivo;
//        /llamamos el metodo que permite cargar la ventana
        JFileChooser fileChooser=new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter(".xlsx & .ssl","xlsx", "ssl");
        fileChooser.setFileFilter(filtro);
        fileChooser.showOpenDialog(fileChooser);
//        abrimos el archivo seleccionado
        archivo = fileChooser.getSelectedFile();

        if(archivo != null)
        {
            pathArchivo = archivo.getAbsolutePath();
            direccionArchivoLabel.setText(pathArchivo);
            verificarButton.setEnabled(true);
            errorLabel.setText("");
        }
    }

    private String mostrarLinks(List<Link> links)
    {
        StringBuilder sb = new StringBuilder();

        for(Link link: links)
            if(!link.getEstadoProcesamiento().equals(EstadoProcesamiento.PROCESADO_CORRECTAMENTE))
                sb.append(link.getUrl()).append("\n");

        return sb.toString();
    }
}