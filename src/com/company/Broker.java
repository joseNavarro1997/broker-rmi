package com.company;

/*
 * AUTORES: Rubén Moreno Jimeno 680882 e Iñigo Gascón Royo 685215
 * FICHERO: ServerB.java
 * DESCRIPCIÓN:
 */

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

public class Broker implements BrokerInterface, Runnable {

    private String ipRegistro; //IP del host del registro RMI
    ArrayList<Servidor> servidores = new ArrayList<Servidor>();

    /**
     * Metodo constructor de la clase que asigna la IP de registro
     */
    public Broker(String ipRegistro) {
        this.ipRegistro = ipRegistro;
    }

    /**
     *
     */
    public String ejecutar_servicio(String nom_servicio, String[] parametros_servicio) {
        boolean encontrado = false;
        Iterator<Servidor> iterServidor = servidores.iterator();
        Servidor servidor = null;
        // Se itera en la lista de servidores con sus servicios, hasta encontrar el servicio
        // [nom_servicio] y dejar en la variable [servidor] el servidor que contiene dicho servicio
        while (iterServidor.hasNext() && !encontrado) {
            servidor = iterServidor.next();
            Iterator<Servicio> servicio = servidor.getServicios().iterator();
            while (servicio.hasNext() && !encontrado) {
                if (servicio.next().getNombre().equals(nom_servicio)) {
                    encontrado = true;
                }
            }
        }
        if (encontrado) {
            try {
                // Se coge el objeto remoto del broker
                Registry registry = LocateRegistry.getRegistry(servidor.getIP());
                switch (servidor.getNombre()) {
                    case "ServerAInterface":
                        ServerAInterface server = (ServerAInterface) registry.lookup(servidor
                                .getNombre());
                        break;
                    default:
                        ServerBInterface server = (ServerBInterface) registry.lookup(servidor
                                .getNombre());
                }


            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     *
     */
    public void registrar_servidor(String host_remoto_IP_port, String nombre_registrado) {
        Servidor servidor = new Servidor(host_remoto_IP_port, nombre_registrado);
        servidores.add(servidor);
    }

    /**
     *
     */
    public void registrar_servicio(String nombre_regitrado, String nom_servicio, String[]
            lista_param, String tipo_retorno) {
        for (Servidor servidor : servidores) {
            if (servidor.getNombre().equals(nombre_regitrado)) {
                servidor.addServicio(nom_servicio, lista_param, tipo_retorno);
            }
        }
    }

    /**
     *
     */
    public ArrayList<String> listar_servicios(String noseque) {
        ArrayList<String> listado = new ArrayList<String>();
        Iterator<Servidor> iterServer = servidores.iterator();
        while (iterServer.hasNext()) {
            Iterator<Servicio> iterServicio = iterServer.next().getServicios().iterator();
            while (iterServicio.hasNext()) {
                listado.add(iterServicio.next().toString());
            }
        }
        return listado;
    }

    public void run() {
        try {
            //Se crea un stub y posteriormente se introduce al registro
            ServerAInterface stub = (ServerAInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(ipRegistro);

            registry.bind("BrokerInterface", stub);


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
