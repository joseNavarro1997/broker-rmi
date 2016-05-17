package com.company;

/*
 * AUTORES: Rubén Moreno Jimeno 680882 e Iñigo Gascón Royo 685215
 * FICHERO: ServerA.java
 * DESCRIPCIÓN:
 */
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerA implements ServerAInterface, Runnable {

    private String ipRegistro; //IP del host del registro RMI
    private final String ipBroker = "localhost"; //IP del broker, se sabe antes de compilarse

    /**
     *	Metodo constructor de la clase que asigna la IP de registro
     */
    public ServerA(String ipRegistro) {
        this.ipRegistro = ipRegistro;
    }

    /**
     *
     */
    public String dar_hora() {
        return "";
    }

    /**
     *
     */
    public String dar_fecha() {
        return "";
    }

    public void run() {
        try {
            //Se crea un stub y posteriormente se introduce al registro
            ServerAInterface stub = (ServerAInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(ipRegistro);
            String nombre_registro = "ServerAInterface";
            registry.bind(nombre_registro, stub);

            // Se coge el objeto remoto del broker
            registry = LocateRegistry.getRegistry(ipBroker);
            BrokerInterface brokerInterface = (BrokerInterface) registry.lookup("BrokerInterface");
            // Se registra el servidor dentro del broker
            brokerInterface.registrar_servidor(ipRegistro,nombre_registro);
            // String nombre_regitrado, String nom_servicio, String [] lista_param, String tipo_retorno
            brokerInterface.registrar_servicio(nombre_registro, "dar_hora",new String[0],"String");
            brokerInterface.registrar_servicio(nombre_registro, "dar_fecha",new String[0],"String");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
