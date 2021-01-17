package me.prosl3nderman.clonewarsbase.Internal.APIs;

import me.prosl3nderman.clonewarsbase.Internal.Battalions.Battalion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.inventivetalent.customskins.CustomSkins;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.logging.Level;

@Singleton
public class SkinAPI {

    private CustomSkins customSkinsAPI;

    @Inject
    public SkinAPI(CustomSkins customSkinsAPI) {
        this.customSkinsAPI = customSkinsAPI;
    }

    public void applyCloneSkin(Player player, Battalion battalion, String rank) {
        Bukkit.getLogger().log(Level.INFO, "Battalion Name: " + battalion.getName() + ". Rank: " + rank + ". Player: " + player.getName());
        customSkinsAPI.applySkin(battalion.getName() + "_" + rank, player.getName());
    }

    public void loadSkin(String battalionName, String rank, File skinFile, Boolean reloadAllSkins) {
        Bukkit.getLogger().log(Level.INFO, "Battalion Name: " + battalionName + ". Rank: " + rank + ". Skin File Path: " + skinFile.getAbsolutePath());
        customSkinsAPI.createSkin(battalionName + "_" + rank, skinFile, "private", "steve", reloadAllSkins);
    }
}
