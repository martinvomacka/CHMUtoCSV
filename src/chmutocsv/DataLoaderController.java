/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chmutocsv;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Vomec
 */
public class DataLoaderController extends Thread{
    private final FXMLDocumentController parent;
    public final LinkedList<Stanice> seznamStanic;

    public DataLoaderController(FXMLDocumentController inParent, LinkedList<Stanice> inSeznamStanic) {
        this.parent = inParent;
        this.seznamStanic = inSeznamStanic;
    }

    @Override
    public void run() {
        System.out.println("Starting job...");
        ExecutorService es = Executors.newFixedThreadPool(5);
        for (Stanice seznamStanic1 : seznamStanic) {
            es.execute(new DataLoaderThread(seznamStanic1));
        }
        es.shutdown();
        while(!es.isTerminated());
        System.out.println("Job DONE!");
    }
}
