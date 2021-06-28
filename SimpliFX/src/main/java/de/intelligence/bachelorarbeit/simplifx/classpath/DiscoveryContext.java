package de.intelligence.bachelorarbeit.simplifx.classpath;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class DiscoveryContext {

    private final String path;
    private final List<ClassLoader> classLoaders;

}
