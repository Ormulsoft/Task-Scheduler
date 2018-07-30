package io;

import grph.Grph;

public class Main {

    public static void main(String[] args){

        String inputPath = "";
        String outputPath = "";
        int cores = 1;

        
        
        Grph in = Input.readDotInput(inputPath);
        Grph out = alg.Algorithm.runAlg(in,cores);
        Output.export(out, outputPath);


    }
}