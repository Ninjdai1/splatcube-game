package dev.ninjdai.splatcube.map;

public class Timings {
    // In seconds:
    public static final int LAST_PLAYER_SPAWN = 0;
    public static final int CUTSCENE_START = LAST_PLAYER_SPAWN + 2;
    public static final int CUTSCENE_TEAM_1_SPAWN = CUTSCENE_START + 11;
    public static final int CUTSCENE_TEAM_2_SPAWN = CUTSCENE_TEAM_1_SPAWN + 2;
    public static final int READY_MESSAGE = CUTSCENE_TEAM_2_SPAWN + 2;
    public static final int GAME_START = READY_MESSAGE + 2;
    public static final int MUSIC_START = GAME_START + 4;
}
