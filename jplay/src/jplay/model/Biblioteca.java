/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jplay.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author pperezp
 */
public class Biblioteca implements Serializable{
    private final List<Cancion> CANCIONES;
    
    public Biblioteca(){
        CANCIONES = new ArrayList<>();
    }

    public Biblioteca(List<Cancion> canciones) {
        this.CANCIONES = canciones;
    }
    
    public List<Cancion> getCanciones() {
        return CANCIONES;
    }
    
    public void remover(Cancion c){
        CANCIONES.remove(c);
    }
    
    public boolean estaCancion(Cancion c){
        return CANCIONES.contains(c);
    }
    
    public List<Cancion> getCancionesMasReproducidas(){
        List<Cancion> topCanciones = new ArrayList<>();
        
        for(Cancion c : CANCIONES){
            if(c.getCantidadReproducciones() != 0){
                topCanciones.add(c);
            }
        }
        
        /*acá tengo que ordenarlas (de la mas reproducida a la menos)*/
        
        /*------ Proceso de ordenado de lista ------*/
        Collections.sort(topCanciones, new Comparator<Cancion>() {
            @Override
            public int compare(Cancion o1, Cancion o2) {
                if(o1.getCantidadReproducciones() > o2.getCantidadReproducciones()){
                    return -1;
                }else if(o1.getCantidadReproducciones() < o2.getCantidadReproducciones()){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
        /*------ Proceso de ordenado de lista ------*/
        
        return topCanciones;
    }
//    
//    public Cancion getCancion(Cancion c){
//        for(Cancion cancion : CANCIONES){
//            if(cancion.getNombre().equalsIgnoreCase(c.getNombre()) &&
//                    cancion.getAutor().equalsIgnoreCase(c.getAutor()) &&
//                    cancion.getAlbum().equalsIgnoreCase(c.getAlbum())){
//                return cancion;
//            }
//        }
//        
//        return null;
//    }
}
