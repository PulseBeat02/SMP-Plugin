package com.github.pulsebeat02;

public class PlayerStatus {

    private boolean war;           // true -> in war, false -> peaceful
    private boolean combat;
    private long peacefulCooldown;  // ticks to switch to peaceful
    private long warCooldown;       // ticks to switch to war
    private long combatCooldown;

    public PlayerStatus(final boolean war, final long peacefulCooldown, final long warCooldown) {
        this.war = war;
        this.peacefulCooldown = peacefulCooldown;
        this.warCooldown = warCooldown;
        this.combatCooldown = 0;
    }

    public boolean isWar() {
        return war;
    }

    public PlayerStatus setWar(final boolean war) {
        this.war = war;
        return this;
    }

    public boolean isCombat() {
        return combat;
    }

    public PlayerStatus setCombat(boolean combat) {
        this.combat = combat;
        return this;
    }

    public long getPeacefulCooldown() {
        return peacefulCooldown;
    }

    public PlayerStatus setPeacefulCooldown(final long peacefulCooldown) {
        this.peacefulCooldown = peacefulCooldown;
        return this;
    }

    public long getWarCooldown() {
        return warCooldown;
    }

    public PlayerStatus setWarCooldown(final long warCooldown) {
        this.warCooldown = warCooldown;
        return this;
    }

    public long getCombatCooldown() {
        return combatCooldown;
    }

    public PlayerStatus setCombatCooldown(long combatCooldown) {
        this.combatCooldown = combatCooldown;
        return this;
    }

}
