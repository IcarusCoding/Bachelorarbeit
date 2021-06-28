package de.intelligence.bachelorarbeit.demoapplications;

import de.intelligence.bachelorarbeit.simplifx.ClasspathScanPolicy;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;
import de.intelligence.bachelorarbeit.simplifx.annotation.ApplicationEntryPoint;

@ApplicationEntryPoint(Core.class)
public final class Core {

    public static void main(String[] args) {
        SimpliFX.setClasspathScanPolicy(ClasspathScanPolicy.LOCAL);
        SimpliFX.launch(Core.class);
    }

}
