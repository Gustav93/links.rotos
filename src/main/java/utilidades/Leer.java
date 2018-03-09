package utilidades;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import link.Link;

public class Leer
{
    //lee la 1ra columna de la 1ra hoja de un archivo excel, donde se van a encontrar los links a procesar
    public static List<Link> leerLinksExcel(File archivo)
    {
        if(!archivo.getName().contains(".xlsx"))
            throw new IllegalArgumentException("No se puede procesar el archivo");

        List<Link> links = new ArrayList();

        InputStream excelStream;

        try
        {
            excelStream = new FileInputStream(archivo);

            XSSFWorkbook excel = new XSSFWorkbook(excelStream); //representacion del archivo excel
            XSSFSheet hojaExcel = excel.getSheetAt(0); //selecciono la 1ra hoja del archivo
            XSSFRow fila;
            XSSFCell celda;
            String link;
            int cantFilas = hojaExcel.getLastRowNum(); //cantidad total de filas de la hoja actual

            for (int i = 0; i <= cantFilas; i++)
            {
                fila = hojaExcel.getRow(i);

                if (fila == null)
                    continue;

                celda = hojaExcel.getRow(i).getCell(0);
                link = celda.toString();

                if(link.equals("")) //si la celda no tiene contenido, no la tengo en cuenta
                    continue;

                links.add(new Link(link));
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("No se encontro el archivo");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return links;
    }

    //en estos archivos los links no aparecen explicitamente asi que hay que buscarlos en cada linea
    public static List<Link> leerLinksSSL(File archivo)
    {
        if(!archivo.getName().contains(".ssl"))
            throw new IllegalArgumentException("No se puede procesar el archivo");

        List<Link> links = new ArrayList();
        String linea;

        try
        {
            FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);

            while ((linea = br.readLine()) != null)
                agregarLink(linea, links);

            br.close();
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return links;
    }

    //Las lineas que tienen los liks no tienen toda la url completa, este metodo se encarga de buscar en la linea,
    //la parte del link que aparece en el archivo para despues agregar la parte de la url que falta.
    private static void agregarLink(String linea, List<Link> links)
    {
        //este patron matchea por ejemplo con:
        //1. "/musimundo/es/p/c/162951/e/123566",
        //2. /musimundo/es/sucursales/findStores_by_name?q=San Martin (Bs. As),
        //3. /musimundo/es/Categorías-Principales/CLIMATIZACION/AIRE-ACONDICIONADO/AIRE-ACONDICIONADO---SPLIT-MARSHALL-MTI-CSH-090AMR/p/00033018
        String patron = "(\"[ A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚÂÊÎÔÛâêîôûàèìòùÀÈÌÒÙ.-\\/-\\d\\+\\(\\)\\_\\?\\=]+\"|\\/\\w+\\/\\w+\\/[ A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚÂÊÎÔÛâêîôûàèìòùÀÈÌÒÙ.\\-\\d\\/\\+]{1,})";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(linea);

        if(matcher.find())
        {
            String url = matcher.group();

            if(url.contains("\"")) //en caso de tener comillas como en el caso 1, se las quito
                url = url.substring(1, url.length()-1);

            url = "https:\\\\www.musimundo.com" + url; //agrego la parte de la url que no tienen

            //el caso en el que matchea con favicon.ico no es necesario tenerlo en cuenta ya que solo es el link de un
            //icono
            if(!url.contains("favicon.ico"))
                links.add(new Link(url));
        }
    }
}
