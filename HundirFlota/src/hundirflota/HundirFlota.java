package hundirflota;

import java.util.Random;
import java.util.Scanner;

public class HundirFlota {
	
		// CONSTANTES
		final static char AGUA_NO_TOCADO = '.';
		final static char AGUA = 'A';
		final static char TOCADO = 'X';
				
		// TAMAÑO DEL TABLERO
		final static int TAMANO = 10;
		
		private static Scanner sc;
		
		public static void main(String[] args) {
			
			// CON LA CLASE SCANNER PODREMOS LEER DESDE TECLADO JUNTO CON SYSTEM.IN
			sc = new Scanner(System.in);
			
			// MAPA USUARIO Y MAPA ORDENADOR (1VS1 CONTRA LA MÁQUINA) ; ARRAYS BIDIMENSIONALES 
			char[][] mapaUsuario = new char [TAMANO][TAMANO];
			char[][] mapaOrdenador = new char [TAMANO][TAMANO];		
			
			// MAPA REGISTRO TIRADAS USUARIO EN TABLERO DEL ORDENADOR
			char [][] mapaOrdenadorParaUsuario = new char [TAMANO] [TAMANO];
			
			// PUNTOS INICIALES JUGADORES
			int puntosUsuario = 24;
			int puntosOrdenador = 24;
			
			// CONTROL DEL PROGRAMA PARA SABER CUANDO HEMOS ACABADO LA PARTIDA
			boolean juegoTerminado = false;
			
			// INDICAR SI EL TIRO HA DADO O NO EN UN OBJETIVO
			boolean tiroCorrecto = false;
			
			// POSICIONES DE LA TIRADA
			int[] tiro = new int[2];
			
			inicializacion(mapaUsuario, mapaOrdenador);
			InicializarMapaRegistro(mapaOrdenadorParaUsuario);
			
			// EMPEZAMOS EL JUEGO, EL JUEGO TERMINA CUANDO LA VARIABLE JUEGOTERMINADO SEA TRUE
			while (!juegoTerminado) {
				
				// AL PRINCIPIO DE CADA TURNO, PINTAMOS EL MAPA DE USUARIO
				System.out.println("MAPA DE USUARIO");
				imprimirMapa(mapaUsuario);
				
				System.out.printf("PUNTOS RESTANTES DEL JUGADOR: %d\n", puntosUsuario);
				System.out.println("TURNO DEL JUGADOR");
				
				// COMENZAMOS CON LA TIRADA DEL USUARIO
				tiroCorrecto = false;
				while(!tiroCorrecto) {
					// SOLICITAMOS LOS DATOS POR TECLADO
					tiro = pedirCasilla();
					// VERIFICAMOS SI EL TIRO ES CORRECTO O NO
					if (tiro[0] != -1 && tiro[1] != -1) {
						// PUEDE SER INCORRECTO PORQUE YA SE HAYA TIRADO SOBRE ESAS COORDENADAS
						tiroCorrecto = evaluarTiro(mapaOrdenador, tiro);
						if (!tiroCorrecto) 
							System.out.println("TIRO INCORRECTO");
					} else {
						System.out.println("TIRO INCORRECTO");
					}
						// SI EL TIRO NO ES CORRECTO SE VOLVERA A TIRAR									
				}
				
				// ACTUALIZAMOS MAPA DEL ORDENADOR Y LOS PUNTOS
				int puntosOrdenadorAnterior = puntosOrdenador;
				puntosOrdenador = actualizarMapa(mapaOrdenador, tiro, puntosOrdenador);
				
				// ACTUALIZAMOS NUESTRO MAPA DE REGISTRO Y LO IMPRIMIMOS
				// SABEMOS SI LA TIRADA HA SIDO AGUA O TOCADO SI EL NUMERO DE PUNTOS SE HA DECREMENTADO
				char tipoTiro = (puntosOrdenadorAnterior - puntosOrdenador) > 0 ? TOCADO : AGUA;
				actualizarMapaRegistro(mapaOrdenadorParaUsuario, tiro, tipoTiro);
				System.out.println("\nREGISTRO DEL MAPA DEL ORDENADOR");
				imprimirMapa(mapaOrdenadorParaUsuario);
				
				// EL JUEGO TERMINA SI EL NUMERO DE PUNTOS LLEGA A 0
				juegoTerminado = (puntosOrdenador == 0);
				
				// SI NO HA GANADO EL JUGADOR LE TOCA A LA MAQUINA JUGAR
				if(!juegoTerminado) {
					System.out.printf("PUNTOS RESTANTES DEL ORDENADOR: %d\n\n", puntosOrdenador);
					System.out.println("TURNO DEL ORDENADOR");
					tiroCorrecto = false;
					// SEGUIMOS LOS MISMOS PARAMETROS DE COMPROBACION QUE EN LA TIRADA DEL USUARIO
					while(!tiroCorrecto) {
						tiro = generaDisparoAleatorio();
						tiroCorrecto = evaluarTiro(mapaUsuario, tiro);
					}
				}
				// ACTUALIZAMOS EL MAPA
				puntosUsuario = actualizarMapa(mapaUsuario, tiro, puntosUsuario);
				// EL JUEGO TERMINA SI EL NUMERO DE PUNTOS LLEGA A CERO
				juegoTerminado = (puntosUsuario == 0);
				
			} // FIN DE LA PARTIDA. ALGUIEN HA GANADO
			
			if (puntosOrdenador == 0) {
				System.out.println("EL VENCEDOR HA SIDO EL JUGADOR");
			} else
				System.out.println("EL VENCEDOR HA SIDO EL ORDENADOR");
			
			sc.close();
		}	
			
			

		
			// METODO QUE DEVUELVE UN NUMERO ALEATORIO DEL 0 AL 9
			private static int aleatorio() {
				
				Random r = new Random(System.currentTimeMillis());
				return r.nextInt(TAMANO);
				
			}
			
			
		
			// METODO PARA INICIALIZAR UN MAPA
			private static void inicializaMapa(char[][] mapa) {
				
				//PRIMERO INICIALIZAMOS EL MAPA ENTERO A TIPO --> AGUA_NO_TOCADO
				for (int i = 0; i < TAMANO; i++) {
					for (int j = 0; j < TAMANO; j++) {
						mapa[i][j] = AGUA_NO_TOCADO;
						System.out.print(mapa[i][j] + "  ");
					}
					System.out.println();
				}
				// BARCOS QUE TENEMOS PARA COLOCAR:
				// 2 PORTAAVIONES (5 CASILLAS), 3 BUQUES (3 CASILLAS), 5 LANCHAS (1 CASILLA)
				int[] barcos = {5, 5, 3, 3, 3, 1, 1, 1, 1, 1};
				
				// POSIBLE DIRECCION DE COLOCACION DE BARCO
				char[] direccion = {'V', 'H'};
				
				// PARA CADA BARCO
				for (int b: barcos) {
					
					// SE COLOCARA ALEATORIAMENTE CADA BARCO DEL ARRAY DE BARCOS, EMPEZANDO DE GRANDES A PEQUEÑOS
					// USAREMOS EL BOOLEANO COLACADO PARA SABER SI UN BARCO SE HA COLOCADO CORRECTAMENTE Y PODEMOS PASAR AL SIGUIENTE
					// ESTO SE DEBE A QUE AL HACERLO ALEATORIO PUEDE QUE INTERFIERA UN BARCO CON OTRO O CON LOS LIMITES DEL MAPA
					
					boolean colocado = false;
					while(!colocado) {
						
						// OBTENEMOS UNA POSICION Y DIRECCION ALEATORIAS
						int fila = aleatorio();
						int columna = aleatorio();
						char direcc = direccion[aleatorio() % 2]; // CON EL RESTO DE DIVIDIR ENTRE DOS OBTENDREMOS UN NUMERO ALEATORIO ENTRE 0 Y 1 (V o H)
								
						// TENDREMOS QUE COMPROBAR SI CABE EL BARCO EN LA POSICION ALEATORIA OBTENIDA
						if (direcc == 'V') {
							if (fila + b <= (TAMANO - 1)) {
								// COMPROBAMOS QUE NO HAY OTRO BARCO QUE SE SOLAPE
								boolean otro = false;
								
								for (int i = fila; (i <= fila + b) && !otro; i++) {
									
									if (mapa[i][columna] != AGUA_NO_TOCADO)
										otro = true;
									
								}
								// SI NO HAY OTRO BARCO, LO COLOCAMOS
								if (!otro) {
									
									for (int i = fila; i < fila + b; i++) {
										mapa[i][columna] = Integer.toString(b).charAt(0); // ESCRIBIMOS EL CARACTER ASOCIADO AL BARCO
									}
									colocado = true;
								}
							}
						} else { // ETRARA AL BLOQUE DE ELSE SI LA DIRECCION ES HORIZONTAL
							
							if (columna + b <= (TAMANO - 1)) {
								
								// COMPROBAMOS QUE NO HAY OTRO BARCO QUE SE SOLAPE
								boolean otro = false;
								for (int j = columna; (j <= columna + b) && !otro; j++) {
									
									if(mapa[fila][j] != AGUA_NO_TOCADO);
										otro = true;
									
								}
								// SI NO HAY OTRO BARCO LO COLOCAMOS
								if (!otro) {
									
									for (int j = columna; j < columna + b; j++) {
										mapa[fila][j] = Integer.toString(b).charAt(0);
									}
									colocado = true;
									
								}
							}
						}
					}
					
				}
				
				
			
			
			
			}
			
			
			
			
			
			// METODO PARA INICIALIZAR AMBOS MAPAS
			public static void inicializacion(char[][] m1, char[][] m2) {
				inicializaMapa(m1);
				inicializaMapa(m2);
			}
			
			
			
			
			
			
			// METODO PARA INICIALIZAR MAPA DE REGISTRO
			private static void InicializarMapaRegistro(char[][] mapa) {
				// INICIALIZAMOS EL MAPA ENTERO A AGUA_NO TOCADO
				for (int i = 0; i < TAMANO; i++)
					for (int j = 0; j < TAMANO; j++)
						mapa[i][j] = AGUA_NO_TOCADO;
			}
			
			
			
			
			
			private static int[] pedirCasilla() {
				
				System.out.println("Introduzca la casilla (LETRA+NUMERO): ");
				String linea = sc.nextLine(); // LEEMOS DE TECLADO
				linea = linea.toUpperCase(); // PASAMOS LA CADENA LINEA A MAYUSCULAS
				int[] t;
				
				// COMPROBAMOS QUE LO INTRODUCIDO POR EL USUARIO ES CORRECTO MEDIANTE UNA EXPRESION REGULAR
				if (linea.matches("^[A-Z][0-9]*$")) {
					
					// 	OBTENEMOS LA LETRA Y SUPONEMOS QUE SERÁ UNA LETRA QUE ESTÉ EN EL ABECEDARIO
					char letra = linea.charAt(0);
					//	EL NUMERO DE FILA ES VALOR NUMERICO (LETRA) - VALOR NUMERICO (A) 
					int fila = Character.getNumericValue(letra) - Character.getNumericValue('A');
					// PARA LA COLUMNA UNICAMENTE TENDREMOS QUE OBTENER EL NUMERO INTRODUCIDO EN LA SEGUNDA POSICION
					int columna = Integer.parseInt(linea.substring(1, linea.length()));
					// SI LAS COODERNADAS ESTAN DENTRO DEL TAMAÑO DEL TABLERO, LAS DEVOVEMOS
					if (fila >= 0 && fila < TAMANO && columna >= 0 && columna <= TAMANO) {
						t = new int[] { fila, columna};
					} else  // SI LAS COORDENADAS ESTAN FUERA DEVOLVEMOS -1 PARA VOLVER A SOLICITAR EL TIRO
						t = new int[] { -1, -1 };
				} else 
					t = new int[] { -1, -1 };	
				
				return t;
			}
			
			
			
			
			
			
			
			public static boolean evaluarTiro (char [][] mapa, int[] t) {
				int fila = t[0];
				int columna = t[1];
				return mapa[fila][columna] == AGUA_NO_TOCADO || (mapa[fila][columna] >= '1' && mapa[fila][columna] <= '5');
				
			}
			
			
			
			
			
			
			private static int actualizarMapa(char[][] mapa, int[] t, int puntos) {
				int fila = t[0];
				int columna = t[1];
				
				if (mapa[fila][columna] == AGUA_NO_TOCADO) {
					mapa[fila][columna] = AGUA;
					System.out.println("AGUA");
				} else {
					mapa[fila][columna] = TOCADO;
					System.out.println("HAS ALCANZADO A ALGÚN BARCO");
					--puntos;
				}
				
				return puntos;
			}
			
			
			
			
			
			
			private static int[] generaDisparoAleatorio() {
				return new int[] {aleatorio(), aleatorio()};
			}
			
			
			
			
			
			
			private static void actualizarMapaRegistro(char[][] mapa, int[] t, char valor) {
				
				int fila = t[0];
				int columna = t[1];
				
				mapa[fila][columna] = valor;
			}
			
			
			
			public static void imprimirMapa(char[][] mapa) {
				
				// CALCULAMOS LAS LETRAS SEGUN EL TAMAÑO
				char[] letras = new char[TAMANO];
				for (int i = 0; i < TAMANO; i++)
						letras[i] = (char) ('A' + i);
				
				// IMPRIMIMOS LA FILA DE ENCABEZADO
				System.out.println("	");
				for(int i = 0; i < TAMANO; i++) {
					System.out.println("[" + i + "]");
				}
				
				System.out.println("");
				// IMPRIMIMOS EL RESTO DE FILAS
				for (int i = 0; i < TAMANO; i++) {
					System.out.print("[" + letras[i] + "]  ");
					for (int j = 0; j < TAMANO; j++) {
						System.out.print(mapa[i][j] + "   ");
					}
					System.out.println("");
				}
			}
		
		
		
}
