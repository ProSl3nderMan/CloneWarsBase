package me.prosl3nderman.clonewarsbase;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.luckperms.api.LuckPerms;
import org.inventivetalent.customskins.CustomSkins;

public class CloneWarsBaseModule extends AbstractModule {

    private final CloneWarsBase plugin;
    private final LuckPerms lpAPI;
    private final CustomSkins customSkinsAPI;

    public CloneWarsBaseModule(CloneWarsBase plugin, LuckPerms lpAPI, CustomSkins customSkinsAPI) {
        this.plugin = plugin;
        this.lpAPI = lpAPI;
        this.customSkinsAPI = customSkinsAPI;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(CloneWarsBase.class).toInstance(plugin);
        this.bind(LuckPerms.class).toInstance(lpAPI);
        this.bind(CustomSkins.class).toInstance(customSkinsAPI);
    }
}
