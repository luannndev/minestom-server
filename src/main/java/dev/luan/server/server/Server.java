package dev.luan.server.server;

import dev.luan.server.blockhandler.HangingSignBlockHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.utils.NamespaceID;

import java.util.concurrent.TimeUnit;
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

        var velocitySecret = System.getProperty("VELOCITY_SECRET");
        if (velocitySecret != null) {
            this.log.accept("Enviroment: VelocityProxy[Minestom]");
            this.log.accept("- " + VelocityProxy.PLAYER_INFO_CHANNEL);
            VelocityProxy.enable(velocitySecret);
        } else {
            MojangAuth.init();
            this.log.accept("Enviroment: MojangAuth[Minestom]");
            this.log.accept("- " + MojangAuth.AUTH_URL);
        }
        this.log.accept("");

        minecraftServer.start("0.0.0.0", 25565);

        MinecraftServer.getBlockManager().registerHandler(NamespaceID.from("minecraft:hanging_sign"), HangingSignBlockHandler::new);

        this.log.accept("Server started successfully. Took " + (System.currentTimeMillis() - startup) + "ms (" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startup) + "s)");
        return minecraftServer;
    }
}
