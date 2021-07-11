package de.intelligence.bachelorarbeit.demoapplications;

import java.io.IOException;

import de.intelligence.bachelorarbeit.simplifx.ClasspathScanPolicy;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;

public final class Core {

    public static void main(String[] args) throws IOException {
        SimpliFX.setClasspathScanPolicy(ClasspathScanPolicy.LOCAL);
        SimpliFX.launchWithPreloader();
    }

}
