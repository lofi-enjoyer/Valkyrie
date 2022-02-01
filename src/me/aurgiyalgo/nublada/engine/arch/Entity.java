package me.aurgiyalgo.nublada.engine.arch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Entity {

    private List<Component> components;
    private List<Component> queuedComponents;
    private Map<Class<? extends Component>, Component> componentsCache;
    public boolean isUpdating;

    public Entity() {
        this.components = new ArrayList<>();
        this.queuedComponents = new ArrayList<>();
    }

    public void update(float delta) {
        isUpdating = true;
        components.forEach(component -> component.update(delta));
        isUpdating = false;

        components.addAll(queuedComponents);
        queuedComponents.clear();
    }

    public void addComponent(Component component) {
        if (isUpdating) {
            queuedComponents.add(component);
            return;
        }

        components.add(component);
    }

    public <T extends Component> Component getComponent(Class<T> componentType) {
        Component component = componentsCache.get(componentType);
        if (component != null) return component;

        for (int i = 0; i < components.size(); i++) {
            if (!componentType.isInstance(components.get(i))) continue;
            component = components.get(i);
            return component;
        }
        return null;
    }

}
