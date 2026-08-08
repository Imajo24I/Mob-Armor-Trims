package net.majo24.naturally_trimmed.config;

import com.mojang.datafixers.util.Either;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.regex.Pattern;

/// Powers materialFilter and patternFilter, which both have been deprecated and replaced by trimFilter
/// Continues to exist in a stripped down version for config migration purposes
// Having the generic / type argument here is required as Config serialization needs to infer whether this filter rule applies to trim material or pattern via the type arguments
public record DisjointedFilterRule<T>(Either<Pattern, TagKey> source, FilterDirection filterDirection) {
    private DisjointedFilterRule(FilterDirection filterDirection, Either<Pattern, TagKey> source) {
        this(source, filterDirection);
    }

    public static <T> DisjointedFilterRule<T> construct(String src, boolean isMaterial) {
        FilterDirection direction = FilterDirection.Blacklist;
        if (src.startsWith(FilterDirection.Whitelist.encoding)) {
            direction = FilterDirection.Whitelist;
            src = src.substring(1);
        } else if (src.startsWith(FilterDirection.Blacklist.encoding)) {
            src = src.substring(1);
        } else {
            NaturallyTrimmed.LOGGER.warn("Missing white-/blacklist encoding (-/+ prefix) for a Trim {} Filter Config Entry. Defaulting to '-'. This might cause an YetAnotherConfigLib error, which can be ignored.", isMaterial ? "Material" : "Pattern");
        }

        return new DisjointedFilterRule<>(direction, getFilter(src, isMaterial));
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
        return this.filterDirection.encoding + this.source.map(Pattern::pattern, tag -> "#" + tag.location());
        //~}
        //~}
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
