---
version: 1.1.0
---

# Keystone 1.1.0 - Legacy codes

Keystone 1.1.0 makes its legacy code handling public: `Serializers.legacyCodes()` turns `&` and `§` codes into MiniMessage tags and lets a plugin choose which codes to convert, such as only the colours a player may use.

## Highlights

* Formats: `LegacyCodeConverter` reads `&0`-`&f`, `&k`-`&o`, `&r`, `&#rrggbb` and `&x&r&r&g&g&b&b`, hands each code to a filter as a typed `LegacyCode` and keeps the codes the filter refuses as typed. It reads plain text, which it escapes, or MiniMessage text, whose tags it keeps.

## Upgrading

* With legacy colors enabled, MiniMessage messages now read legacy codes as the vanilla client does: a colour code ends the decorations before it (`&lBold &cRed` is no longer bold), and a code right after another counts (`&a&lHi` is green and bold instead of showing `&l`).
