  
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Un objeto de esta clase mantiene una 
 * colección map que asocia  categorías (las claves) con
 * la lista (una colección ArrayList) de cursos que pertenecen a esa categoría 
 * Por ej. una entrada del map asocia la categoría 'BASES DE DATOS"' con
 * una lista de cursos de esa categoría
 * 
 * Las claves en el map se recuperan en orden alfabético y 
 * se guardan siempre en mayúsculas
 */
public class PlataformaCursos
{
    private final String ESPACIO = " ";
    private final String SEPARADOR = ":";
    private TreeMap<String, ArrayList<Curso>> plataforma;

    /**
     * Constructor  
     */
    public PlataformaCursos() {
        plataforma = new TreeMap<>();
    }

    /**
     * añadir un nuevo curso al map en la categoría indicada
     * Si ya existe la categoría se añade en ella el nuevo curso
     * (al final de la lista)
     * En caso contrario se creará una nueva entrada en el map con
     * la nueva categoría y el curso que hay en ella
     * Las claves siempre se añaden en mayúsculas  
     *  
     */
    public void addCurso(String categoria, Curso curso) {
        if (plataforma.containsKey(categoria)) {
            ArrayList<Curso> array = plataforma.get(categoria);
            array.add(curso);
        } else {
            ArrayList<Curso> array = new ArrayList<>();
            array.add(curso);
            plataforma.put(categoria, array);
        }
    }

    /**
     *  Devuelve la cantidad de cursos en la categoría indicada
     *  Si no existe la categoría devuelve -1
     *
     */
    public int totalCursosEn(String categoria) {
        if (plataforma.containsKey(categoria)) {
            ArrayList<Curso> array = plataforma.get(categoria);
            return array.size();
        }
        return -1;
    }

    /**
     * Representación textual de la plataforma (el map), cada categoría
     * junto con el nº total de cursos que hay en ella y a continuación
     * la relación de cursos en esa categoría (ver resultados de ejecución)
     * 
     * De forma eficiente ya que habrá muchas concatenaciones
     * 
     * Usar el conjunto de entradas y un iterador
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, ArrayList<Curso>>> conjuntoEntradas = plataforma.entrySet();
        Iterator<Map.Entry<String, ArrayList<Curso>>> it = conjuntoEntradas.iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<Curso>> entrada = it.next();
            sb.append(entrada.getKey().toUpperCase()).append("\n").append(entrada.getValue().toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Mostrar la plataforma
     */
    public void escribir() {
        System.out.println(this.toString());
    }

    /**
     *  Lee de un fichero de texto la información de los cursos
     *  En cada línea del fichero se guarda la información de un curso
     *  con el formato "categoria:nombre:fecha publicacion:nivel"
     *  
     */
    public void leerDeFichero() {
        Scanner sc = new Scanner(
                this.getClass().getResourceAsStream("/cursos.csv"));
        while (sc.hasNextLine())  {
            String lineaCurso = sc.nextLine().trim();
            int p = lineaCurso.indexOf(SEPARADOR);
            String categoria = lineaCurso.substring(0, p).trim();
            Curso curso = obtenerCurso(lineaCurso.substring(p + 1));
            this.addCurso(categoria, curso);
        }
    }

    /**
     *  Dado un String con los datos de un curso
     *  obtiene y devuelve un objeto Curso
     *
     *  Ej. a partir de  "sql essential training: 3/12/2019 : principiante " 
     *  obtiene el objeto Curso correspondiente
     *  
     *  Asumimos todos los valores correctos aunque puede haber 
     *  espacios antes y después de cada dato
     */
    private Curso obtenerCurso(String lineaCurso) {
        String str = "";
        String[] array = lineaCurso.split(SEPARADOR);
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
        String[] fechas = array[1].split("/");
        String diaStr = fechas[0];
        int dia = Integer.parseInt(diaStr);
        String mesStr = fechas[1];
        int mes = Integer.parseInt(mesStr);
        String añoStr = fechas[2];
        int año = Integer.parseInt(añoStr);
        LocalDate hoy = LocalDate.of(año, mes, dia); 
        Nivel nivel = Nivel.valueOf(array[2].toUpperCase());
        Curso curso = new Curso (array[0], hoy, nivel);
        return curso;
    }

    /**
     * devuelve un nuevo conjunto con los nombres de todas las categorías  
     *  
     */
    public TreeSet<String> obtenerCategorias() {
        TreeSet<String> tree = new TreeSet<>();
        Set<String> conjuntoClaves = plataforma.keySet();
        for (String str: conjuntoClaves) {
            tree.add(str);
        }
        return tree;
    }

    /**
     * borra de la plataforma los cursos de la categoría y nivel indicados
     * Se devuelve un conjunto (importa el orden) con los nombres de los cursos borrados 
     * 
     * Asumimos que existe la categoría
     *  
     */

    public TreeSet<String> borrarCursosDe(String categoria, Nivel nivel) {
        TreeSet<String> tree = new TreeSet<>();
        Set<Map.Entry<String, ArrayList<Curso>>> conjuntoEntradas = plataforma.entrySet();
        Iterator <Map.Entry<String, ArrayList<Curso>>> it = conjuntoEntradas.iterator();
        while(it.hasNext())
        {
            Map.Entry <String , ArrayList<Curso>> entrada = it.next();
            ArrayList<Curso> c = entrada.getValue();
            for(int i = c.size() - 1; i >= 0; i--)
            {
                if (c.get(i).getNivel().equals(nivel) && entrada.getKey().equals(categoria)) {
                    tree.add(c.get(i).getNombre());
                    c.remove(i);
                }
            }
        }
        return tree;
    }

    /**
     *   Devuelve el nombre del curso más antiguo en la
     *   plataforma (el primero publicado)
     */

    public String cursoMasAntiguo() {
        LocalDate fechaMin = LocalDate.MAX;
        String nombre = "";
        Set<Map.Entry<String, ArrayList<Curso>>> conjuntoEntradas = plataforma.entrySet();
        Iterator <Map.Entry<String, ArrayList<Curso>>> it = conjuntoEntradas.iterator();
        while(it.hasNext())
        {
            Map.Entry <String , ArrayList<Curso>> entrada = it.next();
            ArrayList<Curso> c = entrada.getValue();
            for(int i = c.size() - 1; i >= 0; i--)
            {
                if (c.get(i).getFecha().compareTo(fechaMin) <= 0) {
                    fechaMin =  c.get(i).getFecha();
                    nombre = c.get(i).getNombre();
                }
            }
        }
        return nombre;
    }

    /**
     *  
     */
    public static void main(String[] args) {
        PlataformaCursos plataforma = new PlataformaCursos();
        plataforma.leerDeFichero();
        plataforma.escribir();

        System.out.println(
            "Curso más antiguo: " + plataforma.cursoMasAntiguo()
            + "\n");

        String categoria = "bases de datos";
        Nivel nivel = Nivel.AVANZADO;
        System.out.println("------------------");
        System.out.println(
            "Borrando cursos de " + categoria.toUpperCase()
            + " y nivel "
            + nivel);
        TreeSet<String> borrados = plataforma.borrarCursosDe(categoria, nivel);

        System.out.println("Borrados " + " = " + borrados.toString() + "\n");
        categoria = "cms";
        nivel = Nivel.INTERMEDIO;
        System.out.println(
            "Borrando cursos de " + categoria.toUpperCase()
            + " y nivel "
            + nivel);
        borrados = plataforma.borrarCursosDe(categoria, nivel);
        System.out.println("Borrados " + " = " + borrados.toString() + "\n");
        System.out.println("------------------\n");
        System.out.println(
            "Después de borrar ....");
        plataforma.escribir();
    }
}
