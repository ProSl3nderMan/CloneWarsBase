package me.prosl3nderman.clonewarsbase.Internal;

//All objects that act as handlers of things implement this,
// so that they are created and destroyed by the main class.
//Credit to MiniDigger for this code design:
//https://github.com/VoxelGamesLib/VoxelGamesLibv2/blob/21bb4237fbf999da05e8dfa4fb6ad346fa9267fd/VoxelGamesLib/src/main/java/com/voxelgameslib/voxelgameslib/internal/handler/Handler.java#L7

public interface Handler {

    //called on plugin enable (normally when the server is enabled, but can happen also with plugin manager).
    //used to set up things.
    void enable();

    //called on plugin disable (normally when the server is disabled, but can happen also with plugin manager).
    //used to clean up things.
    void disable();

}
