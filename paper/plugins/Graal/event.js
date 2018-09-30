plugin = Polyglot.import('plugin');
plugin.registerEvent('org.bukkit.event.player.AsyncPlayerChatEvent', test);

function test(listener, event) {
    event.setFormat('%1$s> ' + Java.type('org.bukkit.ChatColor').RED + '%2$s');
}

"Registered event"