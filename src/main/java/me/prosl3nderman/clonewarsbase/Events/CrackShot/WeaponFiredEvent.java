package me.prosl3nderman.clonewarsbase.Events.CrackShot;

import com.shampaggon.crackshot.events.WeaponShootEvent;
import me.prosl3nderman.clonewarsbase.Commands.CWBCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WeaponFiredEvent implements Listener {

    private CWBCommand cwbCommand;

    @Inject
    public WeaponFiredEvent(CWBCommand cwbCommand) {
        this.cwbCommand = cwbCommand;
    }

    @EventHandler
    public void sendRecoilOnWeaponFire(WeaponShootEvent event) {
        Player player = event.getPlayer();
        Vector velToAdd = player.getLocation().getDirection().multiply(-cwbCommand.testRecoil);
        velToAdd.subtract(new Vector(0, cwbCommand.testFallRecoil, 0));
        player.setVelocity(velToAdd);
    }
}
