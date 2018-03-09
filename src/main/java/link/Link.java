package link;

import utilidades.enums.EstadoProcesamiento;

public class Link
{
    private String url;
    private EstadoProcesamiento estadoProcesamiento;

    public Link(String url)
    {
        this.url = url;
        estadoProcesamiento = EstadoProcesamiento.SIN_PROCESAR;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public EstadoProcesamiento getEstadoProcesamiento() {
        return estadoProcesamiento;
    }

    public void setEstadoProcesamiento(EstadoProcesamiento estadoProcesamiento) {
        this.estadoProcesamiento = estadoProcesamiento;
    }
}
