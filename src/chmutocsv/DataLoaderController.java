/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chmutocsv;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;

/**
 *
 * @author Vomec
 */
public class DataLoaderController extends Task{
    private final HlavniOknoController parent;
    public final LinkedList<Stanice> seznamStanic;

    public DataLoaderController(HlavniOknoController inParent, LinkedList<Stanice> inSeznamStanic) {
        this.parent = inParent;
        this.seznamStanic = inSeznamStanic;
    }

    @Override
    protected Object call() throws Exception {
        int threadsAvailaible = Runtime.getRuntime().availableProcessors();
        updateMessage("Načítám data, prosím čekejte... Počet dostupných procesorů: "+threadsAvailaible);
        ExecutorService es = Executors.newFixedThreadPool(threadsAvailaible);
        for (Stanice seznamStanic1 : seznamStanic) {
            es.execute(new DataLoaderThread(seznamStanic1));
        }
        es.shutdown();
        while(!es.isTerminated());
        return null;
    }
}
