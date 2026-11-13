package me.whereareiam.keystone;

import me.whereareiam.keystone.common.serializer.DefaultSerializerEngine;
import me.whereareiam.keystone.common.serializer.adapter.*;
import me.whereareiam.keystone.common.serializer.legacy.DefaultLegacyCodeConverter;
import me.whereareiam.keystone.decorator.PlayerNameDecorator;
import me.whereareiam.keystone.decorator.PrefixDecorator;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.LegacyCodeConverter;
import me.whereareiam.keystone.serializer.MessageDecorator;
import me.whereareiam.keystone.serializer.SerializerAdapter;
import me.whereareiam.keystone.serializer.SerializerEngine;
import org.jetbrains.annotations.NotNull;

/**
 * Static helper class for creating and configuring SerializerEngine instances.
 * This is the main entry point for using Keystone's serialization system.
 */
@SuppressWarnings("unused")
public final class Serializers {
	private static final LegacyCodeConverter LEGACY_CODES = new DefaultLegacyCodeConverter();

	/**
	 * Gets the converter that turns legacy codes, such as {@code &c} or {@code &#ff0000}, into MiniMessage tags,
	 * the one the MiniMessage adapter applies when legacy colors are enabled. Unlike the adapter, it lets the
	 * caller choose which codes to convert:
	 * <pre>{@code
	 * String miniMessage = Serializers.legacyCodes().toMiniMessage(
	 *         playerText,
	 *         LegacyInput.MINIMESSAGE,
	 *         code -> code instanceof LegacyCode.NamedColor
	 * );
	 * }</pre>
	 *
	 * @return the legacy code converter, shared by every caller
	 */
	@NotNull
	public static LegacyCodeConverter legacyCodes() {
		return LEGACY_CODES;
	}

	/**
	 * Creates a new SerializerEngine with the given options.
	 * Automatically registers built-in adapters and decorators based on options.
	 *
	 * @param options The serialization options
	 * @return A configured SerializerEngine instance
	 */
	@NotNull
	public static SerializerEngine createEngine(@NotNull SerializerOptions options) {
		DefaultSerializerEngine engine = new DefaultSerializerEngine(options);

		// Register built-in adapters
		registerBuiltInAdapters(engine);

		// Register built-in decorators
		registerBuiltInDecorators(engine, options);

		return engine;
	}

	/**
	 * Registers a custom adapter with the engine.
	 * This should be called before using the engine, or the adapter won't be available.
	 *
	 * @param engine  The engine to register the adapter with
	 * @param adapter The adapter to register
	 */
	public static void registerAdapter(@NotNull SerializerEngine engine, @NotNull SerializerAdapter adapter) {
		if (engine instanceof DefaultSerializerEngine impl) {
			impl.registerAdapter(adapter);
			return;
		}

		throw new IllegalArgumentException("Engine must be created via Serializers.createEngine()");
	}

	/**
	 * Adds a decorator to an existing engine.
	 *
	 * @param engine    The engine to add the decorator to
	 * @param decorator The decorator to add
	 */
    public static void registerDecorator(@NotNull SerializerEngine engine, @NotNull MessageDecorator decorator) {
        if (engine instanceof DefaultSerializerEngine impl) {
            impl.addDecorator(decorator);
            return;
        }

        throw new IllegalArgumentException("Engine must be created via Serializers.createEngine()");
    }

    /**
     * Adds a scoped decorator to an existing engine.
     *
     * @param engine    The engine to add the decorator to
     * @param scope     The scope identifier (e.g., module name)
     * @param decorator The decorator to add
     */
    public static void registerScopedDecorator(
            @NotNull SerializerEngine engine,
            @NotNull String scope,
            @NotNull MessageDecorator decorator
    ) {
        if (engine instanceof DefaultSerializerEngine impl) {
            impl.addDecorator(scope, decorator);
            return;
        }

        throw new IllegalArgumentException("Engine must be created via Serializers.createEngine()");
    }

	/**
	 * Registers all built-in adapters with the engine.
	 *
	 * @param engine The engine to register adapters with
	 */
	private static void registerBuiltInAdapters(@NotNull DefaultSerializerEngine engine) {
		engine.registerAdapter(new MiniMessageAdapter());
		engine.registerAdapter(new GsonAdapter());
		engine.registerAdapter(new PlainAdapter());
		engine.registerAdapter(new LegacyAmpersandAdapter());
		engine.registerAdapter(new LegacySectionAdapter());
	}

	/**
	 * Registers built-in decorators based on options.
	 *
	 * @param engine  The engine to register decorators with
	 * @param options The options to configure decorators
	 */
	private static void registerBuiltInDecorators(@NotNull DefaultSerializerEngine engine, @NotNull SerializerOptions options) {
		// Prefix decorator (if prefix supplier is provided)
		if (options.getPrefixSupplier() != null)
			engine.addDecorator(new PrefixDecorator(options.getPrefixSupplier()));

		// Player name decorator (if enabled)
		if (options.isEnablePlayerNamePlaceholder())
			engine.addDecorator(new PlayerNameDecorator());
	}
}

