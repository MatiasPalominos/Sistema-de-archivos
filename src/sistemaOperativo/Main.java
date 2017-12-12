/*
 * Clase encargada de dar inicio a todo el sistema. Interactuando con las demás
 * clases creadas.
 */
package sistemaOperativo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Matías
 */
public class Main
{

    private static Directorio directorio;
    private static BitMap bitmap;
    private static Disco disco;
    private static int tamSector;
    private static int numSectores;

    public static void main(String[] args) throws IOException
    {
        int opcion;

        numSectores = 512;
        tamSector = 512;

        iniciarDisco(numSectores, tamSector);

        do
        {
            opcion = Menu.menuInicial();
            switch (opcion)
            {
                case 1:
                    lanzarPrueba();
                    break;
                case 2:
                    ejecutarMenuPrincipal();
                    break;
            }
        } while (opcion != 0);
    }
    
    public static void iniciarDisco(int tamSector, int numSector) throws FileNotFoundException, IOException
    {
        disco = new Disco(numSectores, tamSector);
        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<Integer> pos = new ArrayList<>();
        ArrayList<Integer> espacioLibre = new ArrayList<>();

        File file = new File("Disco");

        //comienzo verificando si existe el disco.
        if (file.exists())
        {
            FileReader fr = new FileReader(file);
            BufferedReader bf = new BufferedReader(fr);
            String sector = bf.readLine();

            //si la linea no se encuetra vacía, quiere decir que existe información en el directorio.
            if (lineaVacia(sector) == false)
            {
                //ahora busco los nombres y las posiciones de FCB para agregarlos al directorio.
                char[] linea = sector.toCharArray();
                int inicio = 0;
                int fin = 0;
                boolean buscarNombre = true;

                for (char c : linea)
                {
                    if (c != ' ')
                    {
                        if (c != '-')
                        {
                            fin++;
                        } else
                        {
                            //Almaceno el texto del archivo.
                            String texto = sector.substring(inicio, fin);
                            fin++;
                            inicio = fin;

                            if (buscarNombre)
                            {
                                nombres.add(texto);
                                buscarNombre = false;
                            } else
                            {
                                pos.add(Integer.parseInt(texto));
                                buscarNombre = true;
                            }

                        }
                    } else
                    {
                        break;
                    }
                }
                directorio = new Directorio(nombres, pos);
                sector = bf.readLine();

                for (int i = 0; i < sector.length(); i++)
                {
                    espacioLibre.add(Integer.parseInt(sector.charAt(i) + ""));
                }
                bitmap = new BitMap(espacioLibre);

            } else
            {
                formatear(numSector, tamSector);
            }
        } else
        {
            formatear(numSector, tamSector);
        }
    }
    
    private static void lanzarPrueba()
    {
        System.out.println("###FORMATEANDO DISCO###");
        formatear(tamSector, tamSector);
        System.out.println("Creando archivo a.txt");
        crear(512, "a");
        System.out.println("\nCreando archivo b.txt");
        crear(1024, "b");
        System.out.println("\nCreando archivo c.txt");
        crear(2048, "c");
        System.out.println("\n");
        listar();
    }

    /**
     * Verifica si la línea que se para como parámetro se encuentra vacío o no.
     *
     * @param sector
     * @return true si está vacío.
     */
    private static boolean lineaVacia(String sector)
    {
        Sector vacio = new Sector(tamSector);
        String aux = String.valueOf(vacio.getContenido());

        if (aux.equals(sector))
        {
            return true;
        }
        return false;
    }

    

    private static void ejecutarMenuPrincipal()
    {
        System.out.println("hola menu2");
    }

    private static void formatear(int numSector, int tamSector)
    {
        disco = new Disco(numSector, tamSector);
        disco.crearDisco();
        directorio = new Directorio();
        bitmap = new BitMap(disco);
        Sector sec = new Sector(tamSector);

        char[] contenido = new char[tamSector];
        contenido[0] = '1';
        contenido[1] = '1';

        for (int i = 2; i < tamSector; i++)
        {
            contenido[i] = '0';
        }

        sec.setContenido(contenido);
        disco.escribirEnSector(1, sec);
    }

    private static void crear(int tamaño, String nombreArchivo)
    {
        //Valido que el nombre no sea superior a 8 caracteres y que al archivo no exista.
        if (nombreArchivo.length() <= 8 && noExiste(nombreArchivo))
        {
            System.out.println("si entra");
            int totalSectores = 0;
            if (tamaño % 512 != 0)
            {
                totalSectores = tamaño / 512 + 2;
            } else
            {
                totalSectores = tamaño / 512 + 1;
            }

            //verifico que aún queda espacio en el disco.
            if (bitmap.quedaEspacio(totalSectores))
            {
                int numSector = bitmap.obtenerEspacioLibre();
                //Actualizo la memoria que va quedando.
                actualizarBitMap();
                FCB fcb = new FCB(tamSector, tamaño);
                //agrego todos los sectores restantes para guardar los datos del archivo.
                for (int i = 0; i < (totalSectores - 1); i++)
                {
                    int numSec = bitmap.obtenerEspacioLibre();
                    actualizarBitMap();

                    Sector nuevoSector = new Sector(tamSector, numSec);
                    if (i == totalSectores - 2 && tamaño % tamSector != 0)
                    {
                        nuevoSector = new Sector(tamSector, (tamaño % tamSector), numSec);
                    }

                    //añado el puntero al nuevo FCB.
                    fcb.getSectoresDisco().add(nuevoSector);
                    fcb.getPos().add(numSec);

                    //registro el nuevo bloque en el disco.
                    disco.escribirEnSector(numSec, nuevoSector);
                }

                //Registro el FCB en el disco.
                String salida = String.valueOf(fcb.getTamArchivo());
                for (Sector s : fcb.getSectoresDisco())
                {
                    salida += "-" + String.valueOf(s.getId());
                }

                Sector s = new Sector(tamSector, salida.toCharArray());
                disco.escribirEnSector(numSector, s);

                //comienzo a agregar al directorio.
                directorio.getNombres().add(nombreArchivo);
                directorio.getPos().add(numSector);

                salida = "";
                boolean escribirNombre = true;

                int tot = directorio.getNombres().size();
                for (int i = 0; i < tot; i++)
                {
                    if (escribirNombre)
                    {
                        salida += directorio.getNombres().get(i) + "-";
                        escribirNombre = false;
                    }
                    if (escribirNombre == false)
                    {
                        salida += directorio.getPos().get(i) + "-";
                        escribirNombre = true;
                    }
                }

                Sector dir = new Sector(tamSector, salida.toCharArray());
                disco.escribirEnSector(0, dir);
                System.out.println("El archivo se creó correctamente!");
            } else
            {
                System.out.println("No queda espacio en el disco.");
            }
        } else if (nombreArchivo.length() > 8)
        {
            System.out.println("Nombre del archivo muy largo, no debe sobrepasar los 8 caracteres.");
        } else
        {
            System.out.println("El archivo ya existe en el directorio.");
        }
    }

    private static boolean noExiste(String nombreArchivo)
    {
        //System.out.println("noExiste:" + nombreArchivo);
        for (String nombre : directorio.getNombres())
        {
            //System.out.println("String Nombre: " + nombre);
            if (nombre.equals(nombreArchivo))
            {
                return false;
            }
        }
        return true;
    }

    private static void actualizarBitMap()
    {
        String s = "";

        for (int i = 0; i < tamSector; i++)
        {
            s += String.valueOf(bitmap.getEspacio().get(i));
        }

        Sector sec = new Sector(tamSector);
        sec.setContenido(s.toCharArray());
        disco.escribirEnSector(1, sec);
    }

    private static void listar()
    {
        int i = 0;
        //verifico que si hay archivos en el directorio.
        if (!directorio.getNombres().isEmpty())
        {

            for (String nombre : directorio.getNombres())
            {
                int posFCB = directorio.getPos().get(i);
                Sector sec = disco.leerSector(posFCB);
                FCB fcb = convertirBloque(sec);

                System.out.println("Nombre: " + nombre);
                System.out.println("----------------------------");
                System.out.println("Tamaño(bytes): " + fcb.getTamArchivo());
                System.out.println("----------------------------");
                System.out.println("Bloques: " + fcb.getPos().size());
                System.out.println("----------------------------");
            }
            System.out.println("El directorio se listó correctamente!");
        } else
        {
            System.out.println("El directorio está vacío.");
        }
    }

    private static FCB convertirBloque(Sector secFCB)
    {
        FCB salida = new FCB(tamSector);
        salida.setContenido(secFCB.getContenido());

        char[] linea = secFCB.getContenido();
        boolean estaElTamaño = true;
        int inicio = 0;
        int fin = 0;
        String tmp = "";
        String tamaño = "";
        int pos = 1;

        ArrayList<Integer> referencias = new ArrayList<>();
        for (char c : linea)
        {
            if (c != ' ')
            {
                if (c != '-')
                {
                    fin++;
                } else if (estaElTamaño)
                {
                    tamaño = String.valueOf(linea).substring(inicio, fin);
                    salida.setTamArchivo(Integer.valueOf(tamaño));
                    fin++;
                    inicio = fin;
                    estaElTamaño = false;
                } else
                {
                    tmp = String.valueOf(linea).substring(inicio, fin);
                    referencias.add(Integer.valueOf(tmp));
                    fin++;
                    inicio = fin;
                }
            } else
            {
                if (pos == fin + 1)
                {
                    tmp = String.valueOf(linea).substring(inicio, fin);
                    referencias.add(Integer.valueOf(tmp));
                }
                break;
            }
            pos++;
        }
        for (Integer referencia : referencias)
        {
            salida.getPos().add(referencia);
        }
        System.out.println("antes de salida");
        return salida;
    }

}
