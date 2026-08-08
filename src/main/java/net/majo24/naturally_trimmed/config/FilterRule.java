package net.majo24.naturally_trimmed.config;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.trim.*;

import java.util.List;
import java.util.regex.Pattern;

public record FilterRule(Direction direction, Either<Pattern, TagKey<TrimMaterial>> materialSource, Either<Pattern, TagKey<TrimPattern>> patternSource) {
    public FilterRule(Direction direction, String materialSource, String patternSource) {
        this(direction, stringToSource(materialSource, Registries.TRIM_MATERIAL), stringToSource(patternSource, Registries.TRIM_PATTERN));
    }

    public static <T> Either<Pattern, TagKey<T>> stringToSource(String source, ResourceKey<? extends Registry<T>> sourceRegistry) {
        if (source.startsWith("#")) {
            Identifier id = Identifier.parse(source.substring(1));
            return Either.right(TagKey.create(sourceRegistry, id));
        } else {
            return Either.left(Pattern.compile(source));
        }
    }

    public static <T> String sourceToString(Either<Pattern, TagKey<T>> source) {
        //~ if >=1.21.11 '.identifier()' -> '.location()' { *mojank forgott to rename this to identifier() :<( -> needed due to the global replacement*
        //~ if >=1.21.11 '.location()' -> '.location()' {
        return source.map(Pattern::pattern, tag -> "#" + tag.location());
        //~}
        //~}
    }

    public static boolean isTrimBlacklistedByFilter(List<FilterRule> filter, ArmorTrim armorTrim) {
        // Find the first trim filter that matches and handle blacklisted/whitelisted
        for (FilterRule rule : filter) {
            switch (rule.matches(armorTrim)) {
                case Blacklisted -> {
                    return true;
                }
                case Whitelisted -> {
                    return false;
                }
            }
        }
        return false;
    }

    public Result matches(ArmorTrim armorTrim) {
        Holder.Reference<TrimMaterial> trimMaterial = (Holder.Reference<TrimMaterial>) armorTrim.material();
        Holder.Reference<TrimPattern> trimPattern = (Holder.Reference<TrimPattern>) armorTrim.pattern();

        if (this.materialSource.map(
                pattern -> pattern.matcher(trimMaterial.key().identifier().toString()).matches(),
                trimMaterial::is
        ) && this.patternSource.map(
                pattern -> pattern.matcher(trimPattern.key().identifier().toString()).matches(),
                trimPattern::is
        )) {
            return this.direction.matchResult;
        }

        return Result.Irrelevant;
    }

    public enum Direction {
        Whitelist(Result.Whitelisted),
        Blacklist(Result.Blacklisted);

        public final Result matchResult;

        Direction(Result matchResult) {
            this.matchResult = matchResult;
        }
    }

    public enum Result {
        Whitelisted,
        Blacklisted,
        Irrelevant
    }
}
