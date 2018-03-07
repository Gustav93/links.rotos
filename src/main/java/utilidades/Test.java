package utilidades;

import main.Ventana;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utilidades.Leer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test
{
    private static WebDriver driver = null;

    public static List<String> chequear404(File file)
    {
        List<String> links;
        List<String> linksRotos = new ArrayList();

        if(file.getName().contains(".ssl"))
            links = Leer.leerLinksSSL(file);

        else if(file.getName().contains(".xlsx"))
            links = Leer.leerLinksExcel(file);

        else
            throw new IllegalArgumentException("Archivo incorrecto");

        System.setProperty("webdriver.chrome.driver","C:\\chromedriver.exe");
        driver = new ChromeDriver();
        System.out.println(links);
        for(String link : links)
        {
            driver.get(link);

            if(driver.getPageSource().contains("ERROR 404") || driver.getCurrentUrl().equals("https://www.musimundo.com/musimundo/es/")) {
                linksRotos.add(link);
                Ventana.cont++;
            }
        }

        System.out.println(linksRotos);

        driver.quit();

        return linksRotos;
    }
}