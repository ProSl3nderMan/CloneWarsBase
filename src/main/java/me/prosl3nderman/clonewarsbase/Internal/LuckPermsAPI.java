package me.prosl3nderman.clonewarsbase.Internal;

import me.prosl3nderman.clonewarsbase.Internal.Exceptions.NotAsynchronousException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Singleton
public class LuckPermsAPI {

    private LuckPerms lpAPI;

    @Inject
    public LuckPermsAPI(LuckPerms lpAPI) {
        this.lpAPI = lpAPI;
    }

    public void isLuckPermsMethodBeingRanAsynchronously() {
        if (Bukkit.isPrimaryThread())
            throw new NotAsynchronousException("Error! You cannot use LuckPerms methods in LuckPermsAPI synchronously!");
    }

    public String getPlayerRankPrefix(UUID playerUUID) {
        isLuckPermsMethodBeingRanAsynchronously(); //if it isn't, then it'll throw the exception and cancel this process.
        try {
            User lpUser = lpAPI.getUserManager().isLoaded(playerUUID) ? lpAPI.getUserManager().getUser(playerUUID) :
                    lpAPI.getUserManager().loadUser(playerUUID).get();
            return lpUser.getCachedData().getMetaData().getPrefix();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPlayerRank(UUID playerUUID) {
        isLuckPermsMethodBeingRanAsynchronously(); //if it isn't, then it'll throw the exception and cancel this process.
        try {
            User lpUser = lpAPI.getUserManager().isLoaded(playerUUID) ? lpAPI.getUserManager().getUser(playerUUID) :
                    lpAPI.getUserManager().loadUser(playerUUID).get();
            if (lpUser.getPrimaryGroup().equalsIgnoreCase("default"))
                return "cr";
            return lpUser.getPrimaryGroup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }
}
