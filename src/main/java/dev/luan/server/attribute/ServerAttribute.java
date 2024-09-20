package dev.luan.server.attribute;

public final class ServerAttribute<T> {

    private final T defaultValue;

    public ServerAttribute(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T defaultValue() {
        return defaultValue;
    }
}
