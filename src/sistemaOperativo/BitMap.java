/*
 * Clase encargada de gestionar la memoria libre que va quedando.
 */
package sistemaOperativo;

import java.util.ArrayList;

/**
 *
 * @author Matías
 */
public class BitMap
{

    private ArrayList<Integer> espacio;

    public BitMap(Disco disco)
    {
        this.espacio = new ArrayList<Integer>();
        this.espacio.add(1);
        this.espacio.add(1);
        //El espacio 0 y 1 será ocupado por el Directorio y el BitMap
        for (int i = 2; i < disco.getNumSectores(); i++)
        {
            this.espacio.add(0);
        }

    }

    public BitMap(ArrayList<Integer> espaciosLibres)
    {
        this.espacio = espaciosLibres;
    }

    public int obtenerEspacioLibre()
    {
        for (int i = 0; i < this.espacio.size(); i++)
        {
            if (this.espacio.get(i) == 0)
            {
                this.espacio.set(i, 1);
                return i;
            }
        }
        return -1;
    }
    
    public boolean quedaEspacio(int sectores){
        int contador = 0;
        
        for (int i = 0; i < this.espacio.size(); i++)
        {
            if (this.espacio.get(i) == 0)
            {
                contador++;
            }
        }
        
        if(contador>=sectores){
            return true;
        }
        
        return false;
    }

    public ArrayList<Integer> getEspacio()
    {
        return espacio;
    }
    
    public void agregarEspacio(int sector){
        this.espacio.add(sector, 0);
    }

}
