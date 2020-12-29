package com.github.pulsebeat02;

public class PlayerStatus {

    private boolean war;           // true -> in war, false -> peaceful
    private boolean combat;
    private long peacefulCooldown;  // ticks to switch to peaceful
    private long warCooldown;       // ticks to switch to war
    private long combatCooldown;

    public PlayerStatus(final boolean war, final long peacefulCooldown, final long warCooldown, final boolean combat, final long combatCooldown) {
        this.war = war;
        this.peacefulCooldown = peacefulCooldown;
        this.warCooldown = warCooldown;
        this.combat = combat;
        this.combatCooldown = combatCooldown;
    }

    public boolean isWar() {
        return war;
    }

    public void setWar(final boolean war) {
        this.war = war;
    }

    public boolean isCombat() {
        return combat;
    }

    public void setCombat(boolean combat) {
        this.combat = combat;
    }

    public long getPeacefulCooldown() {
        return peacefulCooldown;
    }

    public void setPeacefulCooldown(final long peacefulCooldown) {
        this.peacefulCooldown = peacefulCooldown;
    }

    public long getWarCooldown() {
        return warCooldown;
    }

    public void setWarCooldown(final long warCooldown) {
        this.warCooldown = warCooldown;
    }

    public long getCombatCooldown() {
        return combatCooldown;
    }

    public void setCombatCooldown(long combatCooldown) {
        this.combatCooldown = combatCooldown;
    }

}
