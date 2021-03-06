package trickybridges;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

public class Panel extends JPanel implements Runnable, KeyListener {
	private static final long serialVersionUID = 1L;
	private final static int PANTALLA_INICIO = 1; 
    private final static int PANTALLA_JUEGO = 2;
    private final static int PANTALLA_PERDEDOR = 3;
	private final static int PANTALLA_GANADOR = 4;
	public static int nivelActual = 1; 
	private static int anchoJuego;
	private static int largoJuego;
	public int pantallaActual;
    private int esperaActualizaciones; // para las pantallas intermedias uwu
    private PantallaImagen inicio;
    private PantallaImagen victoria;
    private PantallaImagen espera;
    private PantallaPerdedor derrota;
    private PantallaImagen tutorial;
	private Vidas vidas;
    private static Puntaje puntaje;
    private Sonidos sonidos;
    Mapa niveles = new Mapa ();	
    Personaje personaje = new Personaje ();
    
   
    public Panel (int anchoJuego, int largoJuego, int vidas, int esperaActualizaciones) {
		Panel.anchoJuego = anchoJuego;
		Panel.largoJuego = largoJuego;
		this.pantallaActual = PANTALLA_INICIO;
		this.esperaActualizaciones = esperaActualizaciones;
		this.inicio =  new PantallaImagen(anchoJuego, largoJuego, "imagenes/inicio.jpg", Puntaje.getPuntajeTotal());
	    this.victoria = new PantallaImagen(anchoJuego, largoJuego, "imagenes/victoria.jpg", Puntaje.getPuntajeTotal());
	    this.espera = new PantallaImagen(anchoJuego, largoJuego, "imagenes/muerte.jpg", Puntaje.getPuntajeTotal());
	    this.tutorial = new PantallaImagen(anchoJuego, largoJuego, "imagenes/tutorialRapidojiji.jpg", Puntaje.getPuntajeTotal());
		cargarSonidos();
		this.sonidos.repetirSonido("background");
		inicializarJuego();
	}

	 private void inicializarJuego() {			 // EL ORDEN EN QUE DECLARES LAS FUNCIONES ES EL ORDEN EN QUE SE EJECUTAN (A MENOS QUE ESTEN EN UN RUN/HILO),
		 										// ESO VA A DEFINIR QUE COSA VA POR ENCIMA DE OTRA. VA DE ARRIBA PARA ABAJO, POR LO QUE LAS COSAS QUE ESTEN MAS ABAJO (EN CASO DE DIBUJAR) VAN A APARECER ARRIBA YA QUE SON LAS ULTIMAS EN EJECUTARSE.
		 	nivelActual = 1;					//INICIO EL NIVEL EN 1
		 	Puntaje.reiniciarPuntaje(); 		//INICIO AMBOS PUNTAJES (MAXIMO Y POR NIVEL) EN 0 AL INICIAR
	        this.derrota = null;				// ELIMINO LA IMAGEN DE DERROTA DE LA PANTALLA
	        this.vidas = new Vidas(3);			// LE DOY 3 VIDAS AL CONTADOR DE VIDAS E INICIO LAS VIDAS
	        this.niveles = new Mapa();			// INICIALIZO EL MAPA
			Personaje.inicializarPersonaje();	// LE DOY UBICACION INICIAL AL PERSONAJE 
			personaje.paint(getGraphics());
	        inicializarPuntaje();				// LE DOY E INICIO LAS PUNTUACIONES MAXIMAS POR NIVEL [EL 0/14 QUE APARECE EN MEDIO]
	    }
	
	 
	 public static void inicializarPuntaje() {
		if (nivelActual == 1) {
			Panel.puntaje = new Puntaje(14);
		}
		if (nivelActual == 2) {
			Panel.puntaje = new Puntaje(19);
		}
		if (nivelActual == 3) {
			Panel.puntaje= new Puntaje(25);
		}
		if (nivelActual == 4) {
			Panel.puntaje= new Puntaje(43);
		}
		if (nivelActual == 5) {
			Panel.puntaje = new Puntaje(41);
		} 
		if (nivelActual == 6){ 	
			Panel.puntaje = new Puntaje(41);
		}
		if (nivelActual == 7){ 	
			Panel.puntaje = new Puntaje(94);
		}
		if (nivelActual == 8){ 	
			Panel.puntaje = new Puntaje(208);
		}
	}
	 

	public Dimension getPreferredSize() {
		return new Dimension (anchoJuego, largoJuego);
	}

	
	@Override //Implemento metodo Override para superponer la creada por la clase Runnable
	public void run() {
		while (true) { 
			if (pantallaActual == PANTALLA_JUEGO) {
			verificarFinDeJuego();
        }
		repintar();
        esperar(esperaActualizaciones);
		}
	}
	
	public void morir() {
		Vidas.perderVida();					// PERDER UNA VIDA
		Personaje.reiniciarMapa(); 				// PINTA DE NUEVO EL MAPA 
		Puntaje.setPuntajeTotal();			// LE RESTA AL PUNTAJE TOTAL LOS PUNTOS QUE GANASTE AL PERDER
		Puntaje.setPuntaje0(0);
		Personaje.inicializarPersonaje();	// INICIA DE NUEVO AL PERSONAJE EN LA UBICACION POR NIVEL 
		esperar(5000);
		espera.dibujarse(getGraphics());
		espera.dibujarVidas(getGraphics());
		sonidos.tocarSonido("muerte");		// LLAMA A HACER SONIDO DE MUERTE	
    	esperar(5000);
	}
	
	public void terminarJuego() { 
		Personaje.reiniciarMapa(); 				// PINTA DE NUEVO EL MAPA 
		Puntaje.setPuntajeTotal();			// LE RESTA AL PUNTAJE TOTAL LOS PUNTOS QUE GANASTE AL PERDER
		Puntaje.setPuntaje0(0);
		Personaje.inicializarPersonaje();	// INICIA DE NUEVO AL PERSONAJE EN LA UBICACION POR NIVEL 
	}
	
	public void esperar(int milisegundos) {
		try {
			Thread.sleep (milisegundos);
		} catch (Exception el) {
			Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, "xd");
		}
	}

	public void paintComponent (Graphics g) {
		super.paintComponent(g);
        if (pantallaActual == PANTALLA_INICIO) { //NO LAG
        	terminarJuego();
        	inicializarJuego();
        	inicio.dibujarse(g);
        }
        if (pantallaActual == PANTALLA_PERDEDOR) {
            if (this.derrota == null) {
                this.derrota = new PantallaPerdedor(anchoJuego, largoJuego, "imagenes/derrota.jpg", Puntaje.getPuntaje());
            }
            derrota.dibujarse(g);
        }
        if (pantallaActual == PANTALLA_GANADOR) {
        	victoria.dibujarse(g);
        	victoria.dibujarPuntos(g);
        }
        if (pantallaActual == PANTALLA_JUEGO) { //NO LAG
			pintarHud(g);
			vidas.dibujarse(g);
			puntaje.dibujarse(g);
			pintarNivel(g);
			niveles.dibujarse(g);
			personaje.paint(g);
			
        }
	}
    
	public void pintarHud (Graphics g) { // PINTA NOMAS LO AMARILLITO DE ARRIBA Y ABAJO UWU
		Color naranjon = new Color (173, 117, 24);
		Color amarillito = new Color (247, 179, 96);
		g.setColor(amarillito);
		g.fillRect(0, 0, 1000, 50);
		g.setColor(naranjon); 
		g.drawRect(0, 0, 1000, 50);
		g.setColor(amarillito);
		g.fillRect(0, 850,1000, 50);
		g.setColor(naranjon);
		g.drawRect(0, 850, 1000, 50);
	}
	
	public void pintarNivel (Graphics g) {
		g.setFont(new Font("Sans Serif", Font.BOLD, 30));
    	g.setColor(Color.black);
        g.drawString("LEVEL " + nivelActual, 90 , 35);
	}

	@Override
	public void keyTyped(KeyEvent e) { //INICIALIZO ACA LOS KEYLISTENERS YA QUE ES EL PANEL EL ENCARGADO PRINCIPAL DE ESCUCHAR LOS MOVIMIENTOS DEL USUARIO. DESPUES LO DERIVARE A LAS OTRAS CLASES.
		personaje.keyTyped(e);	
	}
	
	@Override
	public void keyPressed(KeyEvent e) { // AL PRESIONAR CUALQUIER TECLA CAMBIARA LO QUE ESTA DIBUJADO EN PANTALLA.
		sonidos.tocarSonido("piso");		
 		if (pantallaActual == PANTALLA_INICIO) { //SI EL JUEGO ESTA MOSTRANDO LA IMAGEN DEL INICIO, AL TOCARSE UNA TECLA SE INICIARA EL JUEGO
            inicializarJuego();
            pantallaActual = PANTALLA_JUEGO; // LA PANTALLA PASARA A SER, ENTONCES, EL DIBUJO DEL JUEGO.
        }

        if (pantallaActual == PANTALLA_PERDEDOR || pantallaActual == PANTALLA_GANADOR) { // SI LA PANTALLA ES LA DEL PERDEDOR O GANADOR, AL TOCAR UNA TECLA, CAMBIARA A LA PANTALLA INICIO.
           terminarJuego();
        	pantallaActual = PANTALLA_INICIO;
        }

        if (pantallaActual == PANTALLA_JUEGO) { // LE DECLARA A PANTALLA_JUEGO LAS CONFIGURACIONES DE TECLA INICIALIZADAS EN LA CLASE PERSONAJE
        	personaje.keyPressed(e);
        	if  ((Mapa.obtenerNivel()[(Personaje.getPosicionY()/50)][(Personaje.getPosicionX()/50)] == 3)) {
        		personaje.dibujarMuerte(getGraphics());
        		personaje.hacerAnimacion();
        		//personaje.ripearPersonaje();
        		sonidos.tocarSonido("grito");	
        		morir();
        	}
        }
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		personaje.keyReleased(e); 
	}

	private void cargarSonidos() {
        try {
            sonidos = new Sonidos();
            sonidos.agregarSonido("piso", "sonidos/piso.wav");
            sonidos.agregarSonido("grito", "sonidos/muerte.wav");
            sonidos.agregarSonido("background", "sonidos/menu.wav");
            sonidos.agregarSonido("muerte", "sonidos/musicamuerte.wav");
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }
	
	private void verificarFinDeJuego() {
        if (Vidas.getVidas() == 0) { // SI TENES 0 VIDAS, SE MUESTRA LA PANTALLA DE DERROTA.
            pantallaActual = PANTALLA_PERDEDOR;
        }
        
        if(nivelActual > 8){ 
			pantallaActual = PANTALLA_GANADOR; // SI PASASTE EL ULTIMO NIVEL (ESTE CASO 6), TE MUESTRA LA PANTALLA DE VICTORIA.
		}
    }
	
	public static int getNivelActual() {
		return nivelActual;
	}
	
	public static int cambiarNivelActual() {
		Personaje.reiniciarMapa();
		return nivelActual++;
	}

	private void repintar() {
		this.repaint();
	}
}
