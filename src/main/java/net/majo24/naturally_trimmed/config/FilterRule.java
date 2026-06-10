package net.majo24.naturally_trimmed.config;

import com.mojang.datafixers.util.Either;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.regex.Pattern;

public class FilterRule<T> {
    private final Either<Pattern, TagKey> source;
    public final FilterDirection filterDirection;

    private FilterRule(FilterDirection filterDirection, Either<Pattern, TagKey> source) {
        this.source = source;
        this.filterDirection = filterDirection;
    }

    public static <T> FilterRule<T> construct(String src, boolean isMaterial) {
        FilterDirection direction = FilterDirection.Blacklist;
        if (src.startsWith(FilterDirection.Whitelist.encoding)) {
            direction = FilterDirection.Whitelist;
            src = src.substring(1);
        } else if (src.startsWith(FilterDirection.Blacklist.encoding)) {
            src = src.substring(1);
        } else {
            NaturallyTrimmed.LOGGER.warn("Missing white-/blacklist encoding (-/+ prefix) for a Trim {} Filter Config Entry. Defaulting to '-'. This might cause an YetAnotherConfigLib error, which can be ignored.", isMaterial ? "Material" : "Pattern");
        }

        return new FilterRule<>(direction, getFilter(src, isMaterial));
    }

    private static Either<Pattern, TagKey> getFilter(String src, boolean isMaterial) {
        if (src.startsWith("#")) {
            Identifier id = Identifier.parse(src.substring(1));

            if (isMaterial) {
                return Either.right(TagKey.create(Registries.TRIM_MATERIAL, id));
            } else {
                return Either.right(TagKey.create(Registries.TRIM_PATTERN, id));
            }
        } else {
            return Either.left(Pattern.compile(src));
        }
    }

    @Override
    public String toString() {
        //~ if >=1.21.11 '.identifier()' -> '.location()' { *mojank forgott to rename this to identifier() :<( -> needed due to the global replacement*
        //~ if >=1.21.11 '.location()' -> '.location()' {
        return this.filterDirection.encoding + this.source.left().map(Pattern::pattern)
                .orElseGet(() -> "#" + this.source.right().get().location());
        //~}
        //~}
    }

    public static <T> boolean resolveFilterForBlacklisted(List<FilterRule<T>> filter, Holder.Reference<?> trimPart) {
        // Find the first trim filter that matches and handle blacklisted/whitelisted
        for (FilterRule<T> rule : filter) {
            switch (rule.matches(trimPart)) {
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

    public FilterResult matches(Holder.Reference<?> trimPart) {
        return this.source.map(
                pattern -> {
                    if (pattern.matcher(trimPart.key().identifier().toString()).matches()) {
                        return this.filterDirection.matchResult;
                    }

                    return FilterResult.Irrelevant;
                },
                tag -> {
                    if (trimPart.is(tag)) {
                        return this.filterDirection.matchResult;
                    }

                    return FilterResult.Irrelevant;
                }
        );
    }

    public enum FilterDirection {
        Whitelist("+", FilterResult.Whitelisted),
        Blacklist("-", FilterResult.Blacklisted);

        public final String encoding;
        public final FilterResult matchResult;

        FilterDirection(String encoding, FilterResult matchResult) {
            this.encoding = encoding;
            this.matchResult = matchResult;
        }
    }

    public enum FilterResult {
        Whitelisted,
        Blacklisted,
        Irrelevant
    }
}
