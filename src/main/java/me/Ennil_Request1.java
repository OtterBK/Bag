package me;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public final class Ennil_Request1 extends JavaPlugin implements Listener {

    public static Plugin plugin;
    private boolean isFreeTime = false;
    private HashMap<String, Long> killCooldown = new HashMap<String, Long>();
    private HashMap<String, Long> pickUpCooldown = new HashMap<String, Long>();
    private HashMap<String, Location> lastDeathLoc = new HashMap<String, Location>();

    private boolean isIngame = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getLogger().info("엔닐님 요청 플러그인 로드됨");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        plugin = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onCommandInput(PlayerCommandPreprocessEvent e) {
        String[] args = e.getMessage().split(" ");
        String cmd = args[0];

        Player p = e.getPlayer();

        if(cmd.equalsIgnoreCase("/가방") && p.isOp()) {
            if(args.length == 1) {

                p.sendMessage("§a/가방 <탐지/진짜/일반");

            } else {

                int bagType = 0;

                if(args[1].equalsIgnoreCase("탐지")){
                    bagType = 1;
                } else if(args[1].equalsIgnoreCase("진짜")){
                    bagType = 2;
                } else {
                    bagType = 0;
                }

                Bag bag = new Bag(bagType); // 일반 가능
                p.getInventory().addItem(bag.getBagItem());
                p.sendMessage("§a가방 생성 완료");
            }
            e.setCancelled(true);
        } else if(cmd.equalsIgnoreCase("/열쇠") && p.isOp()){
            ItemStack bag = p.getInventory().getItemInMainHand();
            if(args.length == 1) {

                if(bag != null){
                    ItemStack key = Bag.findKey(bag);
                    if(key == null){
                        p.sendMessage("§a일치하는 열쇠가 없습니다.");
                    } else {
                        p.getInventory().addItem(key);
                    }
                } else {
                    p.sendMessage("§a손에 가방을 들고 사용해주세요.");
                }

            } else if(args[1].equalsIgnoreCase("강제")){
                for(Bag tmpBag : Bag.bagList) {
                    Bukkit.getLogger().info(tmpBag.toString());
                    Bukkit.getLogger().info(bag.toString());
                    if (tmpBag.getBagItem().equals(bag)) {
                        tmpBag.open(p);
                        p.sendMessage("강제로 상자를 엽니다.");
                    }
                }
            }
            e.setCancelled(true);
        } else if(cmd.equalsIgnoreCase("/프리") && p.isOp()){
            if(args.length == 1) {

                isFreeTime = !isFreeTime;
                p.sendMessage("§a현재 상태: "+ isFreeTime);

            }
            e.setCancelled(true);
        } else if(cmd.equalsIgnoreCase("/쿨초기") && p.isOp()){
            if(args.length == 1) {

                killCooldown.clear();
                Bukkit.broadcastMessage("§a초기화됨");

            }
            e.setCancelled(true);
        } else if(cmd.equalsIgnoreCase("/시작") && p.isOp()){
            if(args.length == 1) {

                killCooldown.clear();

                for(Player onP : Bukkit.getOnlinePlayers()){
                    onP.sendTitle("§e§l깊콘을 갖고 튀어라!", "§c§l게임이 시작됐습니다.");
                    onP.playSound(onP.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, 1.0f);
                }

                gameTimer();

            }
            e.setCancelled(true);
        } else if(cmd.equalsIgnoreCase("/좌표") && p.isOp()){
            if(args.length == 1) {

                Bag.printAllBagPos(p);

            }
            e.setCancelled(true);
        }
    }

    public void gameTimer(){

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            public void run(){
                
            }
        },0l, 6000l);

    }

//    @EventHandler
//    public void onDeath(PlayerDeathEvent e){
//
//        e.setDeathMessage(null);
//
//        Player deathPlayer = e.getEntity();
//        Entity killer_entity = deathPlayer.getKiller();
//
//        if(killer_entity instanceof Player){
//            Player killer = (Player) killer_entity;
//
//            Long currentTime = System.currentTimeMillis();
//            String killerID = killer.getUniqueId().toString();
//
//            if(!isFreeTime){
//                if(killCooldown.containsKey(killerID)){
//                    long lastkillTime = killCooldown.get(killerID);
//                    if(currentTime - lastkillTime < 300000){
//                        killer.sendMessage("§a아직 킬 쿨타임이 "+(int)((300000 - (currentTime - lastkillTime))/1000) + "초 남았습니다.\n상자 교환이 이루어지지 않습니다.");
//                        deathPlayer.sendMessage("§a아직 상대방이 킬 쿨입니다.\n상자 교환이 이루어지지 않습니다.");
//                        return;
//                    }
//                }
//                killCooldown.put(killerID, currentTime);
//            }
//
//            Inventory victim_inv = deathPlayer.getInventory();
//            Inventory killer_inv = killer.getInventory();
//
//            ArrayList<ItemStack> victim_shulkers = new ArrayList<ItemStack>();
//            for(ItemStack item : victim_inv){
//                if(item != null && item.getType() == Material.TRAPPED_CHEST){
//                    victim_shulkers.add(item);
//                }
//            }
//
//            ArrayList<ItemStack> killer_shulkers = new ArrayList<ItemStack>();
//            for(ItemStack item : killer_inv){
//                if(item != null && item.getType() == Material.TRAPPED_CHEST){
//                    killer_shulkers.add(item);
//                }
//            }
//
//            if(victim_shulkers.size() == 0){
//                killer.sendMessage("§a"+deathPlayer.getName()+" 님은 가방을 갖고 있지 않았습니다.");
//                return;
//            }
//            if(killer_shulkers.size() == 0) {
//                deathPlayer.sendMessage("§a" + killer.getName() + " 님은 가방을 갖고 있지 않았습니다.");
//                return;
//            }
//
//
//            for(ItemStack item : victim_shulkers){
//                victim_inv.remove(item);
//                killer_inv.addItem(item);
//            }
//
//            for(ItemStack item : killer_shulkers){
//                killer_inv.remove(item);
//                victim_inv.addItem(item);
//            }
//
//            killer.getInventory().setContents(killer_inv.getContents());
//            killer.updateInventory();
//            deathPlayer.getInventory().setContents(victim_inv.getContents());
//            deathPlayer.updateInventory();
//
//            killer.sendMessage("§a"+deathPlayer.getName()+" 님을 죽이고 상자를 교환했습니다.");
//            deathPlayer.sendMessage("§a"+killer.getName()+" 님과 상자를 강제로 교환했습니다.");
//        }
//
//
//    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 100));

        if(lastDeathLoc.containsKey(p.getUniqueId().toString())){
            e.setRespawnLocation(lastDeathLoc.get(p.getUniqueId().toString()));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){


        e.setDeathMessage(null);

        Player deathPlayer = e.getEntity();
        Entity killer_entity = deathPlayer.getKiller();


        Inventory victim_inv = deathPlayer.getInventory();
        lastDeathLoc.put(deathPlayer.getUniqueId().toString(), deathPlayer.getLocation());


        ArrayList<ItemStack> victim_bags = new ArrayList<ItemStack>();
        for(ItemStack item : victim_inv){
            if(item != null && item.getType() == Material.TRAPPED_CHEST && item.hasItemMeta()
                    && item.getItemMeta().getDisplayName().contains("가방")){
                deathPlayer.getWorld().dropItem(deathPlayer.getLocation(), item);
                victim_inv.remove(item);
                deathPlayer.getInventory().setContents(victim_inv.getContents());
            }
        }

        if(victim_bags.size() > 0)
            deathPlayer.sendMessage("§c§l가방을 떨어뜨렸습니다.");
        else
            deathPlayer.sendMessage("§c§l소지한 가방이 없습니다.");

        Long currentTime = System.currentTimeMillis();
        String victimID = deathPlayer.getUniqueId().toString();
        pickUpCooldown.put(victimID, currentTime);

        if(killer_entity instanceof Player){
            Player killer = (Player) killer_entity;
            if(victim_bags.size() > 0)
                killer.sendMessage("§c§l"+deathPlayer.getName()+" 님이 가방을 떨어뜨렸습니다.");
            else
                deathPlayer.sendMessage("§c§l"+deathPlayer.getName()+" 님은 소지한 가방이 없습니다.");
        }
    }

    @EventHandler
    public void onFall(EntityDamageEvent e){
        if(e.getCause() == EntityDamageEvent.DamageCause.FALL){ // 낙뎀 삭제
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();

        ItemStack block = p.getInventory().getItemInMainHand();
        if(block != null && block.hasItemMeta()){
            if(block.getItemMeta().hasDisplayName()){
                if(block.getItemMeta().getDisplayName().contains("가방")){
                    p.sendMessage("§a가방은 설치할 수 없습니다.");
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block b = e.getClickedBlock();
            if(b != null && p.getGameMode() != GameMode.CREATIVE){
                if(b.getType() == Material.CHEST
                    || b.getType() == Material.TRAPPED_CHEST
                        || b.getType() == Material.ENDER_CHEST
                        || b.getType() == Material.SHULKER_BOX
                        || b.getType() == Material.BLACK_SHULKER_BOX
                        || b.getType() == Material.BLUE_SHULKER_BOX
                        || b.getType() == Material.BROWN_SHULKER_BOX
                        || b.getType() == Material.CYAN_SHULKER_BOX
                        || b.getType() == Material.GRAY_SHULKER_BOX
                        || b.getType() == Material.GREEN_SHULKER_BOX
                        || b.getType() == Material.LIGHT_BLUE_SHULKER_BOX
                        || b.getType() == Material.LIGHT_GRAY_SHULKER_BOX
                        || b.getType() == Material.LIME_SHULKER_BOX
                        || b.getType() == Material.MAGENTA_SHULKER_BOX
                        || b.getType() == Material.ORANGE_SHULKER_BOX
                        || b.getType() == Material.PINK_SHULKER_BOX
                        || b.getType() == Material.PURPLE_SHULKER_BOX
                        || b.getType() == Material.RED_SHULKER_BOX
                        || b.getType() == Material.WHITE_SHULKER_BOX
                        || b.getType() == Material.YELLOW_SHULKER_BOX
                        || b.getType() == Material.DISPENSER
                        || b.getType() == Material.FURNACE){
                    p.sendTitle("", "§c§l게임 도중에는 상자류 블럭을 열 수 없습니다.");
                    e.setCancelled(true);
                }
            }
        }


    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e){
        Item tmpItem = e.getItem();
        ItemStack item = tmpItem.getItemStack();
        Player p = e.getPlayer();

        Long currentTime = System.currentTimeMillis();
        String victimID = p.getUniqueId().toString();

        if (pickUpCooldown.containsKey(victimID)) {
            long pickupTime = pickUpCooldown.get(victimID);
            if (currentTime - pickupTime < 15000) {
                //p.sendActionBar(new ComponentBuilder("(int)((10000 - (currentTime - pickupTime)) / 1000) + \"초 후 상자를 주울 수 있습니다.\"").color(ChatColor.RED).create());
                p.sendTitle("", "§a§l아직 주울 수 없습니다.", 10, 40, 10);
                e.setCancelled(true);
                return;
            }
        }

        if(item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("가방")){
            for(ItemStack pItem : p.getInventory().getContents()){
                if(Bag.isBag(pItem)){
                    p.sendTitle("", "§a§l이미 가방을 소지 중입니다.", 10, 40, 10);
                    e.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof  Player){
            Player k = (Player)e.getDamager();
            String killerID = k.getUniqueId().toString();
            Long currentTime = System.currentTimeMillis();
            if (pickUpCooldown.containsKey(killerID)) {
                long pickupTime = pickUpCooldown.get(killerID);
                if (currentTime - pickupTime < 15000) {
                    //p.sendActionBar(new ComponentBuilder("(int)((10000 - (currentTime - pickupTime)) / 1000) + \"초 후 상자를 주울 수 있습니다.\"").color(ChatColor.RED).create());
                    k.sendTitle("", "§a§l아직 때릴 수 없습니다.", 10, 40, 10);
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onRemoveDrop(ItemDespawnEvent e){
        Item item = e.getEntity();
        ItemStack isItem = item.getItemStack();
        if(Bag.isBag(isItem)){
            e.setCancelled(true);
        }
    }

}
