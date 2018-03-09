package utilidades;

import link.Link;
import main.Ventana;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utilidades.Leer;
import utilidades.enums.EstadoProcesamiento;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test
{
    private static WebDriver driver = null;

    public static List<Link> chequear404(File file)
    {
        List<Link> links;

        if(file.getName().contains(".ssl"))
            links = Leer.leerLinksSSL(file);

        else if(file.getName().contains(".xlsx"))
            links = Leer.leerLinksExcel(file);

        else
            throw new IllegalArgumentException("Archivo incorrecto");

        System.setProperty("webdriver.chrome.driver","C:\\chromedriver.exe");
        driver = new ChromeDriver();
        System.out.println(links);
        for(Link link : links)
        {
            driver.get(link.getUrl());

            if(driver.getPageSource().contains("ERROR 404"))
                link.setEstadoProcesamiento(EstadoProcesamiento.ERROR_404);

            else if(driver.getCurrentUrl().equals("https://www.musimundo.com/musimundo/es/"))
                link.setEstadoProcesamiento(EstadoProcesamiento.REDIRECCIONAMENTO_HOME);

            else if(driver.getCurrentUrl().equals(link.getUrl()))
                link.setEstadoProcesamiento(EstadoProcesamiento.PROCESADO_CORRECTAMENTE);

            else
                link.setEstadoProcesamiento(EstadoProcesamiento.ERROR);
        }

        driver.quit();

        return links;
    }
}