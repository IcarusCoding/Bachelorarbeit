package de.intelligence.bachelorarbeit.simplifx.realC;

public enum VisibilityState {

    UNDEFINED,
    SHOWN,
    HIDDEN,
    GROUP_SHOWN,
    GROUP_HIDDEN;

    public VisibilityState type() {
        if (this.equals(UNDEFINED)) {
            return UNDEFINED;
        }
        if (this.equals(SHOWN) || this.equals(GROUP_SHOWN)) {
            return SHOWN;
        }
        return HIDDEN;
    }

}
