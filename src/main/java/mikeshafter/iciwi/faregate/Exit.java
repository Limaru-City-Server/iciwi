package mikeshafter.iciwi.faregate;

import mikeshafter.iciwi.CardSql;
import mikeshafter.iciwi.Iciwi;
import mikeshafter.iciwi.api.ClosableFareGate;
import mikeshafter.iciwi.api.IcCard;
import mikeshafter.iciwi.config.Fares;
import mikeshafter.iciwi.config.Lang;
import mikeshafter.iciwi.util.IciwiUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class Exit extends ClosableFareGate {

private final Iciwi plugin = Iciwi.getPlugin(Iciwi.class);
private final Lang lang = new Lang();
private static final CardSql cardSql = new CardSql();

public Exit() {
    super();
    super.setSignLine0(lang.getString("exit"));
}

@Override public void onInteract(Player player, ItemStack item, String[] signText, Sign sign) {
    TicketType ticketType = TicketType.asTicketType(item.getType());
    if (!IciwiUtil.loreCheck(item) || ticketType == null) return;

    // Get station
    String station = IciwiUtil.stripColor(signText[1]);

    // Wax sign
    sign.setWaxed(true);
    sign.update(true);

    List<String> lore = IciwiUtil.parseComponents(Objects.requireNonNull(item.getItemMeta().lore()));

    switch (Objects.requireNonNull(ticketType)) {
        case TICKET:
        boolean entryPunched = lore.get(0).contains("•");
        boolean exitPunched	= lore.get(1).contains("•");
        boolean entryPunchRequired = plugin.getConfig().getBoolean("require-entry-punch");

        // Invalid Ticket
        if (entryPunched && exitPunched) {
            player.sendMessage(lang.getString("invalid-ticket"));
        }

        // Exit
        else if ((entryPunched || !entryPunchRequired) && lore.get(1).equals(station)) {
            IciwiUtil.punchTicket(item, 1);
            player.sendMessage(String.format(lang.getString("ticket-out"), station));
            // Log exit
            String entryStation = lore.get(0).replace(" •", "");
            String fareClass = lore.get(2);
            Fares fares = new Fares();
             
            cardSql.logMaster(player.getUniqueId().toString());
            cardSql.logExit(sign.getLocation().getBlockX(), sign.getLocation().getBlockY(), sign.getLocation().getBlockZ(), entryStation, station);
            cardSql.logJourney(fares.getFare(entryStation, station, fareClass), fares.getFare(entryStation, station, fareClass), fareClass);
            cardSql.logTicketUse(entryStation, station, fareClass);
            super.setCloseGateArray(CardUtil.openGate(lang.getString("exit"), signText, sign));
        }

        // Ticket not used
        else if (!entryPunched && entryPunchRequired) {
            player.sendMessage(lang.getString("cannot-pass"));
        }

        else {
            player.sendMessage(lang.getString("invalid-ticket"));
        }
        break;

        case CARD:
        // Get card from item
        IcCard icCard = IciwiUtil.IcCardFromItem(item);
        if (icCard == null) return;

        // Call entry, and if successful, open fare gate
        if (CardUtil.exit(player, icCard, station, sign.getLocation())) {
            super.setCloseGateArray(CardUtil.openGate(lang.getString("exit"), signText, sign));
        }
        break;

        case RAIL_PASS:
                String name = lore.get(0);
                String expiry = lore.get(1);

                try {
                // check if expired
                    long e = Long.parseLong(expiry);
                // if expired, return and do not open the gate
                    if (e < System.currentTimeMillis()) {
                        return;
                    }
                // otherwise, check if issuing TOC is one of the station's owners
                    List<String> tocs = owners.getOwners(station);
                    if (tocs.contains(owners.getRailPassOperator(name))) {
                // if yes, open the gate
                        super.setCloseGateArray(CardUtil.openGate(lang.getString("entry"), signText, sign));
                    }
                }
catch (Exception ignored) {
                    return;
                }
                break;
        break;
    }
}

}
