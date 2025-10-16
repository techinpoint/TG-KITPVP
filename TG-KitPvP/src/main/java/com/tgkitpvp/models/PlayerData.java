package com.tgkitpvp.models;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private String playerName;
    private int kills;
    private int deaths;
    private int currentStreak;
    private int bestStreak;
    private long lastKitChange;
    private String currentKit;
    private boolean inArena;

    public PlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.kills = 0;
        this.deaths = 0;
        this.currentStreak = 0;
        this.bestStreak = 0;
        this.lastKitChange = 0;
        this.currentKit = null;
        this.inArena = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        this.kills++;
        this.currentStreak++;
        if (this.currentStreak > this.bestStreak) {
            this.bestStreak = this.currentStreak;
        }
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void addDeath() {
        this.deaths++;
        this.currentStreak = 0;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public long getLastKitChange() {
        return lastKitChange;
    }

    public void setLastKitChange(long lastKitChange) {
        this.lastKitChange = lastKitChange;
    }

    public String getCurrentKit() {
        return currentKit;
    }

    public void setCurrentKit(String currentKit) {
        this.currentKit = currentKit;
    }

    public boolean isInArena() {
        return inArena;
    }

    public void setInArena(boolean inArena) {
        this.inArena = inArena;
    }

    public double getKDRatio() {
        if (deaths == 0) {
            return kills;
        }
        return (double) kills / deaths;
    }
}
