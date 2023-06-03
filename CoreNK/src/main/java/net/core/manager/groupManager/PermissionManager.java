package net.core.manager.groupManager;

import cn.nukkit.Player;
import cn.nukkit.permission.Permission;
import net.core.Core;

import java.util.ArrayList;
import java.util.Objects;

public class PermissionManager {

    public static String getGroupPermsRaw(final String group) {
        return String.valueOf(Groups.get("permissions", "ranks", "rank_name", group));
    }

    public static String getGroupParentsRaw(final String group) {
        return String.valueOf(Groups.get("parents", "ranks", "rank_name", group));
    }

    public static ArrayList<String> getGroupPerms(final String group) {
        final String groupPerms_raw = getGroupPermsRaw(group.replace(" ", ""));
        final String groupParents_raw = getGroupParentsRaw(group.replace(" ", ""));
        final ArrayList<String> perms = new ArrayList<>();
        final ArrayList<String> parents = new ArrayList<>();
        if (groupPerms_raw.isEmpty()) {
            return perms;
        }

        final String[] permsList = groupPerms_raw.split(";");
        for (String perm : permsList){
            if (!perm.isEmpty() && !perm.equals("null") && !perms.contains(perm)) {
                perms.add(perm);
            }
        }

        final String[] parentList = groupParents_raw.split(";");
        for (String parent : parentList){
            if (!parent.isEmpty() && !parent.equals("null")) {
                final String groupPerms_raw2 = getGroupPermsRaw(parent.replace(" ", ""));
                final String[] permsList2 = groupPerms_raw2.split(";");
                for (String perm : permsList2){
                    if (!perm.isEmpty() && !perm.equals("null") && !perms.contains(perm)) {
                        perms.add(perm);
                    }
                }
            }
        }

        return perms;
    }

    public static void calculatePlayerPermissions(Player player, boolean clearperms){
        final ArrayList<String> perms = getGroupPerms(Objects.requireNonNull(Groups.getPlayerGroup(player.getName())));
        if (clearperms) player.addAttachment(Core.getInstance()).clearPermissions();

        if (perms.contains("*")){
            for (Permission permission : Core.getInstance().getServer().getPluginManager().getPermissions().values()) {
                if (permission != null) {
                    player.addAttachment(Core.getInstance()).setPermission(permission, true);
                }
            }
            for (String permission : perms) {
                if (permission != null) {
                    player.addAttachment(Core.getInstance()).setPermission(permission, true);
                }
            }
        } else {
            for (String permission : perms){
                if (permission != null) {
                    player.addAttachment(Core.getInstance()).setPermission(permission, true);
                }
            }
        }
    }
}
