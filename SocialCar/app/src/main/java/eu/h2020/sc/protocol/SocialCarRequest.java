package eu.h2020.sc.protocol;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public abstract class SocialCarRequest extends BaseRequest {

    public abstract String getBody();

    //FIXME Remove after refactor
    public abstract String getApiName();

}
