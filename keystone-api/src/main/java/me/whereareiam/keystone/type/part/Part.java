package me.whereareiam.keystone.type.part;

/**
 * One way a text is shown: {@link Chat}, {@link ActionBar}, {@link Title}, {@link BossBar} or {@link Sound}.
 * A {@link me.whereareiam.keystone.model.Display} is a list of them.
 * <pre>{@code
 * Display display = Display.of(
 *         Chat.of(Content.of("{@chirper.vote}")),
 *         BossBar.builder().text(Content.of("<green>/vote")).color(net.kyori.adventure.bossbar.BossBar.Color.GREEN).build());
 * }</pre>
 */
public sealed interface Part permits Chat, ActionBar, Title, BossBar, Sound {
}
