package main;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class Ventana
{
    private JPanel panel1;
    private JTextField textField1;
    private JButton arbrirButton;
    private JButton verificarButton;
    private JTextArea textArea1;
    private JLabel direccionArchivoLabel;
    private File file;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ventana");
        frame.setContentPane(new Ventana().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Ventana()
    {
        verificarButton.setEnabled(false);
        arbrirButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                abrirArchivo();
            }
        });
        verificarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if(verificarButton.isEnabled())
                {
                    textArea1.setText("");
                    List<String> linksRotos = null;
                    Test test = new Test();

                    if(file!= null)
                        linksRotos = test.chequear404(file);

                    textArea1.setText(mostrarLinks(linksRotos));
                }
            }
        });
    }

    private void abrirArchivo()
    {
        String pathArchivo;
//        /llamamos el metodo que permite cargar la ventana
        JFileChooser fileChooser=new JFileChooser();
        fileChooser.showOpenDialog(fileChooser);
//        abrimos el archivo seleccionado
        file = fileChooser.getSelectedFile();

        if(file!=null)
        {
            pathArchivo = file.getAbsolutePath();
            direccionArchivoLabel.setText(pathArchivo);
            verificarButton.setEnabled(true);
        }
    }

    private String mostrarLinks(List<String> links)
    {
        StringBuilder sb = new StringBuilder();

        for(String link: links)
            sb.append(link).append("\n");

        return sb.toString();
    }
}
