package dev.ninjdai.splatcube;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import dev.ninjdai.splatcube.blockhandlers.BannerHandler;
import dev.ninjdai.splatcube.blockhandlers.SignHandler;
import dev.ninjdai.splatcube.blockhandlers.SkullHandler;
import dev.ninjdai.splatcube.gui.Gui;
import dev.ninjdai.splatcube.items.ClickableItems;
import dev.ninjdai.splatcube.map.MapConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.lan.OpenToLAN;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger("Playground");

    public static MapConfig MAP;
    public static InstanceContainer map;

    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        MinecraftServer.getBlockManager().registerHandler(SkullHandler.KEY, SkullHandler::new);
        MinecraftServer.getBlockManager().registerHandler(SignHandler.KEY, SignHandler::new);
        MinecraftServer.getBlockManager().registerHandler(BannerHandler.KEY, BannerHandler::new);

        DimensionType fullBright = DimensionType.builder().ambientLight(1f).build();
        DynamicRegistry.Key<DimensionType> fullBrightDimension = MinecraftServer.getDimensionTypeRegistry().register("splatcube:fullbright", fullBright);
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        final TomlMapper mapper = new TomlMapper();
        try {
            if (System.getenv("MAP") instanceof String mapName) {
                MAP = mapper.readValue(new File(String.format("worlds/%s.toml", mapName)), MapConfig.class);
            } else {
                MAP = mapper.readValue(new File(
                    String.format("worlds/%s.toml", List.of("barnacle_and_dime", "crableg_capital", "robo_ROM-en", "um-ami_ruins").get(new Random().nextInt(4)))
                ), MapConfig.class);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        if (MAP==null) throw new RuntimeException("No suitable map found");

        // Velocity Setup
        if(System.getenv("PAPER_VELOCITY_SECRET") instanceof String vsecret) {
            VelocityProxy.enable(vsecret);
            System.out.println("v-secret: " + vsecret);
        }

        map = instanceManager.createInstanceContainer(fullBrightDimension);
        map.setChunkLoader(new AnvilLoader("worlds/"+MAP.id));

        map.setChunkSupplier(LightingChunk::new);
        map.setChunkSupplier(LightingChunk::new);
        map.loadChunk(0, 0);
        map.loadChunk(MAP.mapCenter.chunkX(), MAP.mapCenter.chunkZ());
        map.loadChunk(MAP.team1Config.spawnViewPos.chunkX(), MAP.team1Config.spawnViewPos.chunkZ());
        map.loadChunk(MAP.team2Config.spawnViewPos.chunkX(), MAP.team2Config.spawnViewPos.chunkZ());

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        EventHandler.register(globalEventHandler);
        globalEventHandler.addChild(Gui.node);
        globalEventHandler.addChild(ClickableItems.right_node);
        globalEventHandler.addChild(ClickableItems.left_node);

        MinecraftServer.getConnectionManager().setPlayerProvider(SplatPlayer::new);

        MinecraftServer.setBrandName("Splatcube");

        minecraftServer.start("0.0.0.0", 25565);
    }
}
