package de.dktk.dd.rpb.core.domain.ctms;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * SponsoringType entity meta model which is used for JPA
 * This allows to use type save criteria API while constructing queries
 *
 * @author tomas@skripcak.net
 * @since 24 Jun 2015
 */
@SuppressWarnings("unused")
@StaticMetamodel(SponsoringType.class)
public class SponsoringType_ {

    // Raw attributes
    public static volatile SingularAttribute<SponsoringType, Integer> id;
    public static volatile SingularAttribute<SponsoringType, String> name;
    public static volatile SingularAttribute<SponsoringType, String> description;

}
