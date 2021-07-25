package de.intelligence.bachelorarbeit.demoapplications;

import java.io.IOException;

import de.intelligence.bachelorarbeit.demoapplications.testapp.TestApplication;
import de.intelligence.bachelorarbeit.simplifx.SimpliFX;

public final class Core {

    public static void main(String[] args) throws IOException {
        SimpliFX.launch(TestApplication.class, TestPreloader.class);
    }

}
