package de.intelligence.bachelorarbeit.simplifx.realC;

public final class ControllerGroupContext {

    private final IControllerGroup group;

    public ControllerGroupContext(IControllerGroup group) {
        this.group = group;
    }

    public void switchController(Class<?> clazz) {
        this.group.switchController(clazz);
    }

    public void switchController(Class<?> clazz, IWrapperAnimationFactory factory) {
        this.group.switchController(clazz, factory);
    }

    public ControllerGroupContext getContextFor(String groupId) {
        return this.group.getContextFor(groupId);
    }

    public Class<?> getActiveController() {
        return this.group.getActiveController();
    }

}
