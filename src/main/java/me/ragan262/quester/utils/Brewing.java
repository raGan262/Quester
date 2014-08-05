package me.ragan262.quester.utils;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;

public class Brewing {
	
	public static final int WATER = 0;
	public static final int AWKWARD = 16;
	public static final int THICK = 32;
	public static final int MUNDANE_E = 64;
	public static final int MUNDANE = 8192;
	
	public static final int REGENERATION = 8193;
	public static final int REGENERATION_E = 8257;
	public static final int REGENERATION_II = 8225;
	
	public static final int SWIFTNESS = 8194;
	public static final int SWIFTNESS_E = 8258;
	public static final int SWIFTNESS_II = 8226;
	
	public static final int FIRERESIST = 8195;
	public static final int FIRERESIST_E = 8259;
	public static final int FIRERESIST_R = 8227;
	
	public static final int HEALING = 8197;
	public static final int HEALING_R = 8261;
	public static final int HEALING_II = 8229;
	
	public static final int NIGHTVISION = 8198;
	public static final int NIGHTVISION_E = 8262;
	
	public static final int STRENGTH = 8201;
	public static final int STRENGTH_E = 8265;
	public static final int STRENGTH_II = 8233;
	
	public static final int INVISIBILITY = 8206;
	public static final int INVISIBILITY_E = 8270;
	
	public static final int POISON = 8196;
	public static final int POISON_E = 8260;
	public static final int POISON_II = 8228;
	
	public static final int WEAKNESS = 8200;
	public static final int WEAKNESS_E = 8264;
	public static final int WEAKNESS_R = 8232;
	
	public static final int SLOWNESS = 8202;
	public static final int SLOWNESS_E = 8266;
	public static final int SLOWNESS_R = 8234;
	
	public static final int HARMING = 8204;
	public static final int HARMING_R = 8268;
	public static final int HARMING_II = 8236;
	
	// bits 14 and 15, swapping these will create splash potion
	public static final int SPLASH = 8192 + 16384;
	
	// format: Map<INGREDIENT ID, Map<ORIGINAL DATA, PRODUCT DATA>>
	private static Map<Integer, Map<Integer, Integer>> recipes;
	
	private Brewing() {
		throw new IllegalAccessError();
	}
	
	// gets DATA of the result, 0 if nothing can be brewed
	public static int getResult(final int ingredient, final int originalData) {
		if(recipes.containsKey(ingredient) && recipes.get(ingredient).containsKey(originalData)) {
			return recipes.get(ingredient).get(originalData);
		}
		return 0;
	}
	
	static {
		recipes = new HashMap<Integer, Map<Integer, Integer>>();
		Map<Integer, Integer> tempMap;
		
		// NETHER WART
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, AWKWARD);
		recipes.put(Material.NETHER_WARTS.getId(), tempMap);
		
		// SUGAR
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, MUNDANE);
		tempMap.put(AWKWARD, SWIFTNESS);
		recipes.put(Material.SUGAR.getId(), tempMap);
		
		// GOLDEN CARROT
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(AWKWARD, NIGHTVISION);
		recipes.put(Material.NETHER_WARTS.getId(), tempMap);
		
		// MAGMA CREAM
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, MUNDANE);
		tempMap.put(AWKWARD, FIRERESIST);
		recipes.put(Material.MAGMA_CREAM.getId(), tempMap);
		
		// GLISTERING MELON
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, MUNDANE);
		tempMap.put(AWKWARD, FIRERESIST);
		recipes.put(Material.SPECKLED_MELON.getId(), tempMap);
		
		// SPIDER EYE
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, MUNDANE);
		tempMap.put(AWKWARD, POISON);
		recipes.put(Material.SPIDER_EYE.getId(), tempMap);
		
		// GHAST TEAR
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, MUNDANE);
		tempMap.put(AWKWARD, REGENERATION);
		recipes.put(Material.GHAST_TEAR.getId(), tempMap);
		
		// BLAZE POWDER
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, MUNDANE);
		tempMap.put(AWKWARD, STRENGTH);
		recipes.put(Material.BLAZE_POWDER.getId(), tempMap);
		
		// REDSTONE DUST
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, MUNDANE_E);
		tempMap.put(NIGHTVISION, NIGHTVISION_E);
		tempMap.put(INVISIBILITY, INVISIBILITY_E);
		tempMap.put(FIRERESIST, FIRERESIST_E);
		tempMap.put(SLOWNESS, SLOWNESS_E);
		tempMap.put(SWIFTNESS, SWIFTNESS_E);
		tempMap.put(SWIFTNESS_II, SWIFTNESS_E);
		tempMap.put(HEALING_II, HEALING_R);
		tempMap.put(HARMING_II, HARMING_R);
		tempMap.put(POISON, POISON_E);
		tempMap.put(POISON_II, POISON_E);
		tempMap.put(REGENERATION, REGENERATION_E);
		tempMap.put(REGENERATION_II, REGENERATION_E);
		tempMap.put(WEAKNESS, WEAKNESS_E);
		tempMap.put(STRENGTH, STRENGTH_E);
		tempMap.put(STRENGTH_II, STRENGTH_E);
		recipes.put(Material.REDSTONE.getId(), tempMap);
		
		// GLOWSTONE DUST
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, THICK);
		tempMap.put(NIGHTVISION_E, NIGHTVISION);
		tempMap.put(INVISIBILITY_E, INVISIBILITY);
		tempMap.put(FIRERESIST_E, FIRERESIST_R);
		tempMap.put(SLOWNESS_E, SLOWNESS_R);
		tempMap.put(SWIFTNESS, SWIFTNESS_II);
		tempMap.put(SWIFTNESS_E, SWIFTNESS_II);
		tempMap.put(HEALING, HEALING_II);
		tempMap.put(HARMING, HARMING_II);
		tempMap.put(POISON, POISON_II);
		tempMap.put(POISON_E, POISON_II);
		tempMap.put(REGENERATION, REGENERATION_II);
		tempMap.put(REGENERATION_E, REGENERATION_II);
		tempMap.put(WEAKNESS_E, WEAKNESS_R);
		tempMap.put(STRENGTH, STRENGTH_II);
		tempMap.put(STRENGTH_E, STRENGTH_II);
		recipes.put(Material.GLOWSTONE_DUST.getId(), tempMap);
		
		// FERMENTED SPIDER EYE
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(WATER, WEAKNESS);
		tempMap.put(AWKWARD, WEAKNESS);
		tempMap.put(THICK, WEAKNESS);
		tempMap.put(MUNDANE, WEAKNESS);
		tempMap.put(MUNDANE_E, WEAKNESS_E);
		tempMap.put(NIGHTVISION, INVISIBILITY);
		tempMap.put(NIGHTVISION_E, INVISIBILITY_E);
		tempMap.put(FIRERESIST, SLOWNESS);
		tempMap.put(FIRERESIST_E, SLOWNESS_E);
		tempMap.put(SWIFTNESS, SLOWNESS);
		tempMap.put(SWIFTNESS_E, SLOWNESS);
		tempMap.put(SWIFTNESS_II, SLOWNESS_E);
		tempMap.put(HEALING, HARMING);
		tempMap.put(HEALING_II, HARMING_II);
		tempMap.put(POISON, HARMING);
		tempMap.put(POISON_E, HARMING);
		tempMap.put(POISON_II, HARMING_II);
		tempMap.put(REGENERATION_E, WEAKNESS_E);
		tempMap.put(REGENERATION, WEAKNESS);
		tempMap.put(REGENERATION_II, WEAKNESS);
		tempMap.put(STRENGTH, WEAKNESS);
		tempMap.put(STRENGTH_II, WEAKNESS);
		tempMap.put(STRENGTH_E, WEAKNESS_E);
		recipes.put(Material.FERMENTED_SPIDER_EYE.getId(), tempMap);
		
		// GUNPOWDER - SPLASH VERSIONS
		tempMap = new HashMap<Integer, Integer>();
		tempMap.put(MUNDANE, SPLASH ^ MUNDANE);
		tempMap.put(REGENERATION, SPLASH ^ REGENERATION);
		tempMap.put(REGENERATION_E, SPLASH ^ REGENERATION_E);
		tempMap.put(REGENERATION_II, SPLASH ^ REGENERATION_II);
		tempMap.put(SWIFTNESS, SPLASH ^ SWIFTNESS);
		tempMap.put(SWIFTNESS_E, SPLASH ^ SWIFTNESS_E);
		tempMap.put(SWIFTNESS_II, SPLASH ^ SWIFTNESS_II);
		tempMap.put(FIRERESIST, SPLASH ^ FIRERESIST);
		tempMap.put(FIRERESIST_E, SPLASH ^ FIRERESIST_E);
		tempMap.put(FIRERESIST_R, SPLASH ^ FIRERESIST_R);
		tempMap.put(HEALING, SPLASH ^ HEALING);
		tempMap.put(HEALING_II, SPLASH ^ HEALING_II);
		tempMap.put(HEALING_R, SPLASH ^ HEALING_R);
		tempMap.put(NIGHTVISION, SPLASH ^ NIGHTVISION);
		tempMap.put(NIGHTVISION_E, SPLASH ^ NIGHTVISION_E);
		tempMap.put(STRENGTH, SPLASH ^ STRENGTH);
		tempMap.put(STRENGTH_E, SPLASH ^ STRENGTH_E);
		tempMap.put(STRENGTH_II, SPLASH ^ STRENGTH_II);
		tempMap.put(INVISIBILITY, SPLASH ^ INVISIBILITY);
		tempMap.put(INVISIBILITY_E, SPLASH ^ INVISIBILITY_E);
		tempMap.put(POISON, SPLASH ^ POISON);
		tempMap.put(POISON_E, SPLASH ^ POISON_E);
		tempMap.put(POISON_II, SPLASH ^ POISON_II);
		tempMap.put(WEAKNESS, SPLASH ^ WEAKNESS);
		tempMap.put(WEAKNESS_E, SPLASH ^ WEAKNESS_E);
		tempMap.put(WEAKNESS_R, SPLASH ^ WEAKNESS_R);
		tempMap.put(SLOWNESS, SPLASH ^ SLOWNESS);
		tempMap.put(SLOWNESS_E, SPLASH ^ SLOWNESS_E);
		tempMap.put(SLOWNESS_R, SPLASH ^ SLOWNESS_R);
		tempMap.put(HARMING, SPLASH ^ HARMING);
		tempMap.put(HARMING_II, SPLASH ^ HARMING_II);
		tempMap.put(HARMING_R, SPLASH ^ HARMING_R);
		recipes.put(Material.SULPHUR.getId(), tempMap);
	}
}
