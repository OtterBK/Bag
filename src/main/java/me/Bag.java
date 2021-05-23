package me;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Bag {

    public static ArrayList<Bag> bagList = new ArrayList<Bag>();

    private ItemStack bagItem;
    private ItemStack keyItem;
    private Inventory inventory;
    private int bagID;
    private int bagType = 0; //0: 일반 1: 탐지 2: 진짜

    public static ItemStack findKey(ItemStack bagItem){
        for(Bag tmpBag : bagList){
            if(tmpBag.getBagItem().equals(bagItem)){
                return tmpBag.getKeyItem();
            }
        }
        return null;
    }

    public static boolean isBag(ItemStack item){
        for(Bag bag : bagList){
            if(bag.getBagItem().equals(item)){
                return true;
            }
        }
        return false;
    }

    public static double getRealBagDistance(Player p){

        for(Player onP : Bukkit.getOnlinePlayers()){
            for(ItemStack item : onP.getInventory()){
                if(item != null && item.hasItemMeta()){
                    for(Bag bag : bagList){
                        if(bag.getBagItem().equals(item)){
                            if(bag.getBagType() == 2){
                                Player realBag_player = onP;
                                return p.getLocation().distance(realBag_player.getLocation());
                            }
                        }
                    }
                }
            }
        }

        for(Entity entity : p.getWorld().getEntities()){
            if(entity instanceof Item){
                Item item = (Item)entity;
                ItemStack is = item.getItemStack();
                for(Bag bag : bagList){
                    if(bag.getBagItem().equals(is)){
                        if(bag.getBagType() == 2){
                            return p.getLocation().distance(item.getLocation());
                        }
                    }
                }
            }
        }
        return -1;
    }

    public static void printAllBagPos(Player p){
        Bukkit.broadcastMessage("§e------ 모든 가방 위치 -----");
        for(Entity entity : p.getWorld().getEntities()){
            if(entity instanceof Item){
                Item item = (Item)entity;
                ItemStack is = item.getItemStack();
                for(Bag bag : bagList){
                    if(bag.getBagItem().equals(is)){
                        Bukkit.broadcastMessage("§6"+item.getLocation().getBlockX() + ", " + item.getLocation().getBlockY() + ", " + item.getLocation().getBlockZ() + ", ");
                    }
                }
            }
        }

        for(Player onP : Bukkit.getOnlinePlayers()){
            for(ItemStack item : onP.getInventory()){
                if(item != null && item.hasItemMeta()){
                    for(Bag bag : bagList){
                        if(bag.getBagItem().equals(item)){
                            Bukkit.broadcastMessage("§6"+onP.getLocation().getBlockX() + ", " + onP.getLocation().getBlockY() + ", " + onP.getLocation().getBlockZ() + ", ");
                        }
                    }
                }
            }
        }
        Bukkit.broadcastMessage("§e----------------------");
    }

    public Bag(int bagType){
        boolean isID_exist = false;
        do{
            bagID = getRandom(10,1000000);
            for(Bag tmpBag : bagList){
                if(tmpBag.bagID == bagID){
                    isID_exist = true;
                    break;
                }
            }
        }while(isID_exist);

        bagList.add(this);



        this.bagType = bagType;

        bagItem = new ItemStack(Material.TRAPPED_CHEST, 1);
        ItemMeta bagMeta = bagItem.getItemMeta();
        bagMeta.setDisplayName("§b가방");
        bagMeta.setLocalizedName(String.valueOf(bagID));
        bagItem.setItemMeta(bagMeta);



        keyItem = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        ItemMeta keyMeta = keyItem.getItemMeta();
        keyMeta.setDisplayName("§6열쇠");
        keyMeta.setLocalizedName(String.valueOf(bagID));
        keyItem.setItemMeta(keyMeta);

        inventory = Bukkit.createInventory(null, 54, "§0§l가방");

        BagEvent bagEvent = new BagEvent();
        Ennil_Request1.plugin.getServer().getPluginManager().registerEvents(bagEvent, Ennil_Request1.plugin);
    }

    public int getBagType(){
        return  this.bagType;
    }

    public int getRandom(int min, int max) {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    public void open(Player p){
        p.openInventory(this.inventory);
        if(bagType==1){
            int distance = (int)getRealBagDistance(p);
            p.sendMessage("§a이 가방은 탐지 가방입니다.");
            if(distance < 0){
                p.sendMessage("§a다만 아직 진짜 가방이 존재하지 않습니다.");
            } else {
                p.sendMessage("§a진짜 가방과의 거리는: "+distance+"m입니다.");
            }

        }
    }

    public ItemStack getBagItem(){
        return this.bagItem;
    }

    public ItemStack getKeyItem(){
        return this.keyItem;
    }

    public class BagEvent implements Listener{
        @EventHandler
        public void onPlayerRightClick(PlayerInteractEvent e){
            if(e.getItem() != null && e.getItem().equals(getBagItem())){
                Player p = e.getPlayer();

                ItemStack offHand = p.getInventory().getItemInOffHand();
                if(offHand != null && offHand.equals(getKeyItem())){
                    open(e.getPlayer());
                } else {
                    p.sendMessage("§a가방에 맞는 열쇠가 필요합니다.");
                }

            }
        }
    }

}
