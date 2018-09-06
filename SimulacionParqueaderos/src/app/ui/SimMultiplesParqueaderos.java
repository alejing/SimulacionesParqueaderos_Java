package app.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author alejandrofranco
 */
public class SimMultiplesParqueaderos extends javax.swing.JDialog implements Runnable {
    
    boolean cronometroActivo, vehiculosGeneradosAtendidos;
    int horaDia, vGenerados = 10, vAtendidos, vTransito;
    Thread hilo;
    ArrayList<Parqueadero> parqueaderos;
    GestionBD gestionBD;
  
    public SimMultiplesParqueaderos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // Ubica en el centro la ventana por defecto
        this.setLocationRelativeTo(null);
        // Pone la imagen de bogotá por defecto
        Imagen Imagen = new Imagen();
        jPanel1.add(Imagen);
        jPanel1.repaint();
        
        // Se crea el contenedor de todos los parqueaderos a simular
        parqueaderos = new ArrayList();
        
        // Se cargan todos los parqueaderos por defecto en el sistema
        cargarParqueaderos();
        
        // Por defecto se carga el P1 como estado inicial para la interface
        Parqueadero p;
        p = parqueaderos.get(0); // P1
        estadoParqueadero((int)p.getHoraInicioJornadaParqueadero(), 
                          (int)p.getHoraFinJornadaParqueadero(), 
                          (int)p.getCuposTotales(), 
                          (int)p.getPorcentajeCuposDiscapacidad(),
                          (int)p.getMiu(),
                          p.getHorasPico(),
                          1);
        
        // Botón Detener inactivo por defecto
        jButton52.setEnabled(false);
        
        // Pone los parqueaderos es su estado inicial
        reiniciarEstadoParqueaderos();
        //reiniciarCuposDisponiblesBD();
        
        // Instancio un objeto de tipo Gestion BD para manipular la BD
        gestionBD = new GestionBD();
    }
    // Cronómetro para la simulación dinámica
    @Override
    public void run(){
        Integer minutos = 0 , segundos = 0, milesimas = 0;
        //min es minutos, seg es segundos y mil es milesimas de segundo
        String min="", seg="", mil="";
        horaDia = 0;
        try
        {
            //Mientras cronometroActivo sea verdadero entonces seguira
            //aumentando el tiempo
            jLabel2.setText(convertirHmilitarHamPm(horaDia));
            actualizarParqueaderos(horaDia);
            while( cronometroActivo )
            {
                Thread.sleep( 4 );
                //Incrementamos 4 milesimas de segundo
                milesimas += 4;
                
           
                //Cuando llega a 1000 osea 1 segundo aumenta 1 segundo
                //y las milesimas de segundo de nuevo a 0
                if( milesimas == 1000 )
                {
                    milesimas = 0;
                    segundos += 1;
                    
                    // cada 12 segundos es 1 hora del día 
                    // 24 horas = 4.8 minutos de simulación
                    
                    if(segundos % (Integer)jSpinner1.getValue() == 0){ 
                        horaDia += 1;
                        jLabel2.setText(convertirHmilitarHamPm(horaDia));
                        
                        if(!vehiculosGeneradosAtendidos){
                            actualizarParqueaderos(horaDia);
                        }else{
                            reiniciarEstadoParqueaderos();
                            cronometroActivo = false;
                            jLabel5.setText("Fin de la simulación.");
                            jButton51.setEnabled(true);
                            jSpinner1.setEnabled(true);
                            jSpinner2.setEnabled(true);
                            jButton52.setEnabled(false);
                        }
                        
                        if(horaDia == 24){
                            cronometroActivo = false;
                            jLabel5.setText("Fin de la simulación.");
                            jButton51.setEnabled(true);
                            jSpinner1.setEnabled(true);
                            jSpinner2.setEnabled(true);
                            jButton52.setEnabled(false);
                        }
                    }
                    //Si los segundos llegan a 60 entonces aumenta 1 los minutos
                    //y los segundos vuelven a 0
                    if( segundos == 60 )
                    {
                        segundos = 0;
                        minutos++;
                    }
                }

                //Esto solamente es estetica para que siempre este en formato
                //00:00:000
                if( minutos < 10 ) min = "0" + minutos;
                else min = minutos.toString();
                if( segundos < 10 ) seg = "0" + segundos;
                else seg = segundos.toString();
                 
                if( milesimas < 10 ) mil = "00" + milesimas;
                else if( milesimas < 100 ) mil = "0" + milesimas;
                else mil = milesimas.toString();
                 
                //Colocamos en la etiqueta la informacion
                jLabel1.setText( min + ":" + seg + ":" + mil ); 

            }
        }catch(Exception e){}
        //Cuando se reincie se coloca nuevamente en 00:00:000
        //jLabel1.setText( "00:00:000" );
    }
    
    //Iniciar el cronometro poniendo cronometroActivo 
    //en verdadero para que entre en el while
    public void iniciarCronometro() {
        cronometroActivo = true;
        hilo = new Thread( this );
        hilo.start();
    }
  
    //Esto es para parar el cronometro
    public void pararCronometro(){
        cronometroActivo = false;
        jLabel2.setText(convertirHmilitarHamPm(0));
    }
    
    // Convertir hora militar a hora natural am/pm
    public String convertirHmilitarHamPm(int horaMilitar){
        String hora;
        
        if(horaMilitar == 0){
            hora = "12:00 AM";
        }else if(horaMilitar < 12){
            hora = horaMilitar+ ":00 AM";
        }else if(horaMilitar == 12){
            hora = "12:00 M";
        }else{
            hora = (horaMilitar-12)+ ":00 PM";
        }

        return hora;
    }
    
    // Método para cargar todos los parqueaderos caracterizados en las encuestas
    private void cargarParqueaderos(){
        
        // Se eliminan todos los elementos del ArrayList
        parqueaderos.clear();
        
        Parqueadero p;
        /*
            Parqueadero 1: 6 am a 7 pm, 17 cupos, 14% discapacitados, 
            110 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(1,6,19,17,14,110, 1, 0, 17);
        double[] hp1 = new double[6];
        hp1[0] = 11; hp1[2] = 0; hp1[4] = 0;
        hp1[1] = 13; hp1[3] = 0; hp1[5] = 0;
        p.setHorasPico(hp1);
        parqueaderos.add(p);
        /*
            Parqueadero 2: 7 am a 8 pm, 39 cupos, 5% discapacitados, 
            200 min ocupación (miu), hora pico de 5 pm a 8 pm
        */
        p = new Parqueadero(2,7,20,39,5,200, 1, 0, 39);
        double[] hp2 = new double[6];
        hp2[0] = 17; hp2[2] = 0; hp2[4] = 0;
        hp2[1] = 20; hp2[3] = 0; hp2[5] = 0;
        p.setHorasPico(hp2);
        parqueaderos.add(p);
        /*
            Parqueadero 3: 7 am a 10 pm, 30 cupos, 12% discapacitados, 
            250 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(3,7,22,30,12,250, 1, 0, 30);
        double[] hp3 = new double[6];
        hp3[0] = 11; hp3[2] = 0; hp3[4] = 0;
        hp3[1] = 13; hp3[3] = 0; hp3[5] = 0;
        p.setHorasPico(hp3);
        parqueaderos.add(p);
        /*
            Parqueadero 4: 7 am a 8 pm, 73 cupos, 3% discapacitados, 
            105 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(4,7,20,73,3,105, 1, 0, 73);
        double[] hp4 = new double[6];
        hp4[0] = 11; hp4[2] = 0; hp4[4] = 0;
        hp4[1] = 13; hp4[3] = 0; hp4[5] = 0;
        p.setHorasPico(hp4);
        parqueaderos.add(p);
        /*
            Parqueadero 5: 8 am a 9 pm, 54 cupos, 4% discapacitados, 
            80 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(5,8,21,54,4,80, 1, 0, 54);
        double[] hp5 = new double[6];
        hp5[0] = 17; hp5[2] = 0; hp5[4] = 0;
        hp5[1] = 20; hp5[3] = 0; hp5[5] = 0;
        p.setHorasPico(hp5);
        parqueaderos.add(p);
        /*
            Parqueadero 6: 6 am a 10 pm, 68 cupos, 1% discapacitados, 
            55 min ocupación (miu), hora pico de 6 am a 10 am y 5 pm 10 pm 
        */
        p = new Parqueadero(6,6,22,68,1,80, 1, 0, 68);
        double[] hp6 = new double[6];
        hp6[0] = 6; hp6[2] = 17; hp6[4] = 0;
        hp6[1] = 10; hp6[3] = 22; hp6[5] = 0;
        p.setHorasPico(hp6);
        parqueaderos.add(p);
        /*
            Parqueadero 7: 5 am a 11 pm, 52 cupos, 2% discapacitados, 
            70 min ocupación (miu), hora pico de 6 am a 9 am 
        */
        p = new Parqueadero(7,5,23,52,2,70, 1, 0, 52);
        double[] hp7 = new double[6];
        hp7[0] = 6; hp7[2] = 0; hp7[4] = 0;
        hp7[1] = 9; hp7[3] = 0; hp7[5] = 0;
        p.setHorasPico(hp7);
        parqueaderos.add(p);
        /*
            Parqueadero 8: 0 am a 12 pm, 42 cupos, 3% discapacitados, 
            60 min ocupación (miu), hora pico de 6 am a 9 am 
        */
        p = new Parqueadero(8,0,24,42,3,60, 1, 0, 42);
        double[] hp8 = new double[6];
        hp8[0] = 6; hp8[2] = 0; hp8[4] = 0;
        hp8[1] = 9; hp8[3] = 0; hp8[5] = 0;
        p.setHorasPico(hp8);
        parqueaderos.add(p);
        /*
            Parqueadero 9: 0 am a 12 pm, 12 cupos, 0% discapacitados, 
            260 min ocupación (miu), hora pico de 5 pm a 8 pm 
        */
        p = new Parqueadero(9,0,24,12,0,260, 1, 0, 12);
        double[] hp9 = new double[6];
        hp9[0] = 17; hp9[2] = 0; hp9[4] = 0;
        hp9[1] = 20; hp9[3] = 0; hp9[5] = 0;
        p.setHorasPico(hp9);
        parqueaderos.add(p);
        /*
            Parqueadero 10: 5 am a 10 pm, 88 cupos, 6% discapacitados, 
            60 min ocupación (miu), hora pico de 5 pm a 8 pm 
        */
        p = new Parqueadero(10,5,22,88,6,60, 1, 0, 88);
        double[] hp10 = new double[6];
        hp10[0] = 17; hp10[2] = 0; hp10[4] = 0;
        hp10[1] = 20; hp10[3] = 0; hp10[5] = 0;
        p.setHorasPico(hp10);
        parqueaderos.add(p);
        /*
            Parqueadero 11: 5 am a 10 pm, 50 cupos, 2% discapacitados, 
            88 min ocupación (miu), hora pico de 6 am a 9 am 
        */
        p = new Parqueadero(11,5,22,50,2,88, 1, 0, 50);
        double[] hp11 = new double[6];
        hp11[0] = 6; hp11[2] = 0; hp11[4] = 0;
        hp11[1] = 9; hp11[3] = 0; hp11[5] = 0;
        p.setHorasPico(hp11);
        parqueaderos.add(p);
         /*
            Parqueadero 12: 0 am a 12 pm, 75 cupos, 9% discapacitados, 
            60 min ocupación (miu), hora pico de 6 am a 9 am 
        */
        p = new Parqueadero(12,0,24,75,9,60, 1, 0, 75);
        double[] hp12 = new double[6];
        hp12[0] = 6; hp12[2] = 0; hp12[4] = 0;
        hp12[1] = 9; hp12[3] = 0; hp12[5] = 0;
        p.setHorasPico(hp12);
        parqueaderos.add(p);
        /*
            Parqueadero 13: 6 am a 5 pm, 38 cupos, 2% discapacitados, 
            90 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(13,6,17,38,2,90, 1, 0, 38);
        double[] hp13 = new double[6];
        hp13[0] = 11; hp13[2] = 0; hp13[4] = 0;
        hp13[1] = 13; hp13[3] = 0; hp13[5] = 0;
        p.setHorasPico(hp13);
        parqueaderos.add(p);
        /*
            Parqueadero 14: 0 am a 12 pm, 50 cupos, 7% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(14,0,24,50,7,120, 1, 0, 50);
        double[] hp14 = new double[6];
        hp14[0] = 11; hp14[2] = 0; hp14[4] = 0;
        hp14[1] = 13; hp14[3] = 0; hp14[5] = 0;
        p.setHorasPico(hp14);
        parqueaderos.add(p);
        /*
            Parqueadero 15: 7 am a 9 pm, 42 cupos, 7% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(15,7,21,42,3,120, 1, 0, 42);
        double[] hp15 = new double[6];
        hp15[0] = 11; hp15[2] = 0; hp15[4] = 0;
        hp15[1] = 13; hp15[3] = 0; hp15[5] = 0;
        p.setHorasPico(hp15);
        parqueaderos.add(p);
        /*
            Parqueadero 16: 6 am a 10 pm, 73 cupos, 12% discapacitados, 
            150 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(16,6,22,73,12,150, 1, 0, 73);
        double[] hp16 = new double[6];
        hp16[0] = 11; hp16[2] = 0; hp16[4] = 0;
        hp16[1] = 13; hp16[3] = 0; hp16[5] = 0;
        p.setHorasPico(hp16);
        parqueaderos.add(p);
        /*
            Parqueadero 17: 8 am a 10 pm, 62 cupos, 15% discapacitados, 
            150 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(17,8,22,62,15,150, 1, 0, 62);
        double[] hp17 = new double[6];
        hp17[0] = 11; hp17[2] = 0; hp17[4] = 0;
        hp17[1] = 13; hp17[3] = 0; hp17[5] = 0;
        p.setHorasPico(hp17);
        parqueaderos.add(p);
        /*
            Parqueadero 18: 6 am a 10 pm, 71 cupos, 3% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(18,6,22,71,3,120, 1, 0, 71);
        double[] hp18 = new double[6];
        hp18[0] = 11; hp18[2] = 0; hp18[4] = 0;
        hp18[1] = 13; hp18[3] = 0; hp18[5] = 0;
        p.setHorasPico(hp18);
        parqueaderos.add(p);
        /*
            Parqueadero 19: 6 am a 5 pm, 12 cupos, 4% discapacitados, 
            90 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(19,6,17,12,4,90, 1, 0, 12);
        double[] hp19 = new double[6];
        hp19[0] = 11; hp19[2] = 0; hp19[4] = 0;
        hp19[1] = 13; hp19[3] = 0; hp19[5] = 0;
        p.setHorasPico(hp19);
        parqueaderos.add(p);
        /*
            Parqueadero 20: 6 am a 5 pm, 43 cupos, 5% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(20,6,17,43,5,120, 1, 0, 43);
        double[] hp20 = new double[6];
        hp20[0] = 11; hp20[2] = 0; hp20[4] = 0;
        hp20[1] = 13; hp20[3] = 0; hp20[5] = 0;
        p.setHorasPico(hp20);
        parqueaderos.add(p);
        /*
            Parqueadero 21: 6 am a 10 pm, 43 cupos, 12% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm y 5 pm a 8 pm 
        */
        p = new Parqueadero(21,6,22,43,12,120, 1, 0, 43);
        double[] hp21 = new double[6];
        hp21[0] = 11; hp21[2] = 17; hp21[4] = 0;
        hp21[1] = 13; hp21[3] = 20; hp21[5] = 0;
        p.setHorasPico(hp21);
        parqueaderos.add(p);
        /*
            Parqueadero 22: 7 am a 9 pm, 24 cupos, 3% discapacitados, 
            90 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(22,7,21,24,3,90, 1, 0, 24);
        double[] hp22 = new double[6];
        hp22[0] = 11; hp22[2] = 0; hp22[4] = 0;
        hp22[1] = 13; hp22[3] = 0; hp22[5] = 0;
        p.setHorasPico(hp22);
        parqueaderos.add(p);
        /*
            Parqueadero 23: 0 am a 12 pm, 91 cupos, 13% discapacitados, 
            180 min ocupación (miu), hora pico de 11 am a 1 pm y 5 pm a 11 pm 
        */
        p = new Parqueadero(23,0,24,91,13,180, 1, 0, 91);
        double[] hp23 = new double[6];
        hp23[0] = 11; hp23[2] = 17; hp23[4] = 0;
        hp23[1] = 14; hp23[3] = 23; hp23[5] = 0;
        p.setHorasPico(hp23);
        parqueaderos.add(p);
        /*
            Parqueadero 24: 6 am a 10 pm, 23 cupos, 5% discapacitados, 
            110 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(24,6,22,23,5,110, 1, 0, 23);
        double[] hp24 = new double[6];
        hp24[0] = 11; hp24[2] = 0; hp24[4] = 0;
        hp24[1] = 13; hp24[3] = 0; hp24[5] = 0;
        p.setHorasPico(hp24);
        parqueaderos.add(p);
        /*
            Parqueadero 25: 6 am a 8 pm, 48 cupos, 6% discapacitados, 
            118 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(25,6,20,48,6,118, 1, 0, 48);
        double[] hp25 = new double[6];
        hp25[0] = 11; hp25[2] = 0; hp25[4] = 0;
        hp25[1] = 13; hp25[3] = 0; hp25[5] = 0;
        p.setHorasPico(hp25);
        parqueaderos.add(p);
         /*
            Parqueadero 26: 6 am a 8 pm, 23 cupos, 2% discapacitados, 
            90 min ocupación (miu), hora pico de 11 am a 1 pm 
        */
        p = new Parqueadero(26,6,20,23,2,90, 1, 0, 23);
        double[] hp26 = new double[6];
        hp26[0] = 11; hp26[2] = 0; hp26[4] = 0;
        hp26[1] = 13; hp26[3] = 0; hp26[5] = 0;
        p.setHorasPico(hp26);
        parqueaderos.add(p);
         /*
            Parqueadero 27: 5 am a 10 pm, 25 cupos, 3% discapacitados, 
            150 min ocupación (miu), hora pico de 11 am a 2 pm y 6 pm a 9 pm 
        */
        p = new Parqueadero(27,5,22,25,3,150, 1, 0, 25);
        double[] hp27 = new double[6];
        hp27[0] = 11; hp27[2] = 18; hp27[4] = 0;
        hp27[1] = 14; hp27[3] = 21; hp27[5] = 0;
        p.setHorasPico(hp27);
        parqueaderos.add(p);
        /*
            Parqueadero 28: 5 am a 8 pm, 15 cupos, 4% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(28,5,20,15,4,120, 1, 0, 15);
        double[] hp28 = new double[6];
        hp28[0] = 11; hp28[2] = 0; hp28[4] = 0;
        hp28[1] = 13; hp28[3] = 0; hp28[5] = 0;
        p.setHorasPico(hp28);
        parqueaderos.add(p);
         /*
            Parqueadero 29: 6 am a 10 pm, 44 cupos, 6% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 2 pm y 5 pm a 9 pm
        */
        p = new Parqueadero(29,6,22,44,6,120, 1, 0, 44);
        double[] hp29 = new double[6];
        hp29[0] = 11; hp29[2] = 17; hp29[4] = 0;
        hp29[1] = 14; hp29[3] = 21; hp29[5] = 0;
        p.setHorasPico(hp29);
        parqueaderos.add(p);
         /*
            Parqueadero 30: 6 am a 6 pm, 26 cupos, 3% discapacitados, 
            150 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(30,6,18,26,3,150, 1, 0, 26);
        double[] hp30 = new double[6];
        hp30[0] = 11; hp30[2] = 0; hp30[4] = 0;
        hp30[1] = 13; hp30[3] = 0; hp30[5] = 0;
        p.setHorasPico(hp30);
        parqueaderos.add(p);
         /*
            Parqueadero 31: 0 am a 12 pm, 26 cupos, 7% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(31,0,24,26,7,120, 1, 0, 26);
        double[] hp31 = new double[6];
        hp31[0] = 11; hp31[2] = 0; hp31[4] = 0;
        hp31[1] = 13; hp31[3] = 0; hp31[5] = 0;
        p.setHorasPico(hp31);
        parqueaderos.add(p);
        /*
            Parqueadero 32: 7 am a 9 pm, 48 cupos, 8% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(32,7,21,48,8,120, 1, 0, 48);
        double[] hp32 = new double[6];
        hp32[0] = 11; hp32[2] = 0; hp32[4] = 0;
        hp32[1] = 13; hp32[3] = 0; hp32[5] = 0;
        p.setHorasPico(hp32);
        parqueaderos.add(p);
         /*
            Parqueadero 33: 5 am a 11 pm, 42 cupos, 12% discapacitados, 
            90 min ocupación (miu), hora pico de 11 am a 2 pm y 4 pm a 8 pm
        */
        p = new Parqueadero(33,5,23,42,12,90, 1, 0, 42);
        double[] hp33 = new double[6];
        hp33[0] = 11; hp33[2] = 16; hp33[4] = 0;
        hp33[1] = 14; hp33[3] = 20; hp33[5] = 0;
        p.setHorasPico(hp33);
        parqueaderos.add(p);
         /*
            Parqueadero 34: 0 am a 12 pm, 43 cupos, 2% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 3 pm
        */
        p = new Parqueadero(34,0,24,43,2,120, 1, 0, 43);
        double[] hp34 = new double[6];
        hp34[0] = 11; hp34[2] = 0; hp34[4] = 0;
        hp34[1] = 15; hp34[3] = 0; hp34[5] = 0;
        p.setHorasPico(hp34);
        parqueaderos.add(p);
        /*
            Parqueadero 35: 6 am a 10 pm, 63 cupos, 15% discapacitados, 
            150 min ocupación (miu), hora pico de 11 am a 1 pm y 6 pm a 8 pm
        */
        p = new Parqueadero(35,6,22,63,15,150, 1, 0, 63);
        double[] hp35 = new double[6];
        hp35[0] = 11; hp35[2] = 18; hp35[4] = 0;
        hp35[1] = 13; hp35[3] = 20; hp35[5] = 0;
        p.setHorasPico(hp35);
        parqueaderos.add(p);
        /*
            Parqueadero 36: 5 am a 9 pm, 7 cupos, 6% discapacitados, 
            90 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(36,5,21,7,6,90, 1, 0, 7);
        double[] hp36 = new double[6];
        hp36[0] = 11; hp36[2] = 0; hp36[4] = 0;
        hp36[1] = 13; hp36[3] = 0; hp36[5] = 0;
        p.setHorasPico(hp36);
        parqueaderos.add(p);
        /*
            Parqueadero 37: 6 am a 8 pm, 22 cupos, 6% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(37,6,20,22,6,120, 1, 0, 22);
        double[] hp37 = new double[6];
        hp37[0] = 11; hp37[2] = 0; hp37[4] = 0;
        hp37[1] = 13; hp37[3] = 0; hp37[5] = 0;
        p.setHorasPico(hp37);
        parqueaderos.add(p);
        /*
            Parqueadero 38: 0 am a 12 pm, 25 cupos, 2% discapacitados, 
            120 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(38,0,24,25,2,120, 1, 0, 25);
        double[] hp38 = new double[6];
        hp38[0] = 11; hp38[2] = 0; hp38[4] = 0;
        hp38[1] = 13; hp38[3] = 0; hp38[5] = 0;
        p.setHorasPico(hp38);
        parqueaderos.add(p);
        /*
            Parqueadero 39: 6 am a 20 pm, 30 cupos, 5% discapacitados, 
            90 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(39,6,20,30,5,90, 1, 0, 30);
        double[] hp39 = new double[6];
        hp39[0] = 11; hp39[2] = 0; hp39[4] = 0;
        hp39[1] = 13; hp39[3] = 0; hp39[5] = 0;
        p.setHorasPico(hp39);
        parqueaderos.add(p);
        /*
            Parqueadero 40: 6 am a 10 pm, 45 cupos, 1% discapacitados, 
            150 min ocupación (miu), hora pico de 6 am a 10 pm y 5 pm a 8 pm
        */
        p = new Parqueadero(40,6,22,45,1,150, 1, 0, 45);
        double[] hp40 = new double[6];
        hp40[0] = 6; hp40[2] = 17; hp40[4] = 0;
        hp40[1] = 10; hp40[3] = 18; hp40[5] = 0;
        p.setHorasPico(hp40);
        parqueaderos.add(p);
        /*
            Parqueadero 41: 0 am a 12 pm, 23 cupos, 12% discapacitados, 
            220 min ocupación (miu), hora pico de 5 pm a 8 pm
        */
        p = new Parqueadero(41,0,24,23,12,220, 1, 0, 23);
        double[] hp41 = new double[6];
        hp41[0] = 17; hp41[2] = 0; hp41[4] = 0;
        hp41[1] = 20; hp41[3] = 0; hp41[5] = 0;
        p.setHorasPico(hp41);
        parqueaderos.add(p);
        /*
            Parqueadero 42: 7 am a 11 pm, 93 cupos, 5% discapacitados, 
            180 min ocupación (miu), hora pico de 11 am a 1 pm y 5 pm a 8 pm
        */
        p = new Parqueadero(42,7,23,93,5,180, 1, 0, 93);
        double[] hp42 = new double[6];
        hp42[0] = 11; hp42[2] = 17; hp42[4] = 0;
        hp42[1] = 13; hp42[3] = 20; hp42[5] = 0;
        p.setHorasPico(hp42);
        parqueaderos.add(p);
        /*
            Parqueadero 43: 7 am a 10 pm, 43 cupos, 2% discapacitados, 
            240 min ocupación (miu), hora pico de 11 am a 1 pm
        */
        p = new Parqueadero(43,7,22,43,2,240, 1, 0, 43);
        double[] hp43 = new double[6];
        hp43[0] = 11; hp43[2] = 0; hp43[4] = 0;
        hp43[1] = 13; hp43[3] = 0; hp43[5] = 0;
        p.setHorasPico(hp43);
        parqueaderos.add(p);
        /*
            Parqueadero 44: 0 am a 12 pm, 25 cupos, 6% discapacitados, 
            150 min ocupación (miu), hora pico de 11 am a 3 pm y 9 pm a 10 pm
        */
        p = new Parqueadero(44,0,24,25,6,150, 1, 0, 25);
        double[] hp44 = new double[6];
        hp44[0] = 11; hp44[2] = 21; hp44[4] = 0;
        hp44[1] = 15; hp44[3] = 22; hp44[5] = 0;
        p.setHorasPico(hp44);
        parqueaderos.add(p);
         /*
            Parqueadero 45: 7 am a 10 pm, 22 cupos, 6% discapacitados, 
            180 min ocupación (miu), hora pico de 11 am a 3 pm y 6 pm a 7 pm
        */
        p = new Parqueadero(45,7,22,22,6,180, 1, 0, 22);
        double[] hp45 = new double[6];
        hp45[0] = 11; hp45[2] = 18; hp45[4] = 0;
        hp45[1] = 15; hp45[3] = 19; hp45[5] = 0;
        p.setHorasPico(hp45);
        parqueaderos.add(p);
        /*
            Parqueadero 46: 0 am a 12 pm, 23 cupos, 5% discapacitados, 
            150 min ocupación (miu), hora pico de 11 am a 2 pm y 7 pm a 10 pm
        */
        p = new Parqueadero(46,0,24,23,5,150, 1, 0, 23);
        double[] hp46 = new double[6];
        hp46[0] = 11; hp46[2] = 19; hp46[4] = 0;
        hp46[1] = 14; hp46[3] = 22; hp46[5] = 0;
        p.setHorasPico(hp46);
        parqueaderos.add(p);
        /*
            Parqueadero 47: 7 am a 10 pm, 48 cupos, 6% discapacitados, 
            300 min ocupación (miu), hora pico de 6 am a 9 am y 11 am a 2 pm
        */
        p = new Parqueadero(47,7,22,48,6,300, 1, 0, 48);
        double[] hp47 = new double[6];
        hp47[0] = 6; hp47[2] = 11; hp47[4] = 0;
        hp47[1] = 9; hp47[3] = 14; hp47[5] = 0;
        p.setHorasPico(hp47);
        parqueaderos.add(p);
        /*
            Parqueadero 48: 7 am a 10 pm, 28 cupos, 6% discapacitados, 
            360 min ocupación (miu), hora pico de 6 am a 9 am y 11 am a 2 pm
        */
        p = new Parqueadero(48,7,22,28,6,360, 1, 0, 28);
        double[] hp48 = new double[6];
        hp48[0] = 6; hp48[2] = 11; hp48[4] = 0;
        hp48[1] = 9; hp48[3] = 14; hp48[5] = 0;
        p.setHorasPico(hp48);
        parqueaderos.add(p);
        /*
            Parqueadero 49: 0 am a 12 pm, 19 cupos, 6% discapacitados, 
            240 min ocupación (miu), hora pico de 6 am a 9 am
        */
        p = new Parqueadero(49,0,24,19,6,240, 1, 0, 19);
        double[] hp49 = new double[6];
        hp49[0] = 6; hp49[2] = 0; hp49[4] = 0;
        hp49[1] = 9; hp49[3] = 0; hp49[5] = 0;
        p.setHorasPico(hp49);
        parqueaderos.add(p);
        /*
            Parqueadero 50: 0 am a 12 pm, 24 cupos, 5% discapacitados, 
            150 min ocupación (miu), hora pico de 6 am a 9 am y 11 am a 3 pm
        */
        p = new Parqueadero(50,0,24,24,5,150, 1, 0, 24);
        double[] hp50 = new double[6];
        hp50[0] = 6; hp50[2] = 11; hp50[4] = 0;
        hp50[1] = 9; hp50[3] = 15; hp50[5] = 0;
        p.setHorasPico(hp50);
        parqueaderos.add(p);
    }
    
    // Valida si el parqueadero esta en horario de funcionamiento
    public boolean validarHorarioLaboral(double HoraInicio, double HoraFin, int horaDia){
        
        boolean estado;
        estado = horaDia >= HoraInicio && horaDia <= HoraFin;
        return estado; // Estado true esta en horario laboral
    } 
    
    // Valida si el parqueadero esta en hora pico
    public boolean validarHoraPico(double[] horasPico, int horaDia){
        boolean estado;
        
        estado = (horaDia >= (int)horasPico[0] && horaDia <= (int)horasPico[1]) || 
                 (horaDia >= (int)horasPico[2] && horaDia <= (int)horasPico[3]) || 
                 (horaDia >= (int)horasPico[4] && horaDia <= (int)horasPico[5]);
        return estado; // Estado true esta en hora pico
    }
    
    // retorna el valor de Ro para la hora valle
    public double dameRoValle(){
        Random rnd = new Random(); // genera un numero entre 6 y 80
        return ((int)(rnd.nextDouble() * 75 + 6))/100.0;
    }
    
    // retorna el valor de Ro para la hora pico
    public double dameRoPico(){
        Random rnd = new Random(); // genera un numero entre 76 y 99
        return ((int)(rnd.nextDouble() * (99-76+1)+76))/100.0;
    }
    
    // Redondeo de cifras significativas de número
     public double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado=(resultado-parteEntera)*Math.pow(10, numeroDecimales);
        resultado=Math.round(resultado);
        resultado=(resultado/Math.pow(10, numeroDecimales))+parteEntera;
        return resultado;
    }
     
    // Actualiza el valor de L como esperado de cupos en el parqueadero
    public int dameL(double Ro, double lambda, double miu, int s, int cifrasSignificativas){
        // Se realizan los calculos de Ro, Po, Lq, Wq, W, L, Pw
        mms m = new mms();
        //double Ro = redondearDecimales(m.intencidadDeTraficoMMS(lambda, miu, s), cifrasSignificativas);
        double Po = redondearDecimales(m.PoMMS(lambda, miu, s, Ro), cifrasSignificativas);
        double Lq = redondearDecimales(m.LqMMS(lambda, miu, s, Po, Ro), cifrasSignificativas);
        double Wq = redondearDecimales(m.WqMMS(Lq, lambda), cifrasSignificativas);
        double  W = redondearDecimales(m.WMMS(Wq, miu), cifrasSignificativas);
        double  L = redondearDecimales(m.LMMS(lambda, W), cifrasSignificativas);
        double Pw = redondearDecimales(m.PwMMS(lambda, miu, s, Po, Ro), (cifrasSignificativas-2));
        
        return (int)L;
    }
    
    // devuleve el estado de ocupación del parqueadero
    public Color estadoVisualParqueadero(int L, int cuposTotales){
       Color miColor = new Color(0,0,0);
       /*
        if(L >= 0 && L <= (int)(0.1*cuposTotales)){ // 0 - 10%
            miColor = new Color(255,0,0); // Color rojo (muy pocos cupos disponibles)
        }else if(L >= (int)(0.11*cuposTotales) && L <= (int)(0.3*cuposTotales)){
            miColor = new Color(255,153,51); // Color naranja (pocos cupos disponibles)
        }else if(L >= (int)(0.51*cuposTotales) && L <= (int)(0.8*cuposTotales)){
            miColor = new Color(255,255,51); // Color amarillo (buenos cupos disponibles)
        }else if(L >= (int)(0.81*cuposTotales)){
            miColor = new Color(51,153,0); // Color verde (muy buenos cupos disponibles)
        }
        */
       if(L >= 0 && L <= (int)(0.1*cuposTotales)){ // 0 - 10%
            miColor = new Color(251,0,8); // Color rojo (muy pocos cupos disponibles)
        }else if(L >= (int)(0.11*cuposTotales) && L <= (int)(0.3*cuposTotales)){
            miColor = new Color(253,103,102); // Color rosa (pocos cupos disponibles)
        }else if(L >= (int)(0.31*cuposTotales) && L <= (int)(0.5*cuposTotales)){
            miColor = new Color(253,182,10); // Color naranja (media cupos disponibles)
        }else if(L >= (int)(0.51*cuposTotales) && L <= (int)(0.8*cuposTotales)){
            miColor = new Color(128,204,55); // Color verde claro (buenos cupos disponibles)
        }else if(L >= (int)(0.81*cuposTotales)){
            miColor = new Color(23,166,60); // Color verde (muy buenos cupos disponibles)
        }
        return miColor;
    }
    
    // Actualiza todos los parqueaderos
    public void actualizarParqueaderos(int horaDia){
        parqueadero("P1: ",jButton1, horaDia, parqueaderos.get(0)); // P1
        parqueadero("P2: ",jButton2, horaDia, parqueaderos.get(1)); // P2
        parqueadero("P3: ",jButton3, horaDia, parqueaderos.get(2)); // P3
        parqueadero("P4: ",jButton4, horaDia, parqueaderos.get(3)); // P4
        parqueadero("P5: ",jButton5, horaDia, parqueaderos.get(4)); // P5
        parqueadero("P6: ",jButton6, horaDia, parqueaderos.get(5)); // P6
        parqueadero("P7: ",jButton7, horaDia, parqueaderos.get(6)); // P7
        parqueadero("P8: ",jButton8, horaDia, parqueaderos.get(7)); // P8
        parqueadero("P9: ",jButton9, horaDia, parqueaderos.get(8)); // P9
        parqueadero("P10: ",jButton10, horaDia, parqueaderos.get(9)); // P10
        parqueadero("P11: ",jButton11, horaDia, parqueaderos.get(10)); // P11
        parqueadero("P12: ",jButton12, horaDia, parqueaderos.get(11)); // P12
        parqueadero("P13: ",jButton13, horaDia, parqueaderos.get(12)); // P13
        parqueadero("P14: ",jButton14, horaDia, parqueaderos.get(13)); // P14
        parqueadero("P15: ",jButton15, horaDia, parqueaderos.get(14)); // P15
        parqueadero("P16: ",jButton16, horaDia, parqueaderos.get(15)); // P16
        parqueadero("P17: ",jButton17, horaDia, parqueaderos.get(16)); // P17
        parqueadero("P18: ",jButton18, horaDia, parqueaderos.get(17)); // P18
        parqueadero("P19: ",jButton19, horaDia, parqueaderos.get(18)); // P19
        parqueadero("P20: ",jButton20, horaDia, parqueaderos.get(19)); // P20
        parqueadero("P21: ",jButton21, horaDia, parqueaderos.get(20)); // P21
        parqueadero("P22: ",jButton22, horaDia, parqueaderos.get(21)); // P22
        parqueadero("P23: ",jButton23, horaDia, parqueaderos.get(22)); // P23
        parqueadero("P24: ",jButton24, horaDia, parqueaderos.get(23)); // P24
        parqueadero("P25: ",jButton25, horaDia, parqueaderos.get(24)); // P25
        parqueadero("P26: ",jButton26, horaDia, parqueaderos.get(25)); // P26
        parqueadero("P27: ",jButton27, horaDia, parqueaderos.get(26)); // P27
        parqueadero("P28: ",jButton28, horaDia, parqueaderos.get(27)); // P28
        parqueadero("P29: ",jButton29, horaDia, parqueaderos.get(28)); // P29
        parqueadero("P30: ",jButton30, horaDia, parqueaderos.get(29)); // P30
        parqueadero("P31: ",jButton31, horaDia, parqueaderos.get(30)); // P31
        parqueadero("P32: ",jButton32, horaDia, parqueaderos.get(31)); // P32
        parqueadero("P33: ",jButton33, horaDia, parqueaderos.get(32)); // P33
        parqueadero("P34: ",jButton34, horaDia, parqueaderos.get(33)); // P34
        parqueadero("P35: ",jButton35, horaDia, parqueaderos.get(34)); // P35
        parqueadero("P36: ",jButton36, horaDia, parqueaderos.get(35)); // P36
        parqueadero("P37: ",jButton37, horaDia, parqueaderos.get(36)); // P37
        parqueadero("P38: ",jButton38, horaDia, parqueaderos.get(37)); // P38
        parqueadero("P39: ",jButton39, horaDia, parqueaderos.get(38)); // P39
        parqueadero("P40: ",jButton40, horaDia, parqueaderos.get(39)); // P40
        parqueadero("P41: ",jButton41, horaDia, parqueaderos.get(40)); // P41
        parqueadero("P42: ",jButton42, horaDia, parqueaderos.get(41)); // P42
        parqueadero("P43: ",jButton43, horaDia, parqueaderos.get(42)); // P43
        parqueadero("P44: ",jButton44, horaDia, parqueaderos.get(43)); // P44
        parqueadero("P45: ",jButton45, horaDia, parqueaderos.get(44)); // P45
        parqueadero("P46: ",jButton46, horaDia, parqueaderos.get(45)); // P46
        parqueadero("P47: ",jButton47, horaDia, parqueaderos.get(46)); // P47
        parqueadero("P48: ",jButton48, horaDia, parqueaderos.get(47)); // P48
        parqueadero("P49: ",jButton49, horaDia, parqueaderos.get(48)); // P49
        parqueadero("P50: ",jButton50, horaDia, parqueaderos.get(49)); // P50
    }
    
    // Estado de operación del parqueadero n
    public void parqueadero(String textP, javax.swing.JButton btn, int horaDia, Parqueadero p){
        
        int cupos = 0;
        double lambda;
        //Parqueadero p;
        //p = parqueaderos.get(0);
        btn.setText(textP+cupos+"/"+(int)p.getCuposTotales());
 
        if(validarHorarioLaboral(p.getHoraInicioJornadaParqueadero(), p.getHoraFinJornadaParqueadero(), horaDia)){
            // Esta en horario de funcionamiento  
            btn.setEnabled(true);
            if(validarHoraPico(p.getHorasPico(), horaDia)){
                // Esta en hora pico
                System.out.println(textP+"Abierto:: Hora Pico ...");
                 // Calculo de Ro y lambda para la hora
                double Ro = dameRoPico();
                lambda = (int)(Ro*p.getCuposTotales()*(60.0/p.getMiu()));
                dinamicaParqueadero(textP, lambda, p, btn);

            }else{
                // Esta en hora valle
                System.out.println(textP+" Abierto:: Hora Valle ...");
                // Calculo de Ro y lambda para la hora
                double Ro = dameRoValle();
                lambda = (int)(Ro*p.getCuposTotales()*(60.0/p.getMiu()));
                dinamicaParqueadero(textP, lambda, p, btn);
                
            }
        }else{
            // NO esta en horario de funcionamiento
            System.out.println(textP+"Cerrado ....");
            btn.setBackground(new Color(192, 192, 192));
            btn.setText(textP+(int)p.getCuposTotales()+"/"+(int)p.getCuposTotales());
            btn.setEnabled(false);
        }
        
        
    }
    
    // Actualización dinámica de todos los estados del parqueadero
    public void dinamicaParqueadero(String textP, double lambda, Parqueadero p, javax.swing.JButton btn){

            //System.out.println("Ro = " + Ro);
            //vGenerados += lambda; // Se almacenan los vehículos generados
            if(p.getT() == (int)Math.ceil(p.getMiu()/60.0)){
                System.out.println("TT = " + p.getT());
                System.out.println("LLambda = " + lambda);
                p.setT(p.getT()-1); // decremento en 1 las horas acumuladas
                // Actualizo los cupos dinámicos
                /// p.setCuposDinamicos(p.getCuposDinamicos()-(int)lambda);

                if(p.getCuposDinamicos() < 0){
                    // Aqui guardar en BD cuposDisponibles = 0
                    gestionBD.actualizarCuposDisponiblesBD(p.getId(),0);
                    btn.setText(textP+"0/"+(int)p.getCuposTotales());
                    btn.setBackground(new Color(255,0,0)); // Ocupado totalmente
                    p.setCuposDinamicos((int)(p.getCuposTotales()+p.getCuposDinamicos()));
                }else{
                    // Actualiza el estado de cupos dinamicamente según el estado de ocupación
                    if(p.getCuposDinamicos() < 0){
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),0);
                        btn.setText(textP+"0/"+(int)p.getCuposTotales());
                    }else if(p.getCuposDinamicos() > p.getCuposTotales()){
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),(int)p.getCuposTotales());
                        btn.setText(textP+((int)p.getCuposTotales())+"/"+(int)p.getCuposTotales());
                    }else{
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),p.getCuposDinamicos());
                        btn.setText(textP+(p.getCuposDinamicos())+"/"+(int)p.getCuposTotales());
                    }
                    // Actualiza el estado grafico del botón
                    btn.setBackground(estadoVisualParqueadero(p.getCuposDinamicos(), (int)p.getCuposTotales()));
                    p.setCuposDinamicos(p.getCuposDinamicos()-(int)lambda);
                    vAtendidos += p.getLambdaAnterior(); // Se almacenan lo vehículos atendidos
                    // Actualizó los cupos liberando los que ya se han atendido
                    p.setCuposDinamicos(p.getCuposDinamicos()+p.getLambdaAnterior());
                    // Aqui guardar en BD cuposDisponibles = getCuposDinamicos
                    /*
                    if(p.getCuposDinamicos() < 0){
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),0);
                    }else if(p.getCuposDinamicos() > p.getCuposTotales()){
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),(int)p.getCuposTotales());
                    }else{
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),p.getCuposDinamicos());
                    }
                    */
                    p.setLambdaAnterior((int)lambda);
                }

                //System.out.println("CCupos = " + p.getCuposDinamicos());
                /// vAtendidos += p.getLambdaAnterior(); // Se almacenan lo vehículos atendidos
                // Actualizó los cupos liberando los que ya se han atendido
                /// p.setCuposDinamicos(p.getCuposDinamicos()+p.getLambdaAnterior());
                /// p.setLambdaAnterior((int)lambda);
            }else{
                System.out.println("T = " + p.getT());
                System.out.println("Lambda = " + lambda);
                p.setT(p.getT()+1);
                /// p.setCuposDinamicos(((int)p.getCuposDinamicos()-(int)lambda));
                //System.out.println("Cupos = " + p.getCuposDinamicos());

                if(p.getCuposDinamicos() < 0){
                    // Aqui guardar en BD cuposDisponibles = 0
                    gestionBD.actualizarCuposDisponiblesBD(p.getId(),0);
                    btn.setText(textP+"0/"+(int)p.getCuposTotales());
                    btn.setBackground(new Color(255,0,0)); // Ocupado totalmente
                    p.setCuposDinamicos((int)(p.getCuposTotales()+p.getCuposDinamicos()));
                }else{
                    // Actualiza el estado de cupos dinamicamente según el estado de ocupación
                    if(p.getCuposDinamicos() < 0){
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),0);
                        btn.setText(textP+"0/"+(int)p.getCuposTotales());
                    }else if(p.getCuposDinamicos() > p.getCuposTotales()){
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),(int)p.getCuposTotales());
                        btn.setText(textP+((int)p.getCuposTotales())+"/"+(int)p.getCuposTotales());
                    }else{
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),p.getCuposDinamicos());
                        btn.setText(textP+(p.getCuposDinamicos())+"/"+(int)p.getCuposTotales());
                    }
                    btn.setBackground(estadoVisualParqueadero(p.getCuposDinamicos(), (int)p.getCuposTotales()));
                    p.setCuposDinamicos(((int)p.getCuposDinamicos()-(int)lambda));
                    // Aqui guardar en BD cuposDisponibles = getCuposDinamicos
                    /*
                    if(p.getCuposDinamicos() < 0){
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),0);
                    }else if(p.getCuposDinamicos() > p.getCuposTotales()){
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),(int)p.getCuposTotales());
                    }else{
                        gestionBD.actualizarCuposDisponiblesBD(p.getId(),p.getCuposDinamicos());
                    }
                    */
                    p.setLambdaAnterior((int)lambda);
                }

            }

            // Actualiza en numero de vehículos en transito
            vTransito = (vGenerados - vAtendidos); 
            
            if(vTransito < 0){
                vAtendidos = vGenerados;
                vTransito = vGenerados - vAtendidos;
                vehiculosGeneradosAtendidos = true;
            }
            // Visualiza los vehículos en el sistema
            jLabel21.setText(""+vGenerados);
            jLabel23.setText(""+vAtendidos);
            jLabel25.setText(""+vTransito);

    } 
    
    // Estado parqueadero individual
    private void estadoParqueadero(int horaInicio, int horaFin, int cupos, int cuposDiscapacidad, int miu, double[] horasPico, int parqueadero){
        jLabel8.setText(convertirHmilitarHamPm(horaInicio) + " - " + convertirHmilitarHamPm(horaFin));
        jLabel10.setText(""+cupos+" cupos");
        jLabel12.setText(""+cuposDiscapacidad+" %");
        jLabel14.setText(""+miu+" min");
        if(horasPico[0] == 0){
            jLabel16.setText("");
        }else{
            jLabel16.setText(convertirHmilitarHamPm((int)horasPico[0]) + " - " + convertirHmilitarHamPm((int)horasPico[1]));
        }
        if(horasPico[2] == 0){
            jLabel17.setText("");
        }else{
            jLabel17.setText(convertirHmilitarHamPm((int)horasPico[2]) + " - " + convertirHmilitarHamPm((int)horasPico[3]));
        }
        if(horasPico[4] == 0){
            jLabel18.setText("");
        }else{
            jLabel18.setText(convertirHmilitarHamPm((int)horasPico[4]) + " - " + convertirHmilitarHamPm((int)horasPico[5]));
        }
        jLabel19.setText(""+parqueadero);
    }
    
    // Reinicia todo los parqueaderos del sistema
    private void reiniciarEstadoParqueaderos(){
        estadoIncialParquadero("P1: ",parqueaderos.get(0),jButton1); // P1
        estadoIncialParquadero("P2: ",parqueaderos.get(1),jButton2); // P2
        estadoIncialParquadero("P3: ",parqueaderos.get(2),jButton3); // P3
        estadoIncialParquadero("P4: ",parqueaderos.get(3),jButton4); // P4
        estadoIncialParquadero("P5: ",parqueaderos.get(4),jButton5); // P5
        estadoIncialParquadero("P6: ",parqueaderos.get(5),jButton6); // P6
        estadoIncialParquadero("P7: ",parqueaderos.get(6),jButton7); // P7
        estadoIncialParquadero("P8: ",parqueaderos.get(7),jButton8); // P8
        estadoIncialParquadero("P9: ",parqueaderos.get(8),jButton9); // P9
        estadoIncialParquadero("P10: ",parqueaderos.get(9),jButton10); // P10
        estadoIncialParquadero("P11: ",parqueaderos.get(10),jButton11); // P11
        estadoIncialParquadero("P12: ",parqueaderos.get(11),jButton12); // P12
        estadoIncialParquadero("P13: ",parqueaderos.get(12),jButton13); // P13
        estadoIncialParquadero("P14: ",parqueaderos.get(13),jButton14); // P14
        estadoIncialParquadero("P15: ",parqueaderos.get(14),jButton15); // P15
        estadoIncialParquadero("P16: ",parqueaderos.get(15),jButton16); // P16
        estadoIncialParquadero("P17: ",parqueaderos.get(16),jButton17); // P17
        estadoIncialParquadero("P18: ",parqueaderos.get(17),jButton18); // P18
        estadoIncialParquadero("P19: ",parqueaderos.get(18),jButton19); // P19
        estadoIncialParquadero("P20: ",parqueaderos.get(19),jButton20); // P20
        estadoIncialParquadero("P21: ",parqueaderos.get(20),jButton21); // P21
        estadoIncialParquadero("P22: ",parqueaderos.get(21),jButton22); // P22
        estadoIncialParquadero("P23: ",parqueaderos.get(22),jButton23); // P23
        estadoIncialParquadero("P24: ",parqueaderos.get(23),jButton24); // P24
        estadoIncialParquadero("P25: ",parqueaderos.get(24),jButton25); // P25
        estadoIncialParquadero("P26: ",parqueaderos.get(25),jButton26); // P26
        estadoIncialParquadero("P27: ",parqueaderos.get(26),jButton27); // P27
        estadoIncialParquadero("P28: ",parqueaderos.get(27),jButton28); // P28
        estadoIncialParquadero("P29: ",parqueaderos.get(28),jButton29); // P29
        estadoIncialParquadero("P30: ",parqueaderos.get(29),jButton30); // P30
        estadoIncialParquadero("P31: ",parqueaderos.get(30),jButton31); // P31
        estadoIncialParquadero("P32: ",parqueaderos.get(31),jButton32); // P32
        estadoIncialParquadero("P33: ",parqueaderos.get(32),jButton33); // P33
        estadoIncialParquadero("P34: ",parqueaderos.get(33),jButton34); // P34
        estadoIncialParquadero("P35: ",parqueaderos.get(34),jButton35); // P35
        estadoIncialParquadero("P36: ",parqueaderos.get(35),jButton36); // P36
        estadoIncialParquadero("P37: ",parqueaderos.get(36),jButton37); // P37
        estadoIncialParquadero("P38: ",parqueaderos.get(37),jButton38); // P38
        estadoIncialParquadero("P39: ",parqueaderos.get(38),jButton39); // P39
        estadoIncialParquadero("P40: ",parqueaderos.get(39),jButton40); // P40
        estadoIncialParquadero("P41: ",parqueaderos.get(40),jButton41); // P41
        estadoIncialParquadero("P42: ",parqueaderos.get(41),jButton42); // P42
        estadoIncialParquadero("P43: ",parqueaderos.get(42),jButton43); // P43
        estadoIncialParquadero("P44: ",parqueaderos.get(43),jButton44); // P44
        estadoIncialParquadero("P45: ",parqueaderos.get(44),jButton45); // P45
        estadoIncialParquadero("P46: ",parqueaderos.get(45),jButton46); // P46
        estadoIncialParquadero("P47: ",parqueaderos.get(46),jButton47); // P47
        estadoIncialParquadero("P48: ",parqueaderos.get(47),jButton48); // P48
        estadoIncialParquadero("P49: ",parqueaderos.get(48),jButton49); // P49
        estadoIncialParquadero("P50: ",parqueaderos.get(49),jButton50); // P50
    }
    
    // Reinicia solo un parqueadero en el sistema
    public void estadoIncialParquadero(String textP, Parqueadero p, javax.swing.JButton btn){
        
        p.setT(1);
        p.setLambdaAnterior(0);
        p.setCuposDinamicos((int)p.getCuposTotales());
        btn.setText(textP+p.getCuposDinamicos()+"/"+(int)p.getCuposTotales()); 
        btn.setBackground(new Color(13,166,60)); // Parqueadero libre
    }
    
    // Ver párametros de un parqueadero
    public void verParametrosParqueadero(Parqueadero p, int parqueadero){
         estadoParqueadero((int)p.getHoraInicioJornadaParqueadero(), 
                           (int)p.getHoraFinJornadaParqueadero(), 
                           (int)p.getCuposTotales(), 
                           (int)p.getPorcentajeCuposDiscapacidad(),
                           (int)p.getMiu(),
                            p.getHorasPico(),
                            parqueadero);
    }
    
    private void reiniciarCuposDisponiblesBD(){
        for (int i = 0; i < parqueaderos.size(); i++) {
            Parqueadero p = parqueaderos.get(i);
            gestionBD.actualizarCuposDisponiblesBD(p.getId(),(int)p.getCuposTotales());
	}
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simulación dinámica de parqueaderos de la ciudad");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jButton1.setBackground(new java.awt.Color(51, 153, 0));
        jButton1.setText("P1: 0 / 0");
        jButton1.setOpaque(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(51, 153, 0));
        jButton2.setText("P2: 0 / 0");
        jButton2.setOpaque(true);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(51, 153, 0));
        jButton3.setText("P3: 0 / 0");
        jButton3.setOpaque(true);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(51, 153, 0));
        jButton4.setText("P4: 0 / 0");
        jButton4.setOpaque(true);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(51, 153, 0));
        jButton5.setText("P5: 0 / 0");
        jButton5.setOpaque(true);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(51, 153, 0));
        jButton6.setText("P6: 0 / 0");
        jButton6.setOpaque(true);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(51, 153, 0));
        jButton7.setText("P7: 0 / 0");
        jButton7.setOpaque(true);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(51, 153, 0));
        jButton8.setText("P8: 0 / 0");
        jButton8.setOpaque(true);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(51, 153, 0));
        jButton9.setText("P9: 0 / 0");
        jButton9.setOpaque(true);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(51, 153, 0));
        jButton10.setText("P10: 0 / 0");
        jButton10.setOpaque(true);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(51, 153, 0));
        jButton11.setText("P11: 0 / 0");
        jButton11.setOpaque(true);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(51, 153, 0));
        jButton12.setText("P12: 0 / 0");
        jButton12.setOpaque(true);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setBackground(new java.awt.Color(51, 153, 0));
        jButton13.setText("P13: 0 / 0");
        jButton13.setOpaque(true);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setBackground(new java.awt.Color(51, 153, 0));
        jButton14.setText("P14: 0 / 0");
        jButton14.setOpaque(true);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setBackground(new java.awt.Color(51, 153, 0));
        jButton15.setText("P15: 0 / 0");
        jButton15.setOpaque(true);
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setBackground(new java.awt.Color(51, 153, 0));
        jButton16.setText("P16: 0 / 0");
        jButton16.setOpaque(true);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setBackground(new java.awt.Color(51, 153, 0));
        jButton17.setText("P17: 0 / 0");
        jButton17.setOpaque(true);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setBackground(new java.awt.Color(51, 153, 0));
        jButton18.setText("P18: 0 / 0");
        jButton18.setOpaque(true);
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setBackground(new java.awt.Color(51, 153, 0));
        jButton19.setText("P19: 0 / 0");
        jButton19.setOpaque(true);
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setBackground(new java.awt.Color(51, 153, 0));
        jButton20.setText("P20: 0 / 0");
        jButton20.setOpaque(true);
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setBackground(new java.awt.Color(51, 153, 0));
        jButton21.setText("P21: 0 / 0");
        jButton21.setOpaque(true);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setBackground(new java.awt.Color(51, 153, 0));
        jButton22.setText("P22: 0 / 0");
        jButton22.setOpaque(true);
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setBackground(new java.awt.Color(51, 153, 0));
        jButton23.setText("P23: 0 / 0");
        jButton23.setOpaque(true);
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton24.setBackground(new java.awt.Color(51, 153, 0));
        jButton24.setText("P24: 0 / 0");
        jButton24.setOpaque(true);
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton25.setBackground(new java.awt.Color(0, 153, 0));
        jButton25.setText("P25: 0 / 0");
        jButton25.setOpaque(true);
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton26.setBackground(new java.awt.Color(51, 153, 0));
        jButton26.setText("P26: 0 / 0");
        jButton26.setOpaque(true);
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jButton27.setBackground(new java.awt.Color(51, 153, 0));
        jButton27.setText("P27: 0 / 0");
        jButton27.setOpaque(true);
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton28.setBackground(new java.awt.Color(51, 153, 0));
        jButton28.setText("P28: 0 / 0");
        jButton28.setOpaque(true);
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton29.setBackground(new java.awt.Color(51, 153, 0));
        jButton29.setText("P29: 0 / 0");
        jButton29.setOpaque(true);
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton30.setBackground(new java.awt.Color(51, 153, 0));
        jButton30.setText("P30: 0 / 0");
        jButton30.setOpaque(true);
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jButton31.setBackground(new java.awt.Color(51, 153, 0));
        jButton31.setText("P31: 0 / 0");
        jButton31.setOpaque(true);
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton32.setBackground(new java.awt.Color(51, 153, 0));
        jButton32.setText("P32: 0 / 0");
        jButton32.setOpaque(true);
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton33.setBackground(new java.awt.Color(51, 153, 0));
        jButton33.setText("P33: 0 / 0");
        jButton33.setOpaque(true);
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jButton34.setBackground(new java.awt.Color(51, 153, 0));
        jButton34.setText("P34: 0 / 0");
        jButton34.setOpaque(true);
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton35.setBackground(new java.awt.Color(51, 153, 0));
        jButton35.setText("P35: 0 / 0");
        jButton35.setOpaque(true);
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jButton36.setBackground(new java.awt.Color(51, 153, 0));
        jButton36.setText("P36: 0 / 0");
        jButton36.setOpaque(true);
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setBackground(new java.awt.Color(51, 153, 0));
        jButton37.setText("P37: 0 / 0");
        jButton37.setOpaque(true);
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton38.setBackground(new java.awt.Color(51, 153, 0));
        jButton38.setText("P38: 0 / 0");
        jButton38.setOpaque(true);
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton39.setBackground(new java.awt.Color(51, 153, 0));
        jButton39.setText("P39: 0 / 0");
        jButton39.setOpaque(true);
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        jButton40.setBackground(new java.awt.Color(51, 153, 0));
        jButton40.setText("P40: 0 / 0");
        jButton40.setOpaque(true);
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jButton41.setBackground(new java.awt.Color(51, 153, 0));
        jButton41.setText("P41: 0 / 0");
        jButton41.setOpaque(true);
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jButton42.setBackground(new java.awt.Color(51, 153, 0));
        jButton42.setText("P42: 0 / 0");
        jButton42.setOpaque(true);
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jButton43.setBackground(new java.awt.Color(51, 153, 0));
        jButton43.setText("P43: 0 / 0");
        jButton43.setOpaque(true);
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        jButton44.setBackground(new java.awt.Color(51, 153, 0));
        jButton44.setText("P44: 0 / 0");
        jButton44.setOpaque(true);
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        jButton45.setBackground(new java.awt.Color(51, 153, 0));
        jButton45.setText("P45: 0 / 0");
        jButton45.setOpaque(true);
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        jButton46.setBackground(new java.awt.Color(51, 153, 0));
        jButton46.setText("P46: 0 / 0");
        jButton46.setOpaque(true);
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        jButton47.setBackground(new java.awt.Color(51, 153, 0));
        jButton47.setText("P47: 0 / 0");
        jButton47.setOpaque(true);
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jButton48.setBackground(new java.awt.Color(51, 153, 0));
        jButton48.setText("P48: 0 / 0");
        jButton48.setOpaque(true);
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        jButton49.setBackground(new java.awt.Color(51, 153, 0));
        jButton49.setText("P49: 0 / 0");
        jButton49.setOpaque(true);
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton49ActionPerformed(evt);
            }
        });

        jButton50.setBackground(new java.awt.Color(51, 153, 0));
        jButton50.setText("P50: 0 / 0");
        jButton50.setOpaque(true);
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("12:00 AM");

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos del Parqueadero", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica Neue", 1, 14), new java.awt.Color(0, 51, 255))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel7.setText("Horario:");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("7 am - 10 pm");

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel9.setText("Capacidad: ");

        jLabel10.setText("0");

        jLabel11.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel11.setText("Cupos. Discap:");

        jLabel12.setText("0 %");

        jLabel13.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel13.setText("Prom. Ocu. Esp:");

        jLabel14.setText("000 min");

        jLabel15.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel15.setText("Horas Pico Operación");

        jLabel16.setText("00 am - 00 pm");

        jLabel17.setText("00 am - 00 pm");

        jLabel18.setText("00 am - 00 pm");

        jLabel19.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("1");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estado del Sistema", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica Neue", 1, 14), new java.awt.Color(0, 51, 255))); // NOI18N

        jLabel20.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Vehículos Generados");

        jLabel21.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("10");

        jLabel22.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Vehículos Atendidos");

        jLabel23.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("0");

        jLabel24.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Vehículos en Tránsito");

        jLabel25.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(53, 53, 53)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(88, 88, 88)
                                .addComponent(jButton42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton45))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(420, 420, 420)
                                .addComponent(jButton31)
                                .addGap(18, 18, 18)
                                .addComponent(jButton33))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(389, 389, 389)
                                .addComponent(jButton34)
                                .addGap(37, 37, 37)
                                .addComponent(jButton36)
                                .addGap(18, 18, 18)
                                .addComponent(jButton43))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(410, 410, 410)
                                        .addComponent(jButton41)
                                        .addGap(242, 242, 242))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jButton44)
                                            .addComponent(jButton47))
                                        .addGap(29, 29, 29)
                                        .addComponent(jButton39)
                                        .addGap(18, 18, 18)))
                                .addComponent(jButton40)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(299, 299, 299)
                        .addComponent(jButton27)
                        .addGap(26, 26, 26)
                        .addComponent(jButton19)
                        .addGap(18, 18, 18)
                        .addComponent(jButton20)
                        .addGap(18, 18, 18)
                        .addComponent(jButton21)
                        .addGap(18, 18, 18)
                        .addComponent(jButton24))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(555, 555, 555)
                        .addComponent(jButton12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(239, 239, 239)
                        .addComponent(jButton2)
                        .addGap(130, 130, 130)
                        .addComponent(jButton7)
                        .addGap(43, 43, 43)
                        .addComponent(jButton11))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(196, 196, 196)
                                .addComponent(jButton4)
                                .addGap(18, 18, 18)
                                .addComponent(jButton6))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(183, 183, 183)
                                .addComponent(jButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButton26)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jButton5)
                                .addGap(18, 18, 18)
                                .addComponent(jButton10)
                                .addGap(18, 18, 18)
                                .addComponent(jButton13))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButton9)
                                        .addGap(53, 53, 53)
                                        .addComponent(jButton14)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton16))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButton8)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton18))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(246, 246, 246)
                        .addComponent(jButton32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton38)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton35)
                                .addGap(27, 27, 27)
                                .addComponent(jButton37))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton46)
                                .addGap(114, 114, 114)
                                .addComponent(jButton30)
                                .addGap(18, 18, 18)
                                .addComponent(jButton48)
                                .addGap(27, 27, 27)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton50))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(jButton29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton22)
                        .addGap(18, 18, 18)
                        .addComponent(jButton49)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jButton12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton7)
                    .addComponent(jButton11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jButton5)
                    .addComponent(jButton10)
                    .addComponent(jButton13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton9)
                    .addComponent(jButton14)
                    .addComponent(jButton16)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton15)
                    .addComponent(jButton17)
                    .addComponent(jButton18)
                    .addComponent(jButton26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton19)
                    .addComponent(jButton20)
                    .addComponent(jButton21)
                    .addComponent(jButton24)
                    .addComponent(jButton27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton22)
                    .addComponent(jButton23)
                    .addComponent(jButton25)
                    .addComponent(jButton28)
                    .addComponent(jButton29)
                    .addComponent(jButton49))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jButton48)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton31)
                            .addComponent(jButton33)
                            .addComponent(jButton38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton34, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton36)
                                .addComponent(jButton43)
                                .addComponent(jButton32)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton35)
                                    .addComponent(jButton37)
                                    .addComponent(jButton45))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton39)
                                    .addComponent(jButton40)
                                    .addComponent(jButton44)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jButton42))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton47))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton30)
                            .addComponent(jButton46)
                            .addComponent(jButton50))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("0:00:000");

        jButton51.setText("Iniciar");
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton51ActionPerformed(evt);
            }
        });

        jButton52.setText("Detener");
        jButton52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton52ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("Tiempo (segundo) >> (hora) :");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(2, 2, 12, 2));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel4.setText("Tiempo de simulación :");

        jLabel5.setText("...");

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel6.setText("Estado :");

        jLabel26.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel26.setText("Vehículos en el sistema :");

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(10, 10, null, 10));
        jSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner2StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton52)
                .addGap(24, 24, 24)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton51)
                    .addComponent(jButton52)
                    .addComponent(jLabel3)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel26)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Botón simular
    private void jButton51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton51ActionPerformed
        
        reiniciarCuposDisponiblesBD();
        
        iniciarCronometro();
        jLabel5.setText("Simulando.");
        jButton51.setEnabled(false);
        jSpinner1.setEnabled(false);
        jSpinner2.setEnabled(false);
        jButton52.setEnabled(true);
        // se pone por defecto el parqueadero 1 del sistema
        Parqueadero p;
        p = parqueaderos.get(0); // P1
        estadoParqueadero((int)p.getHoraInicioJornadaParqueadero(), 
                          (int)p.getHoraFinJornadaParqueadero(), 
                          (int)p.getCuposTotales(), 
                          (int)p.getPorcentajeCuposDiscapacidad(),
                          (int)p.getMiu(),
                          p.getHorasPico(),
                          1);
        // Se reinicia el estado de los parqueaderos 
        reiniciarEstadoParqueaderos();
        // Reinicia el estado para generar una nueva simulación de parqueaderos
        vehiculosGeneradosAtendidos = false;
        // Se reinicia el conteo de vehículos en el sistema
        vGenerados = (Integer)jSpinner2.getValue();
        vAtendidos = 0; vTransito = 0;
        jLabel21.setText(""+vGenerados);
        jLabel23.setText(""+vAtendidos);
        jLabel25.setText(""+vTransito);
    }//GEN-LAST:event_jButton51ActionPerformed
    // Botón detener
    private void jButton52ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton52ActionPerformed
        // TODO add your handling code here:
        pararCronometro();
        jLabel5.setText("Detenido.");
        jLabel1.setText( "00:00:000" );
        jButton51.setEnabled(true);
        jSpinner1.setEnabled(true);
        jSpinner2.setEnabled(true);
        jButton52.setEnabled(false);
    }//GEN-LAST:event_jButton52ActionPerformed
    
    // Botón Parqueadero 1
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        verParametrosParqueadero(parqueaderos.get(0), 1); // P1
    }//GEN-LAST:event_jButton1ActionPerformed
    // Botón Parqueadero 2
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        verParametrosParqueadero(parqueaderos.get(1), 2); // P2                                    
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner2StateChanged
        // TODO add your handling code here:
        vGenerados = (Integer)jSpinner2.getValue();
        jLabel21.setText(""+vGenerados);
    }//GEN-LAST:event_jSpinner2StateChanged
    // Botón Parqueadero 3
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        verParametrosParqueadero(parqueaderos.get(2), 3); // P3
    }//GEN-LAST:event_jButton3ActionPerformed
    // Botón Parqueadero 4
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        verParametrosParqueadero(parqueaderos.get(3), 4); // P4
    }//GEN-LAST:event_jButton4ActionPerformed
    // Botón Parqueadero 5
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        verParametrosParqueadero(parqueaderos.get(4), 5); // P5
    }//GEN-LAST:event_jButton5ActionPerformed
     // Botón Parqueadero 6
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        verParametrosParqueadero(parqueaderos.get(5), 6); // P6
    }//GEN-LAST:event_jButton6ActionPerformed
     // Botón Parqueadero 7
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        verParametrosParqueadero(parqueaderos.get(6), 7); // P7
    }//GEN-LAST:event_jButton7ActionPerformed
    // Botón Parqueadero 8
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        verParametrosParqueadero(parqueaderos.get(7), 8); // P8
    }//GEN-LAST:event_jButton8ActionPerformed
    // Botón Parqueadero 9
    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        verParametrosParqueadero(parqueaderos.get(8), 9); // P9
    }//GEN-LAST:event_jButton9ActionPerformed
    // Botón Parqueadero 10
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        verParametrosParqueadero(parqueaderos.get(9), 10); // P10
    }//GEN-LAST:event_jButton10ActionPerformed
    // Botón Parqueadero 11
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
         verParametrosParqueadero(parqueaderos.get(10), 11); // P11
    }//GEN-LAST:event_jButton11ActionPerformed
    // Botón Parqueadero 12
    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        verParametrosParqueadero(parqueaderos.get(11), 12); // P12
    }//GEN-LAST:event_jButton12ActionPerformed
    // Botón Parqueadero 13
    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        verParametrosParqueadero(parqueaderos.get(12), 13); // P13
    }//GEN-LAST:event_jButton13ActionPerformed
    // Botón Parqueadero 14
    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        verParametrosParqueadero(parqueaderos.get(13), 14); // P14
    }//GEN-LAST:event_jButton14ActionPerformed
    // Botón Parqueadero 15
    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        verParametrosParqueadero(parqueaderos.get(14), 15); // P15
    }//GEN-LAST:event_jButton15ActionPerformed
    // Botón Parqueadero 16
    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        verParametrosParqueadero(parqueaderos.get(15), 16); // P16
    }//GEN-LAST:event_jButton16ActionPerformed
    // Botón Parqueadero 17
    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        verParametrosParqueadero(parqueaderos.get(16), 17); // P17
    }//GEN-LAST:event_jButton17ActionPerformed
    // Botón Parqueadero 18
    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        verParametrosParqueadero(parqueaderos.get(17), 18); // P18
    }//GEN-LAST:event_jButton18ActionPerformed
    // Botón Parqueadero 19
    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        verParametrosParqueadero(parqueaderos.get(18), 19); // P19
    }//GEN-LAST:event_jButton19ActionPerformed
    // Botón Parqueadero 20
    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        verParametrosParqueadero(parqueaderos.get(19), 20); // P20
    }//GEN-LAST:event_jButton20ActionPerformed
    // Botón Parqueadero 21
    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        verParametrosParqueadero(parqueaderos.get(20), 21); // P21
    }//GEN-LAST:event_jButton21ActionPerformed
    // Botón Parqueadero 22
    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        verParametrosParqueadero(parqueaderos.get(21), 22); // P22
    }//GEN-LAST:event_jButton22ActionPerformed
    // Botón Parqueadero 23
    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        verParametrosParqueadero(parqueaderos.get(22), 23); // P23
    }//GEN-LAST:event_jButton23ActionPerformed
    // Botón Parqueadero 24
    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        verParametrosParqueadero(parqueaderos.get(23), 24); // P24
    }//GEN-LAST:event_jButton24ActionPerformed
    // Botón Parqueadero 25
    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        verParametrosParqueadero(parqueaderos.get(24), 25); // P25
    }//GEN-LAST:event_jButton25ActionPerformed
    // Botón Parqueadero 26
    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        verParametrosParqueadero(parqueaderos.get(25), 26); // P26
    }//GEN-LAST:event_jButton26ActionPerformed
    // Botón Parqueadero 27
    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        verParametrosParqueadero(parqueaderos.get(26), 27); // P27
    }//GEN-LAST:event_jButton27ActionPerformed
    // Botón Parqueadero 28
    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        verParametrosParqueadero(parqueaderos.get(27), 28); // P28
    }//GEN-LAST:event_jButton28ActionPerformed
    // Botón Parqueadero 28
    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        verParametrosParqueadero(parqueaderos.get(28), 29); // P29
    }//GEN-LAST:event_jButton29ActionPerformed
    // Botón Parqueadero 30
    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        verParametrosParqueadero(parqueaderos.get(29), 30); // P30
    }//GEN-LAST:event_jButton30ActionPerformed
    // Botón Parqueadero 31
    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        verParametrosParqueadero(parqueaderos.get(30), 31); // P31
    }//GEN-LAST:event_jButton31ActionPerformed
    // Botón Parqueadero 32
    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        verParametrosParqueadero(parqueaderos.get(31), 32); // P32
    }//GEN-LAST:event_jButton32ActionPerformed
    // Botón Parqueadero 33
    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        verParametrosParqueadero(parqueaderos.get(32), 33); // P33
    }//GEN-LAST:event_jButton33ActionPerformed
    // Botón Parqueadero 34
    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        verParametrosParqueadero(parqueaderos.get(33), 34); // P34
    }//GEN-LAST:event_jButton34ActionPerformed
    // Botón Parqueadero 35
    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        verParametrosParqueadero(parqueaderos.get(34), 35); // P35
    }//GEN-LAST:event_jButton35ActionPerformed
    // Botón Parqueadero 36
    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        verParametrosParqueadero(parqueaderos.get(35), 36); // P36
    }//GEN-LAST:event_jButton36ActionPerformed
    // Botón Parqueadero 37
    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        verParametrosParqueadero(parqueaderos.get(36), 37); // P37
    }//GEN-LAST:event_jButton37ActionPerformed
    // Botón Parqueadero 38
    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        verParametrosParqueadero(parqueaderos.get(37), 38); // P38
    }//GEN-LAST:event_jButton38ActionPerformed
    // Botón Parqueadero 39
    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        verParametrosParqueadero(parqueaderos.get(38), 39); // P39
    }//GEN-LAST:event_jButton39ActionPerformed
    // Botón Parqueadero 40
    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        verParametrosParqueadero(parqueaderos.get(39), 40); // P40
    }//GEN-LAST:event_jButton40ActionPerformed
    // Botón Parqueadero 41
    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        verParametrosParqueadero(parqueaderos.get(40), 41); // P41
    }//GEN-LAST:event_jButton41ActionPerformed
    // Botón Parqueadero 42
    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        verParametrosParqueadero(parqueaderos.get(41), 42); // P42
    }//GEN-LAST:event_jButton42ActionPerformed
    // Botón Parqueadero 43
    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        verParametrosParqueadero(parqueaderos.get(42), 43); // P43
    }//GEN-LAST:event_jButton43ActionPerformed
    // Botón Parqueadero 44
    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        verParametrosParqueadero(parqueaderos.get(43), 44); // P44
    }//GEN-LAST:event_jButton44ActionPerformed
    // Botón Parqueadero 45
    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        verParametrosParqueadero(parqueaderos.get(44), 45); // P45
    }//GEN-LAST:event_jButton45ActionPerformed
    // Botón Parqueadero 46
    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        verParametrosParqueadero(parqueaderos.get(45), 46); // P46
    }//GEN-LAST:event_jButton46ActionPerformed
    // Botón Parqueadero 47
    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        verParametrosParqueadero(parqueaderos.get(46), 47); // P47
    }//GEN-LAST:event_jButton47ActionPerformed
    // Botón Parqueadero 48
    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        verParametrosParqueadero(parqueaderos.get(47), 48); // P48
    }//GEN-LAST:event_jButton48ActionPerformed
    // Botón Parqueadero 49
    private void jButton49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton49ActionPerformed
        verParametrosParqueadero(parqueaderos.get(48), 49); // P49
    }//GEN-LAST:event_jButton49ActionPerformed
    // Botón Parqueadero 50
    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        verParametrosParqueadero(parqueaderos.get(49), 50); // P50
    }//GEN-LAST:event_jButton50ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimMultiplesParqueaderos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SimMultiplesParqueaderos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimMultiplesParqueaderos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SimMultiplesParqueaderos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SimMultiplesParqueaderos dialog = new SimMultiplesParqueaderos(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    // End of variables declaration//GEN-END:variables
}
