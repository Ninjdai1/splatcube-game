package dev.ninjdai.splatcube;

import dev.ninjdai.splatcube.map.Timings;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;

import java.net.URI;
import java.time.Duration;

public class EventHandler {
    private static final ResourcePackInfo PACK_INFO = ResourcePackInfo.resourcePackInfo()
            .uri(URI.create("https://download.mc-packs.net/pack/fc4ca9205c186b2b3f75a0b7eae554cad15f060f.zip"))
            .hash("fc4ca9205c186b2b3f75a0b7eae554cad15f060f")
            .build();

    public static void onPlayerConfiguration(AsyncPlayerConfigurationEvent event) {
        final Player player = event.getPlayer();
        event.setSpawningInstance(Main.map);
        player.setRespawnPoint(Main.MAP.mapCenter);

        player.setGameMode(GameMode.ADVENTURE);
        player.setNoGravity(true);
        final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                .packs(PACK_INFO)
                .prompt(Component.text("Please download the resource pack in order to be able to play properly", NamedTextColor.RED))
                .required(true)
                .build();
        player.sendResourcePacks(request);
    };

    public static void onPlayerSpawn(PlayerSpawnEvent event) {
        if (!event.isFirstSpawn()) return;
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        if (event.getInstance().getPlayers().size() < 1/*for debugging purposes, will be 8*/) return;

        Entity cutsceneDisplay = new Entity(EntityType.ITEM_DISPLAY);
        cutsceneDisplay.setAutoViewable(true);
        cutsceneDisplay.setInstance(Main.map, Main.MAP.cutscene.start);
        cutsceneDisplay.setSynchronizationTicks(Long.MAX_VALUE);
        cutsceneDisplay.setNoGravity(true);
        ItemDisplayMeta meta = (ItemDisplayMeta) cutsceneDisplay.getEntityMeta();
        meta.setPosRotInterpolationDuration(180);

        for (Player p: Main.map.getPlayers()) {
            p.spectate(cutsceneDisplay);
        }

        Main.map.scheduler().scheduleTask(() -> {
            Main.map.playSound(Sound.sound(Key.key("splatcube:battle.regular.sound.opening"), Sound.Source.PLAYER, 1.5F, 1));
            cutsceneDisplay.teleport(Main.MAP.cutscene.end);
            Main.map.showTitle(Title.title(
                    Component.text("Turf War"),
                    Component.text("Ink the most turf to win"),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofMillis(500))
            ));
            Main.map.sendActionBar(Component.text(Main.MAP.name));
            return TaskSchedule.stop();
        }, TaskSchedule.seconds(Timings.CUTSCENE_START));

        Main.map.scheduler().scheduleTask(() -> {
            meta.setPosRotInterpolationDuration(0);
            cutsceneDisplay.teleport(Main.MAP.team1Config.spawnViewPos);
            return TaskSchedule.stop();
        }, TaskSchedule.seconds(Timings.CUTSCENE_TEAM_1_SPAWN));

        Main.map.scheduler().scheduleTask(() -> {
            cutsceneDisplay.teleport(Main.MAP.team2Config.spawnViewPos);
            return TaskSchedule.stop();
        }, TaskSchedule.seconds(Timings.CUTSCENE_TEAM_2_SPAWN));

        Main.map.scheduler().scheduleTask(() -> {
            Main.map.showTitle(Title.title(
                    Component.text("Ready..."),
                    Component.empty(),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(100))
            ));
            for (Player p: Main.map.getPlayers()) {
                p.stopSpectating();
                SplatPlayer player = (SplatPlayer) p;
                player.teleport(Main.MAP.team1Config.spawnPos.getFirst());
                player.setNoGravity(false);
                player.setHeldItemSlot((byte) 4);
                player.toggleSquidForm(false);
            }
            return TaskSchedule.stop();
        }, TaskSchedule.seconds(Timings.READY_MESSAGE));

        Main.map.scheduler().scheduleTask(() -> {
            Main.map.sendTitlePart(TitlePart.TITLE, Component.text("Go!"));
            for (Player p: Main.map.getPlayers()) {
                p.setGameMode(GameMode.ADVENTURE);
            }
            return TaskSchedule.stop();
        }, TaskSchedule.seconds(Timings.GAME_START));

        Main.map.scheduler().scheduleTask(() -> {
            Main.map.playSound(Sound.sound(Key.key("splatcube:battle.regular.music"), Sound.Source.PLAYER, 1, 1));
            return TaskSchedule.stop();
        }, TaskSchedule.seconds(Timings.MUSIC_START));
    }

    public static void onSwitchHands(PlayerSwapItemEvent event) {
        event.setCancelled(true);
        SplatPlayer player = (SplatPlayer) event.getPlayer();
        switch (player.getColor()) {
            case BLUE -> player.setColor(Color.ORANGE);
            case ORANGE -> player.setColor(Color.LIME);
            case LIME -> player.setColor(Color.PINK);
            case PINK -> player.setColor(Color.BLUE);
        }
        player.sendActionBar(Component.text("Switched to color " + player.getColor().name(), player.getColor().getColor()));
    }

    public static void onDropItem(ItemDropEvent event) {
        event.setCancelled(true);
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        } else {
            event.getPlayer().setGameMode(GameMode.CREATIVE);
        }
    }

    public static void onSneakStart(PlayerStartSneakingEvent event) {
        SplatPlayer player = (SplatPlayer) event.getPlayer();
        player.toggleSquidForm(true);
        if (player.isOnGround() && event.getInstance().getBlock(player.getPosition().add(0, -1, 0)).compare(Block.IRON_BARS)) {
            player.teleport(player.getPosition().add(0, -0.01, 0));
        }
    }

    public static void onSneakStop(PlayerStopSneakingEvent event) {
        SplatPlayer player = (SplatPlayer) event.getPlayer();
        player.toggleSquidForm(false);
    }

    public static void onInventoryClick(InventoryPreClickEvent event) {
        event.setCancelled(true);
    }

    public static void register(EventNode<Event> node) {
        node.addListener(AsyncPlayerConfigurationEvent.class, EventHandler::onPlayerConfiguration);
        node.addListener(ItemDropEvent.class, EventHandler::onDropItem);
        node.addListener(PlayerSwapItemEvent.class, EventHandler::onSwitchHands);
        node.addListener(PlayerSpawnEvent.class, EventHandler::onPlayerSpawn);
        node.addListener(PlayerStartSneakingEvent.class, EventHandler::onSneakStart);
        node.addListener(PlayerStopSneakingEvent.class, EventHandler::onSneakStop);
        node.addListener(InventoryPreClickEvent.class, EventHandler::onInventoryClick);
    }
}
