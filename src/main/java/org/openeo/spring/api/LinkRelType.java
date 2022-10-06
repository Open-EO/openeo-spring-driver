package org.openeo.spring.api;

/**
 * Types of links relations to be used in STAC Link objects.
 *
 * @see <a href="https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md#link-object">Link Objects</a>
 * @see <a href="https://www.iana.org/assignments/link-relations/link-relations.xhtml">IANA link relations</a>
 */
public enum LinkRelType {
    ABOUT,
    LICENCE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
