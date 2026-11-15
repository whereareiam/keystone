package me.whereareiam.keystone.common.recipient;

import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.recipient.RecipientGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * Recipients taken at one moment.
 *
 * @param members the recipients
 */
record DefaultRecipientGroup(@NotNull List<Recipient> members) implements RecipientGroup {
	DefaultRecipientGroup {
		members = List.copyOf(members);
	}

	@Override
	public @NotNull Iterator<Recipient> iterator() {
		return members.iterator();
	}
}
