package me.prosl3nderman.clonewarsbase.Internal.APIs;

import me.prosl3nderman.clonewarsbase.Internal.Exceptions.NotAsynchronousException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.track.Track;
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

    private void isLuckPermsMethodBeingRanAsynchronously() {
        if (Bukkit.isPrimaryThread())
            throw new NotAsynchronousException("Error! You cannot use LuckPerms methods in LuckPermsAPI synchronously!");
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
        return "cr";
    }

    public String promotePlayerOnTrack(UUID playerUUID) {
        isLuckPermsMethodBeingRanAsynchronously(); //if it isn't, then it'll throw the exception and cancel this process.
        try {
            User lpUser = lpAPI.getUserManager().isLoaded(playerUUID) ? lpAPI.getUserManager().getUser(playerUUID) :
                    lpAPI.getUserManager().loadUser(playerUUID).get();
            lpAPI.getTrackManager().getTrack("clone_ranks").promote(lpUser, lpAPI.getContextManager().getContextSetFactory().mutable());
            lpAPI.getUserManager().saveUser(lpUser);
            return lpUser.getPrimaryGroup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String demotePlayerOnTrack(UUID playerUUID) {
        isLuckPermsMethodBeingRanAsynchronously(); //if it isn't, then it'll throw the exception and cancel this process.
        try {
            User lpUser = lpAPI.getUserManager().isLoaded(playerUUID) ? lpAPI.getUserManager().getUser(playerUUID) :
                    lpAPI.getUserManager().loadUser(playerUUID).get();
            if (lpUser.getPrimaryGroup().equalsIgnoreCase("PVT"))
                return "PVT";
            lpAPI.getTrackManager().getTrack("clone_ranks").demote(lpUser, lpAPI.getContextManager().getContextSetFactory().mutable());
            lpAPI.getUserManager().saveUser(lpUser);
            return lpUser.getPrimaryGroup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public void setPlayerRank(UUID playerUUID, String rank) {
        isLuckPermsMethodBeingRanAsynchronously(); //if it isn't, then it'll throw the exception and cancel this process.
        try {
            User lpUser = lpAPI.getUserManager().isLoaded(playerUUID) ? lpAPI.getUserManager().getUser(playerUUID) :
                    lpAPI.getUserManager().loadUser(playerUUID).get();
            lpUser.data().remove(Node.builder("group." + lpUser.getPrimaryGroup()).build());
            lpUser.data().add(Node.builder("group." + rank).build());
            lpAPI.getUserManager().saveUser(lpUser);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Boolean isRankLegit(String rank) {
        isLuckPermsMethodBeingRanAsynchronously(); //if it isn't, then it'll throw the exception and cancel this process.
        if (lpAPI.getGroupManager().getGroup(rank) != null)
            return true;
        return false;
    }

    public Boolean firstPersonsRankIsLesserThanSecondPersonsRank(UUID firstPerson, UUID secondPerson) {
        isLuckPermsMethodBeingRanAsynchronously(); //if it isn't, then it'll throw the exception and cancel this process.
        try {
            User lpFirstUser = lpAPI.getUserManager().isLoaded(firstPerson) ? lpAPI.getUserManager().getUser(firstPerson) :
                    lpAPI.getUserManager().loadUser(firstPerson).get();
            User lpSecondUser = lpAPI.getUserManager().isLoaded(secondPerson) ? lpAPI.getUserManager().getUser(secondPerson) :
                    lpAPI.getUserManager().loadUser(secondPerson).get();

            String firstUserRank = lpFirstUser.getPrimaryGroup();
            String secondUserRank = lpSecondUser.getPrimaryGroup();

            if (firstUserRank.equalsIgnoreCase(secondUserRank))
                return false;

            Track track = lpAPI.getTrackManager().getTrack("clone_ranks");
            for (int i = track.getGroups().indexOf(secondUserRank); i >= 0; i--) {
                if (track.getGroups().get(i).equalsIgnoreCase(firstUserRank))
                    return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean givenRankIsGreaterThanGiversRank(String givenRank, UUID giver) {
        isLuckPermsMethodBeingRanAsynchronously(); //if it isn't, then it'll throw the exception and cancel this process.
        try {
            User lpGiver = lpAPI.getUserManager().isLoaded(giver) ? lpAPI.getUserManager().getUser(giver) :
                    lpAPI.getUserManager().loadUser(giver).get();

            String secondUserRank = lpGiver.getPrimaryGroup();

            if (givenRank.equalsIgnoreCase(secondUserRank))
                return false;

            Track track = lpAPI.getTrackManager().getTrack("clone_ranks");
            for (int i = track.getGroups().indexOf(secondUserRank) + 1; i < track.getGroups().size(); i++) {
                if (track.getGroups().get(i).equalsIgnoreCase(givenRank))
                    return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
