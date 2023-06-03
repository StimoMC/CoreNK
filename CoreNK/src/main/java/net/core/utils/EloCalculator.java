package net.core.utils;

import net.core.Options;
import net.core.api.StatsAPI;
import org.jetbrains.annotations.NotNull;

public class EloCalculator {

    /**
     * @return String[]
     * @apiNote with this I can calculate the elo for players :3
     */
    public static String[] calculateElo(@NotNull String winner, @NotNull Integer winnerElo, @NotNull String loser, @NotNull Integer loserElo){
        int elo_winner = (int) Math.floor((1) / (1 + (Math.pow(10, ((double)winnerElo - loserElo) / 400))) * 30);
        elo_winner = (elo_winner == 0 ? 1 : elo_winner);

        int elo_loser = (int) Math.floor((1) / (1 + (Math.pow(10, ((double)loserElo - winnerElo) / 400))) * 30);
        elo_loser = (elo_loser == 0 ? 1 : elo_loser);

        StatsAPI.addElo(winner, elo_winner, Options.statsTable);
        StatsAPI.removeElo(loser, elo_loser, Options.statsTable);

        return new String[]{winner, loser, String.valueOf(elo_winner), String.valueOf(elo_loser)};
    }
}