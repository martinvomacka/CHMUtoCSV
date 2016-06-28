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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vomec
 */
public class DataLoaderController extends Thread{
    private final FXMLDocumentController parent;
    private final LinkedList<StaniceData> parentBuffer;
    private final ArrayList<String> parentVybrane;

    public DataLoaderController(FXMLDocumentController inParent, LinkedList<StaniceData> inParentBuffer, ArrayList<String> inSeznam) {
        this.parent = inParent;
        this.parentBuffer = inParentBuffer;
        this.parentVybrane = inSeznam;
    }

    @Override
    public void run() {
        ExecutorService es = Executors.newFixedThreadPool(5);
    for(int i=0;i<10;i++)
        es.execute(new DataLoaderThread(parentBuffer, null, null));
    es.shutdown();
    while(!es.isTerminated());
    }
}
