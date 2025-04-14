//*********************** */
/**** CLASES PADRES ******/
//*********************** */

import java.util.ArrayList;
import java.util.List;

//CLASE EMPRESA DE MENSAJERIA
class EmpresaDeMensajeria {
    private String nombre;
    private List<Mensajero> mensajeros;

    public EmpresaDeMensajeria(String nombre) {
        this.nombre = nombre;
        this.mensajeros = new ArrayList<>();
    }

    public void agregarMensajero(Mensajero mensajero) {
        this.mensajeros.add(mensajero);
        System.out.println(mensajero.getNombre() + " ha sido añadido a la nómina de " + this.nombre + ".");
    }

    public boolean quitarMensajero(String nombreMensajero) {
        int indice = -1;
        for (int i = 0; i < this.mensajeros.size(); i++) {
            if (this.mensajeros.get(i).getNombre().equals(nombreMensajero)) {
                indice = i;
                break;
            }
        }
        if (indice > -1) {
            Mensajero mensajeroRemovido = this.mensajeros.remove(indice);
            System.out.println(mensajeroRemovido.getNombre() + " ha sido removido de la nómina de " + this.nombre + ".");
            return true;
        } else {
            System.out.println(nombreMensajero + " no se encuentra en la nómina de " + this.nombre + ".");
            return false;
        }
    }

    public void listarNomina() {
        System.out.println("--- Nómina de Mensajeros de " + this.nombre + " ---");
        if (this.mensajeros.isEmpty()) {
            System.out.println("No hay mensajeros en la nómina.");
        } else {
            for (Mensajero mensajero : this.mensajeros) {
                System.out.println("- " + mensajero.getNombre());
            }
        }
        System.out.println("---------------------------------------");
    }
}

//CLASE DE DESTINOS
class Destino {
    private String nombre;
    private Double restriccionPesoMax;
    private boolean requiereLlamada;
    private java.util.function.Consumer<Mensajero> efectoEntrega;

    public Destino(String nombre, Double restriccionPesoMax, boolean requiereLlamada, java.util.function.Consumer<Mensajero> efectoEntrega) {
        this.nombre = nombre;
        this.restriccionPesoMax = restriccionPesoMax;
        this.requiereLlamada = requiereLlamada;
        this.efectoEntrega = efectoEntrega;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getRestriccionPesoMax() {
        return restriccionPesoMax;
    }

    public boolean getRequiereLlamada() {
        return requiereLlamada;
    }

    public java.util.function.Consumer<Mensajero> getEfectoEntrega() {
        return efectoEntrega;
    }
}

// CLASE DEL MENSAJERO
class Mensajero {
    private String nombre;
    private double peso;
    private MedioDeTransporte medioDeTransporte;
    private MedioDeComunicacion medioDeComunicacion;

    public Mensajero(String nombre, double peso, MedioDeTransporte medioDeTransporte, MedioDeComunicacion medioDeComunicacion) {
        this.nombre = nombre;
        this.peso = peso;
        this.medioDeTransporte = medioDeTransporte;
        this.medioDeComunicacion = medioDeComunicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public MedioDeTransporte getMedioDeTransporte() {
        return medioDeTransporte;
    }

    public MedioDeComunicacion getMedioDeComunicacion() {
        return medioDeComunicacion;
    }

    public boolean puedeEntregar(Destino destino, Paquete paquete) {
        double pesoTotal = this.medioDeTransporte.calcularPesoTotal(this.peso, paquete.getPeso());
        if (destino.getRestriccionPesoMax() != null && pesoTotal > destino.getRestriccionPesoMax()) {
            System.out.println(this.nombre + " no puede entregar a " + destino.getNombre() + " con su medio de transporte actual por restricción de peso.");
            return false;
        }
        if (destino.getRequiereLlamada() && !(this.medioDeComunicacion instanceof Telefono)) {
            System.out.println(this.nombre + " no puede entregar a " + destino.getNombre() + " porque se requiere una llamada y no pudo realizarla con su medio de comunicación.");
            return false;
        }
        return true;
    }

    public boolean entregar(Destino destino, Paquete paquete) {
        System.out.println(this.nombre + " está intentando entregar el paquete \"" + paquete.getDescripcion() + "\" a " + destino.getNombre() + " viajando en " + this.medioDeTransporte.getNombre() + ".");

        if (this.puedeEntregar(destino, paquete)) {
            this.medioDeTransporte.aplicarEfectoEntrega(this, destino);
            aplicarEfectoEntregaDestino(destino);
            System.out.println(this.nombre + " entregó el paquete \"" + paquete.getDescripcion() + "\" a " + destino.getNombre() + ".");
            return true;
        }
        return false;
    }

    public void aplicarEfectoEntregaDestino(Destino destino) {
        if (destino.getEfectoEntrega() != null) {
            destino.getEfectoEntrega().accept(this);
        }
    }
}

//Clases de Medio de Transporte
class MedioDeTransporte {
    private String nombre;
    private double peso;

    public MedioDeTransporte(String nombre, double peso) {
        this.nombre = nombre;
        this.peso = peso;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPeso() {
        return peso;
    }

    public double calcularPesoTotal(double pesoBaseMensajero, double pesoPaquete) {
        return pesoBaseMensajero + this.peso + pesoPaquete;
    }

    public void aplicarEfectoEntrega(Mensajero mensajero, Destino destino) {
        // Lógica genérica para efectos de entrega según el medio de transporte
    }
}

//Clase de medio de comunicacion
class MedioDeComunicacion {
    private String nombre;

    public MedioDeComunicacion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean comunicar(Mensajero mensajero, String mensaje) {
        System.out.println(mensajero.getNombre() + " utiliza su " + this.nombre + " para decir: " + mensaje);
        return true; // Por defecto, la comunicación es exitosa
    }
}

//Clase de Paquete
class Paquete {
    private String descripcion;
    private double peso;
    private Destino destino;

    public Paquete(String descripcion, double peso, Destino destino) {
        this.descripcion = descripcion;
        this.peso = peso;
        this.destino = destino;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPeso() {
        return peso;
    }

    public Destino getDestino() {
        return destino;
    }
}

//*********************** */
/**** CLASES HIJAS ******/
//*********************** */

//Clases de Mensajeros
class ChuckNorris extends Mensajero {
    public ChuckNorris() {
        super("Chuck Norris", 900, new Pie(), new Dedo());
    }

    @Override
    public boolean entregar(Destino destino, Paquete paquete) {
        System.out.println(this.getNombre() + " está intentando entregar el paquete \"" + paquete.getDescripcion() + "\" a " + destino.getNombre() + " viajando en " + this.getMedioDeTransporte().getNombre() + ".");
        if (this.puedeEntregar(destino, paquete)) {
            this.getMedioDeTransporte().aplicarEfectoEntrega(this, destino);
            System.out.println(this.getNombre() + " entregó el paquete \"" + paquete.getDescripcion() + "\" a " + destino.getNombre() + ".");
            return true;
        }
        return false;
    }
}

class Neo extends Mensajero {
    private int credito;

    public Neo() {
        super("Neo", 0, new Volando(), new Telefono()); // No pesa, vuela y tiene teléfono
        this.credito = 10;
    }

    public int getCredito() {
        return credito;
    }

    public void setCredito(int credito) {
        this.credito = credito;
    }
}

class Harry extends Mensajero {
    private boolean enBuckbeak;

    public Harry() {
        super("Harry", 70, new Bicicleta(), new SinComunicacion()); // Empieza en bicicleta y sin comunicación
        this.enBuckbeak = false;
    }

    public void subirABicicleta() {
        super.setMedioDeTransporte(new Bicicleta());
        this.enBuckbeak = false;
        System.out.println(this.getNombre() + " sube a su " + this.getMedioDeTransporte().getNombre() + ".");
    }

    public void subirABuckbeak() {
        super.setMedioDeTransporte(new Buckbeak());
        this.enBuckbeak = true;
        System.out.println(this.getNombre() + " sube a " + this.getMedioDeTransporte().getNombre() + ".");
    }

    public void desmontarVehiculo() {
        super.setMedioDeTransporte(new Pie());
        this.enBuckbeak = false;
        System.out.println(this.getNombre() + " desmonta su vehículo y va a pie.");
    }

    public void intentarComunicar(String mensaje) {
        this.getMedioDeComunicacion().comunicar(this, mensaje);
    }
}

//Clases de medios de comunicacion
class Dedo extends MedioDeComunicacion {
    public Dedo() {
        super("dedo (pulgar al oído)");
    }

    @Override
    public boolean comunicar(Mensajero mensajero, String persona) {
        System.out.println(mensajero.getNombre() + " se lleva el pulgar a la oreja y llama a " + persona + ".");
        return true;
    }
}

class Telefono extends MedioDeComunicacion {
    private int costoLlamada;

    public Telefono() {
        super("teléfono");
        this.costoLlamada = 5;
    }

    @Override
    public boolean comunicar(Mensajero mensajero, String mensaje) {
        if (mensajero instanceof Neo && ((Neo) mensajero).getCredito() >= this.costoLlamada) {
            System.out.println(mensajero.getNombre() + " llama por teléfono: " + mensaje + ". Su crédito baja $" + this.costoLlamada + ".");
            ((Neo) mensajero).setCredito(((Neo) mensajero).getCredito() - this.costoLlamada);
            return true;
        } else if (!(mensajero instanceof Neo)){
            System.out.println(mensajero.getNombre() + " llama por teléfono: " + mensaje + ".");
            return true;
        }
         else {
            System.out.println(mensajero.getNombre() + " no tiene suficiente crédito para llamar.");
            return false;
        }
    }
}

class SinComunicacion extends MedioDeComunicacion {
    public SinComunicacion() {
        super("ninguno");
    }
    @Override
    public boolean comunicar(Mensajero mensajero, String mensaje) {
        System.out.println(mensajero.getNombre() + " no tiene medio de comunicación para: " + mensaje);
        return false;
    }
}

//Clases de medios de transporte
class Pie extends MedioDeTransporte {
    public Pie() {
        super("a pie", 0);
    }

    @Override
    public double calcularPesoTotal(double pesoBaseMensajero, double pesoPaquete) {
        return pesoBaseMensajero + pesoPaquete;
    }
}

class Volando extends MedioDeTransporte {
    public Volando() {
        super("volando", 0);
    }

    @Override
    public void aplicarEfectoEntrega(Mensajero mensajero, Destino destino) {
        // Lógica específica para cuando se entrega volando (si la hay)
    }
}

class Bicicleta extends MedioDeTransporte {
    public Bicicleta() {
        super("bicicleta mágica", 1);
    }

    @Override
    public void aplicarEfectoEntrega(Mensajero mensajero, Destino destino) {
        if(mensajero.getPeso() < 50) {
            System.out.println(mensajero.getNombre() + " Tiene 50kg o menos, puede morir por agotamiento si sigue quemando calorias");
        }
        if(mensajero.getPeso() > 120) {
            System.out.println(mensajero.getNombre() + " Tiene 120kg o mas, puede morir por infarto");
        }
        mensajero.setPeso(mensajero.getPeso() - 2);
        System.out.println(mensajero.getNombre() + " viajó en bicicleta y bajó 2 kilos de peso.");
    }
}

class Buckbeak extends MedioDeTransporte {
    public Buckbeak() {
        super("Buckbeak", 500);
    }

    @Override
    public void aplicarEfectoEntrega(Mensajero mensajero, Destino destino) {
        // No tiene efectos al entregar en Buckbeak
    }
}

public class Main {
    public static void main(String[] args) {
        // Destinos
        Destino puenteBrooklyn = new Destino("Puente de Brooklyn", 1000.0, false, null);
        Destino laMatrix = new Destino("La Matrix", null, true, (mensajero) ->
                System.out.println(mensajero.getNombre() + " sintió que la realidad se distorsionaba.")
        );

        // Mensajeros
        ChuckNorris chuck = new ChuckNorris();
        Neo neo = new Neo();
        Harry harry = new Harry();

        // Paquetes
        Paquete paquete1 = new Paquete("Moto Gilera 110", 200.0, puenteBrooklyn);
        Paquete paquete2 = new Paquete("3 Docenas de Medialunitas", 2.0, laMatrix);
        Paquete paquete3 = new Paquete("Play 5", 5.0, puenteBrooklyn);


        // Empresa de Mensajería
        EmpresaDeMensajeria bessoneMensajeros = new EmpresaDeMensajeria("BESSONE RAPI-ENTREGAS S.A.");

        // Agregar mensajeros a la empresa
        bessoneMensajeros.agregarMensajero(chuck);
        bessoneMensajeros.agregarMensajero(neo);
        bessoneMensajeros.agregarMensajero(harry);

        // Listar la nómina
        bessoneMensajeros.listarNomina();

        // Simulación de entregas
        System.out.println("\n--- Simulación de Entregas ---");

        chuck.entregar(paquete1.getDestino(), paquete1);
        System.out.println("Peso actual de Chuck Norris: " + chuck.getPeso() + "kg\n");

        neo.entregar(paquete2.getDestino(), paquete2);
        System.out.println("Crédito actual de Neo: $" + neo.getCredito() + "\n");

        harry.entregar(paquete3.getDestino(), paquete3); // Inicialmente en bicicleta
        harry.intentarComunicar("Hola, Susana?");

        harry.subirABuckbeak();
        harry.entregar(paquete3.getDestino(), paquete3);
        System.out.println("Peso actual de Harry: " + harry.getPeso() + "kg\n");

        harry.desmontarVehiculo();
        harry.entregar(paquete1.getDestino(), paquete1);
        harry.intentarComunicar("Hola Susana");
        System.out.println("Peso actual de Harry: " + harry.getPeso() + "kg\n");

        harry.entregar(paquete2.getDestino(), paquete1);
    }
}
