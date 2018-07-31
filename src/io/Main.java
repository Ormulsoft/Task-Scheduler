package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import grph.Grph;

public class Main {

    public static void main(String[] args) {

        String inputPath = "Nodes_7_OutTree.dot";
        String outputPath = "";
        int cores = 1;

        
        
		Grph in = Input.readDotInput(inputPath);
		Grph out = alg.Algorithm.runAlg(in,cores);
		Output.export(out, outputPath);
        


    }
}