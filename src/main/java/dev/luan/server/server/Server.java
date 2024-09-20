package dev.luan.server.server;

import net.minestom.server.MinecraftServer;

import java.util.function.Consumer;

public final class Server {

    private final Consumer<String> log;

    public Server(Consumer<String> log) {
        this.log = log;
    }

    public MinecraftServer run() {
        this.log.accept("Running Server with Minestom on Java " + System.getProperty("java.version") + " with " + System.getProperty("os.name") + ".");

        var startup = System.currentTimeMillis();

        this.log.accept("Loading enviroment.");
        var minecraftServer = MinecraftServer.init();

        return minecraftServer;
    }
}
