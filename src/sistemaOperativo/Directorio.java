/*
 * Clase encargada de manejar las posiciones del FCB dentro del Disco.
 */

package sistemaOperativo;

import java.util.ArrayList;

/**
 *
 * @author Matías
 */
public class Directorio {
    private ArrayList<String> nombres;
    private ArrayList<Integer> pos;
    
    public Directorio(ArrayList<String> nombres, ArrayList<Integer> pos){
        this.nombres = nombres;
        this.pos = pos;
    }
    
    public Directorio(){
        this.nombres = new ArrayList<String>();
        this.pos = new ArrayList<Integer>();
    }

    public ArrayList<String> getNombres()
    {
        return nombres;
    }

    public void setNombres(ArrayList<String> nombres)
    {
        this.nombres = nombres;
    }

    public ArrayList<Integer> getPos()
    {
        return pos;
    }

    public void setPos(ArrayList<Integer> pos)
    {
        this.pos = pos;
    }
    
    
}
