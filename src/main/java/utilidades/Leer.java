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

public class Leer
{
    public static List<String> leerLinksExcel(File archivoExcel) {
        List<String> links = new ArrayList();

        InputStream excelStream;

        try {
            excelStream = new FileInputStream(archivoExcel);

            XSSFWorkbook excel = new XSSFWorkbook(excelStream); //representacion del archivo excel
            XSSFSheet hojaExcel = excel.getSheetAt(0); //selecciono la 1ra hoja del archivo

            XSSFRow fila;
            XSSFCell celda;

            int cantFilas = hojaExcel.getLastRowNum();
            String link;

            for (int i = 0; i <= cantFilas; i++) {
                fila = hojaExcel.getRow(i);

                if (fila == null)
                    break;

                celda = hojaExcel.getRow(i).getCell(0);
                link = celda.toString();

                links.add(link);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return links;
    }

    public static List<String> leerLinksSSL(File archivo) {

        List<String> links = new ArrayList();
        String cadena;

        try
        {
            FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);

            while ((cadena = br.readLine()) != null)
            {
//                System.out.println(cadena);
                agregarLink(cadena, links);
            }

            br.close();
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println(links);
        System.out.println(links.size());
        return links;
    }

    private static void agregarLink(String linea, List<String> links)
    {
        String patron = "\\\"\\/\\w{1,}\\/\\w{1,}\\/\\w{1,}\\/\\w{1,}\\/\\w{1,}\\/\\w{1,}\\/\\w{1,}\\\"";
        String patron2 = "\\\"\\/\\w{1,}\\/\\w{1,}\\/\\w{1,}\\/(-{0,}\"|\\d{1,}\")";
        String patron3 = "\\/\\w{1,}\\/\\w{1,}\\/\\w{1,}\\/\\w{0,}\\?\\w\\=[ A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚÂÊÎÔÛâêîôûàèìòùÀÈÌÒÙ.-\\d\\(\\)]{1,}";
        String patron4 = "(\"[ A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚÂÊÎÔÛâêîôûàèìòùÀÈÌÒÙ.-\\/-\\d\\+\\(\\)\\_\\?\\=]+\"|\\/\\w{1,}\\/\\w{1,}\\/[ A-Za-zäÄëËïÏöÖüÜáéíóúáéíóúÁÉÍÓÚÂÊÎÔÛâêîôûàèìòùÀÈÌÒÙ.\\-\\d\\/\\+]{1,})";
        Pattern pattern = Pattern.compile(patron4);
        Matcher matcher = pattern.matcher(linea);

        if(matcher.find())
        {
            String url = matcher.group();

            if(url.contains("\""))
                url = url.substring(1, url.length()-1);

            url = "https:\\\\www.musimundo.com" + url;

            if(!url.contains("favicon.ico"))
                links.add(url);
        }
    }
}
