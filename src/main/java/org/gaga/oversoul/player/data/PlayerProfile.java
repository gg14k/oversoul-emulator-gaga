package org.gaga.oversoul.player.data;

/**
 * Dados persistentes do perfil/personagem principal do jogador.
 *
 * Representa os dados da tabela player_characters.
 */
public class PlayerProfile {

    private final int playerCharacterId;

    private String name;

    private int gold;
    private int gem;
    private int alignment;

    private int attackPower;
    private int block;
    private int chaos;
    private int criticalHit;
    private int damagePower;

    private int earth;
    private int energy;
    private int fire;
    private int hits;
    private int homeTown;
    private int ice;
    private int initiative;
    private int light;
    private int luck;
    private int neutral;
    private int power;
    private int stamina;
    private int shadow;
    private int water;

    public PlayerProfile(
            int playerCharacterId,
            String name,
            int gold,
            int gem,
            int alignment,
            int attackPower,
            int block,
            int chaos,
            int criticalHit,
            int damagePower,
            int earth,
            int energy,
            int fire,
            int hits,
            int homeTown,
            int ice,
            int initiative,
            int light,
            int luck,
            int neutral,
            int power,
            int stamina,
            int shadow,
            int water
    ) {
        this.playerCharacterId = playerCharacterId;
        this.name = name;
        this.gold = gold;
        this.gem = gem;
        this.alignment = alignment;
        this.attackPower = attackPower;
        this.block = block;
        this.chaos = chaos;
        this.criticalHit = criticalHit;
        this.damagePower = damagePower;
        this.earth = earth;
        this.energy = energy;
        this.fire = fire;
        this.hits = hits;
        this.homeTown = homeTown;
        this.ice = ice;
        this.initiative = initiative;
        this.light = light;
        this.luck = luck;
        this.neutral = neutral;
        this.power = power;
        this.stamina = stamina;
        this.shadow = shadow;
        this.water = water;
    }

    public int getPlayerCharacterId() {
        return playerCharacterId;
    }

    public String getName() {
        return name;
    }

    public int getGold() {
        return gold;
    }

    public int getGem() {
        return gem;
    }

    public int getAlignment() {
        return alignment;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getBlock() {
        return block;
    }

    public int getChaos() {
        return chaos;
    }

    public int getCriticalHit() {
        return criticalHit;
    }

    public int getDamagePower() {
        return damagePower;
    }

    public int getEarth() {
        return earth;
    }

    public int getEnergy() {
        return energy;
    }

    public int getFire() {
        return fire;
    }

    public int getHits() {
        return hits;
    }

    public int getHomeTown() {
        return homeTown;
    }

    public int getIce() {
        return ice;
    }

    public int getInitiative() {
        return initiative;
    }

    public int getLight() {
        return light;
    }

    public int getLuck() {
        return luck;
    }

    public int getNeutral() {
        return neutral;
    }

    public int getPower() {
        return power;
    }

    public int getStamina() {
        return stamina;
    }

    public int getShadow() {
        return shadow;
    }

    public int getWater() {
        return water;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setGem(int gem) {
        this.gem = gem;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }
}