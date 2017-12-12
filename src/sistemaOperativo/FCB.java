/*
 * Clase encargada de administrar los archivos que se encuentran en el Disco.
 */
package sistemaOperativo;

import java.util.ArrayList;

/**
 *
 * @author Matías
 */
public class FCB
{
    private int tamArchivo;
    private int tamSectores;
    private char[] contenido;
    private ArrayList<Sector> sectoresDisco;
    private ArrayList<Integer> pos;

    public FCB(int tamSectores)
    {
        this.tamSectores = tamSectores;
        this.contenido = new char[tamSectores];
        this.sectoresDisco = new ArrayList<>();
        this.pos = new ArrayList<>();

        for (int i = 0; i < tamSectores; i++)
        {
            contenido[i] = ' ';
        }
    }

    public FCB(int tamSectores, int tamArchivo)
    {
        this.tamSectores = tamSectores;
        this.tamArchivo = tamArchivo;
        this.contenido = new char[tamSectores];
        this.sectoresDisco = new ArrayList<>();
        this.pos = new ArrayList<>();

        for (int i = 0; i < (contenido.length); i++)
        {
            contenido[i] = ' ';
        }
    }

    public int getTamArchivo()
    {
        return tamArchivo;
    }

    public void setTamArchivo(int tamArchivo)
    {
        this.tamArchivo = tamArchivo;
    }

    public int getTamSectores()
    {
        return tamSectores;
    }

    public void setTamSectores(int tamSectores)
    {
        this.tamSectores = tamSectores;
    }

    public ArrayList<Sector> getSectoresDisco()
    {
        return sectoresDisco;
    }

    public void setSectoresDisco(ArrayList<Sector> sectoresDisco)
    {
        this.sectoresDisco = sectoresDisco;
    }

    public ArrayList<Integer> getPos()
    {
        return pos;
    }

    public void setPos(ArrayList<Integer> pos)
    {
        this.pos = pos;
    }

    public char[] getContenido()
    {
        return contenido;
    }

    public void setContenido(char[] contenido)
    {
        this.contenido = contenido;
    }

}
