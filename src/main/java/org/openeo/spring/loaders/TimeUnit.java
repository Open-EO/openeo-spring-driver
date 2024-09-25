package org.openeo.spring.loaders;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Official abbreviations of units of measure.
 * 
 * @see <a href="https://ncics.org/portfolio/other-resources/udunits2/">UDUNITS2 Database</a>
 */
public enum TimeUnit {
    DAY("d"),
    YEAR("yr");
    
    private final String abbrv;
    
    private TimeUnit(String abbrv) {
        this.abbrv = abbrv;
    }
    
    /**
     * Get the abbreviation of this unit of measure.
     */
    public String getAbbrv() {
        return abbrv;
    }
    
    /**
     * Returns a list of abbreviations of all available units of measure.
     */
    public static List<String> getAllAbbrv() {
        return Arrays.asList(TimeUnit.values()).stream()
                .map(u -> u.getAbbrv())
                .collect(Collectors.toList());
    }
}
