/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package eft_s9_rodrigo_vasquez;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class EFT_S9_Rodrigo_Vasquez {

    // Constantes de Precios por zona
    private static final double PRECIO_VIP = 35000.0;
    private static final double PRECIO_PALCO = 28000.0;
    private static final double PRECIO_PLATEA_BAJA = 22000.0;
    private static final double PRECIO_PLATEA_ALTA = 18000.0;
    private static final double PRECIO_GALERIA = 12000.0;

    // Dimensiones y representación de asientos fijos
    private static final int FILAS_PB_PA_G = 9; // Filas a, b, c, d, e, f, g, h, i (Platea/Galería)
    private static final int COLUMNAS_FPG = 10;
    private static boolean[][] asientosPlateaGaleria = new boolean[FILAS_PB_PA_G][COLUMNAS_FPG]; // false = libre 'L'
    
    // VIP y Palco (Arreglos Fijos)
    private static boolean[] asientosVip = new boolean[20]; // v1 a v20
    private static boolean[] asientosPalco = new boolean[10]; // p1 a p10
    
    // Listas para la Gestión de Datos de Ventas (ArrayLists Dinámicos; CRUD: Leer, Actualizar, Eliminar)
    private static List<String> ventasId = new ArrayList<>(); // ID Único de Venta
    private static List<String> ventasAsientoCodigo = new ArrayList<>(); // Código del asiento (ej: "v5", "a3")
    private static List<Double> ventasPrecioFinal = new ArrayList<>(); // Precio final de venta
    private static List<Integer> ventasEdad = new ArrayList<>();
    private static List<Character> ventasGenero = new ArrayList<>();

    // Contadores Estáticos (Variables Estáticas)
    private static int siguienteIdVenta = 1;

    // Scanner para entrada del usuario
    private static final Scanner sc = new Scanner(System.in);

    //Método principal que ejecuta el menú interactivo.
    public static void main(String[] args) {
        // Inicialización de Arreglos (Todos inician en false/libre por defecto)
        inicializarAsientos();

        int opcion;
        // Bucle do-while para el menú interactivo
        do {
            mostrarMenuPrincipal();
            opcion = leerEnteroSeguro("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    comprarEntrada();
                    break;
                case 2:
                    mostrarMapaAsientos();
                    break;
                case 3:
                    mostrarResumenVentas();
                    break;
                case 4:
                    cancelarVenta();
                    break;
                case 5:
                    System.out.println("Saliendo del sistema. ¡Gracias por su uso!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
            // Esperar antes de continuar, a menos que sea la opción Salir
            if (opcion != 5) {
                System.out.println("\nPresione ENTER para continuar...");
                sc.nextLine(); 
            }
        } while (opcion != 5);
        sc.close();
    }

    //Inicializa los arreglos de asientos.
    private static void inicializarAsientos() {
        // Arreglos booleanos se inicializan en 'false' por defecto (Libre)
        // for (int i = 0; i < asientosVip.length; i++) { asientosVip[i] = false; }
        // Etc.
    }

    //Muestra el menú principal en la consola
    private static void mostrarMenuPrincipal() {
        System.out.println("\n==============================================");
        System.out.println("       SISTEMA DE VENTA TEATRO MORO         ");
        System.out.println("==============================================");
        System.out.println("1) Comprar Entrada");
        System.out.println("2) Ver Asientos Disponibles");
        System.out.println("3) Ver Resumen de Ventas");
        System.out.println("4) Cancelar Venta");
        System.out.println("5) Salir");
    }

    //Para leer una entrada entera de forma segura
    private static int leerEnteroSeguro(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                // Capturar el entero
                int valor = sc.nextInt();
                sc.nextLine(); 
                return valor;
            } catch (InputMismatchException e) {
                // Captura de error si se introduce texto en lugar de número
                System.out.println("Entrada inválida. Por favor, ingrese un número entero.");
                sc.nextLine(); // Limpiar entrada
            }
        }
    }

    //Lógica para el proceso completo de compra de una entrada.
    private static void comprarEntrada() {
        mostrarMapaAsientos();
        System.out.println("\n--- Proceso de Compra ---");
        
        // 1. Solicitud de asiento y validación
        System.out.print("Ingrese el código del asiento (ej: v5, a3, i10): ");
        String codigoAsiento = sc.nextLine().trim().toLowerCase();

        // Obtener índice y precio base. Si es -1, el código es inválido o no existe.
        int[] indices = mapearAsientoAIndices(codigoAsiento);
        if (indices [0] == -1) {
            System.out.println("ERROR: Código de asiento inválido o fuera de rango.");
            return;
        }

        int filaIndex = indices [0];
        int columnaIndex = indices[1];
        double precioBase = obtenerPrecioBase(codigoAsiento);

        // 2. Validación de disponibilidad
        if (estaAsientoOcupado(filaIndex, columnaIndex, codigoAsiento)) {
            System.out.println("ERROR: El asiento " + codigoAsiento.toUpperCase() + " ya está ocupado ('O').");
            return;
        }

        // 3. Solicitud de datos del cliente
        int edad = leerEnteroSeguro("Ingrese la edad del cliente: ");
        if (edad < 0 || edad > 120) { // Validación de datos
             System.out.println("Edad inválida. Cancelando venta.");
             return;
        }
        
        char genero = solicitarGenero();
        
        System.out.print("¿Es estudiante? (s/n): ");
        boolean esEstudiante = sc.nextLine().trim().equalsIgnoreCase("s");

        // 4. Cálculo de Descuentos y Precio Final
        double porcentajeDescuento = calcularDescuento(edad, genero, esEstudiante);
        double descuento = precioBase * porcentajeDescuento;
        double precioFinal = precioBase - descuento;

        // 5. Registro de Venta y Actualización de Asiento
        registrarVenta(filaIndex, columnaIndex, codigoAsiento, precioFinal, edad, genero);
        
        // 6. Impresión de Boleta
        System.out.println("\n¡Compra exitosa! Generando boleta...");
        imprimirBoleta(ventasId.size() - 1, precioBase, descuento);
    }
    
    //Solicita el género del cliente hasta obtener una entrada válida
    private static char solicitarGenero() {
        while (true) {
            System.out.print("Ingrese el género (M/H/O): ");
            String entrada = sc.nextLine().trim().toUpperCase();
            if (entrada.length() == 1) {
                char g = entrada.charAt(0);
                if (g == 'M' || g == 'H' || g == 'O') {
                    return g;
                }
            }
            System.out.println("Género inválido. Use M, H u O.");
        }
    }


    //Mapea un código de asiento a su fila/columna interna.
    private static int[] mapearAsientoAIndices(String codigo) {
        char tipo = codigo.charAt(0);
        int numero;
        
        try {
            // Intentamos obtener el número del asiento (ej. el '5' de 'v5')
            numero = Integer.parseInt(codigo.substring(1));
        } catch (NumberFormatException e) {
            return new int[]{-1, -1};
        }

        switch (tipo) {
            case 'v': // VIP: 1-20
                if (numero >= 1 && numero <= 20) return new int[]{9, numero - 1}; 
                break;
            case 'p': // Palco: 1-10
                if (numero >= 1 && numero <= 10) return new int[]{10, numero - 1};
                break;
            default: // Platea/Galeria (a-i): 1-10
                if (numero >= 1 && numero <= 10) {
                    // Mapeo de 'a' a fila 0, 'b' a fila 1, ..., 'i' a fila 8
                    int filaIndex = tipo - 'a';
                    if (filaIndex >= 0 && filaIndex < FILAS_PB_PA_G) {
                        return new int[]{filaIndex, numero - 1};
                    }
                }
                break;
        }
        return new int[]{-1, -1}; // Asiento no válido o fuera de rango
    }
    
    //Obtiene el precio base del asiento según su código.
    private static double obtenerPrecioBase(String codigo) {
        char tipo = codigo.charAt(0);
        switch (tipo) {
            case 'v': return PRECIO_VIP;
            case 'p': return PRECIO_PALCO;
            case 'a': 
            case 'b': return PRECIO_PLATEA_BAJA;
            case 'c': 
            case 'd': 
            case 'e': return PRECIO_PLATEA_ALTA;
            case 'f': 
            case 'g': 
            case 'h': 
            case 'i': return PRECIO_GALERIA;
            default: return 0.0;
        }
    }
    
    //Verifica si un asiento está ocupado.
    private static boolean estaAsientoOcupado(int filaIndex, int columnaIndex, String codigo) {
        char tipo = codigo.charAt(0);
        if (tipo == 'v' && filaIndex == 9) {
            return asientosVip[columnaIndex];
        } else if (tipo == 'p' && filaIndex == 10) {
            return asientosPalco[columnaIndex];
        } else if (filaIndex >= 0 && filaIndex < FILAS_PB_PA_G) {
            return asientosPlateaGaleria[filaIndex][columnaIndex];
        }
        return true; // Si cae aquí, algo salió mal o es un índice no mapeado
    }
    
    //Calcula el porcentaje de descuento aplicable (0.0 a 1.0).
    private static double calcularDescuento(int edad, char genero, boolean esEstudiante) {
        double descuento = 0.0;

        // Establecer prioridad de descuentos
        if (edad >= 60) { // Prioridad 1: Tercera Edad
            descuento = 0.30;
        } else if (esEstudiante) { // Prioridad 2: Estudiante
            descuento = 0.25;
        } else if (genero == 'M') { // Prioridad 3: Mujer
            descuento = 0.07;
        } else if (edad <= 18 && edad >= 0) { // Prioridad 4: Niño
            descuento = 0.05;
        }

    return descuento;
    }
    
    //Registra la venta y marca el asiento como ocupado (CRUD: CREATE)
    private static void registrarVenta(int filaIndex, int columnaIndex, String codigoAsiento, 
                                       double precioFinal, int edad, char genero) {
        
        String idVenta = "VNT" + String.format("%04d", siguienteIdVenta++);
        
        // Agregar a las listas paralelas
        ventasId.add(idVenta);
        ventasAsientoCodigo.add(codigoAsiento.toUpperCase());
        ventasPrecioFinal.add(precioFinal);
        ventasEdad.add(edad);
        ventasGenero.add(genero);

        // Actualizar estado del asiento
        char tipo = codigoAsiento.charAt(0);
        if (tipo == 'v') {
            asientosVip[columnaIndex] = true;
        } else if (tipo == 'p') {
            asientosPalco[columnaIndex] = true;
        } else {
            asientosPlateaGaleria[filaIndex][columnaIndex] = true;
        }
    }

    //Imprime la boleta de compra.
    private static void imprimirBoleta(int index, double precioBase, double descuentoAplicado) {
        String id = ventasId.get(index);
        String asiento = ventasAsientoCodigo.get(index);
        double finalPrice = ventasPrecioFinal.get(index);
        int edad = ventasEdad.get(index);
        
        System.out.println("==============================================");
        System.out.println("             BOLETA DE VENTA - TEATRO MORO    ");
        System.out.println("==============================================");
        System.out.println("ID Venta: " + id);
        System.out.println("Asiento: " + asiento);
        System.out.println("Edad Cliente: " + edad);
        System.out.printf("Precio Base: $%,.2f\n", precioBase);
        System.out.printf("Descuento Aplicado: $%,.2f\n", descuentoAplicado);
        System.out.println("----------------------------------------------");
        System.out.printf("Total a Pagar: $%,.2f\n", finalPrice); // Uso de expresión aritmética y formato
        System.out.println("==============================================");
        System.out.println("Gracias por su compra, disfrute la función.");
    }
    
    //Muestra el mapa visual de asientos disponibles y ocupados.
    //Utiliza ciclos for anidados para las secciones Platea/Galería
    private static void mostrarMapaAsientos() {
        System.out.println("\n==============================================");
        System.out.println("         MAPA DE ASISTENCIA (L=Libre, O=Ocupado)   ");
        System.out.println("==============================================");
        
        // VIP y Palco (Arreglos 1D)
        System.out.print("VIP ($"+ PRECIO_VIP +"): ");
        for (int i = 0; i < asientosVip.length; i++) {
            System.out.print((asientosVip[i] ? "O" : "L") + (i + 1));
            if (i < asientosVip.length - 1) { // Añade coma solo si no es el último
                System.out.print(", ");
            }
        }
        System.out.println();
        
        System.out.print("Palco ($"+ PRECIO_PALCO +"): ");
        for (int i = 0; i < asientosPalco.length; i++) {
            System.out.print((asientosPalco[i] ? "O" : "L") + (i + 1));
            if (i < asientosPalco.length - 1) { // Añade coma solo si no es el último
                System.out.print(", ");
            }
        }
        System.out.println("\n");
        
        // Platea y Galería (Arreglo 2D)
        System.out.println("FILA | 1  2  3  4  5  6  7  8  9  10 | ZONA / PRECIO");
        System.out.println("----------------------------------------------");
        
        char filaLetra = 'a';
        for (int f = 0; f < FILAS_PB_PA_G; f++) {
            System.out.print(filaLetra + "    | ");
            for (int c = 0; c < COLUMNAS_FPG; c++) {
                System.out.print(asientosPlateaGaleria[f][c] ? "O  " : "L  "); // Recorrido de arreglo bidimensional
            }
            
            // Etiquetado de Zonas
            if (f <= 1) { // a, b
                System.out.println("| Platea Baja ($"+ PRECIO_PLATEA_BAJA +")");
            } else if (f <= 4) { // c, d, e
                System.out.println("| Platea Alta ($"+ PRECIO_PLATEA_ALTA +")");
            } else { // f, g, h, i
                System.out.println("| Galería ($"+ PRECIO_GALERIA +")");
            }
            filaLetra++;
        }
        System.out.println("==============================================");
    }
    
    //Muestra un resumen general de todas las ventas (CRUD: READ)
    private static void mostrarResumenVentas() {
        if (ventasId.isEmpty()) {
            System.out.println("No hay ventas registradas aún.");
            return;
        }

        System.out.println("\n==============================================");
        System.out.println("             RESUMEN DE VENTAS                ");
        System.out.println("==============================================");
        
        double totalIngresos = 0.0;
        int totalVentas = ventasId.size();
        
        // Iteración sobre las listas paralelas
        for (int i = 0; i < totalVentas; i++) {
            String asiento = ventasAsientoCodigo.get(i);
            double precio = ventasPrecioFinal.get(i);
            String id = ventasId.get(i);
            
            System.out.printf("ID: %s | Asiento: %-5s | Edad: %-3d | Precio Final: $%,.2f\n", 
                              id, asiento, ventasEdad.get(i), precio);
            totalIngresos += precio;
        }

        System.out.println("----------------------------------------------");
        System.out.println("Total de entradas vendidas: " + totalVentas);
        System.out.printf("Total de ingresos: $%,.2f\n", totalIngresos);
        System.out.println("==============================================");
    }
    
    //Permite cancelar una venta por ID (CRUD: DELETE)
    private static void cancelarVenta() {
        if (ventasId.isEmpty()) {
            System.out.println("No hay ventas para cancelar.");
            return;
        }

        System.out.println("\n--- Cancelación de Venta ---");
        System.out.print("Ingrese el ID de la venta a cancelar (ej: VNT0001): ");
        String idConsulta = sc.nextLine().trim().toUpperCase();

        int index = -1;
        // Buscar el ID en la lista (READ)
        for (int i = 0; i < ventasId.size(); i++) {
            if (ventasId.get(i).equals(idConsulta)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("ERROR: ID de venta no encontrado.");
            return;
        }

        // Obtener datos del asiento a liberar
        String codigoAsiento = ventasAsientoCodigo.get(index).toLowerCase();
        int[] indices = mapearAsientoAIndices(codigoAsiento);
        int filaIndex = indices[0];
        int columnaIndex = indices[1];
        
        // 1. Liberar el asiento
        char tipo = codigoAsiento.charAt(0);
        if (tipo == 'v') {
            asientosVip[columnaIndex] = false;
        } else if (tipo == 'p') {
            asientosPalco[columnaIndex] = false;
        } else {
            asientosPlateaGaleria[filaIndex][columnaIndex] = false;
        }

        // 2. Eliminar el registro de las listas dinámicas (DELETE)
        ventasId.remove(index);
        ventasAsientoCodigo.remove(index);
        ventasPrecioFinal.remove(index);
        ventasEdad.remove(index);
        ventasGenero.remove(index);

        System.out.println("Venta con ID " + idConsulta + " cancelada exitosamente.");
        System.out.println("Asiento " + codigoAsiento.toUpperCase() + " liberado ('L').");
    }
}