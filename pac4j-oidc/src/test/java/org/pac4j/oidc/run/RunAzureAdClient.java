package org.pac4j.oidc.run;

import com.esotericsoftware.kryo.Kryo;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.profile.Gender;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.oidc.client.AzureAdClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.kryo.AccessTokenTypeSerializer;
import org.pac4j.oidc.profile.AzureAdIdTokenProfile;
import org.pac4j.oidc.profile.AzureAdProfile;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link AzureAdClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class RunAzureAdClient extends RunClient {

    public static void main(final String[] args) throws Exception {
        new RunAzureAdClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribe12";
    }

    @Override
    protected IndirectClient getClient() {
        final OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("788339d7-1c44-4732-97c9-134cb201f01f");
        configuration.setSecret("we/31zi+JYa7zOugO4TbSw0hzn+hv2wmENO9AS3T84s=");
        configuration.setDiscoveryURI("https://login.microsoftonline.com/38c46e5a-21f0-46e5-940d-3ca06fd1a330/.well-known/openid-configuration");
        final AzureAdClient client = new AzureAdClient(configuration);
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(AzureAdProfile.class);
        kryo.register(AccessTokenType.class, new AccessTokenTypeSerializer());
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final AzureAdProfile profile = (AzureAdProfile) userProfile;
        assertEquals("alVNQ8eaO_Psdu7MIYRy5oGbqe5YD2BxKlDm3rwXseE", profile.getId());
        assertEquals(AzureAdProfile.class.getName() + CommonProfile.SEPARATOR + "alVNQ8eaO_Psdu7MIYRy5oGbqe5YD2BxKlDm3rwXseE",
                profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), AzureAdProfile.class));
        assertNotNull(profile.getIdTokenString());
        assertCommonProfile(profile, getLogin(), "Jérôme", "TESTPAC4J", "MyDisplayName", null,
                Gender.UNSPECIFIED, null, null, null, null);
        assertEquals("live.com", profile.getIdp());
        assertEquals("6c59c433-11b5-4fb1-9641-40b829e7a8e4", profile.getAttribute("oid"));
        assertEquals("38c46e5a-21f0-46e5-940d-3ca06fd1a330", profile.getAttribute("tid"));
        assertEquals(11, profile.getAttributes().size());
        final AzureAdIdTokenProfile idTokenProfile = profile.getIdToken().get();
        assertEquals("1.0", idTokenProfile.getAttribute("ver"));
        assertCommonProfile(idTokenProfile, getLogin(), "Jérôme", "TESTPAC4J", "MyDisplayName", null,
                Gender.UNSPECIFIED, null, null, null, null);
        assertNotNull(idTokenProfile.getAmr());
        assertNotNull(idTokenProfile.getIssuer());
        assertEquals("6c59c433-11b5-4fb1-9641-40b829e7a8e4", idTokenProfile.getAttribute("oid"));
        assertEquals("38c46e5a-21f0-46e5-940d-3ca06fd1a330", idTokenProfile.getAttribute("tid"));
        final List<String> audience =  (List<String>) idTokenProfile.getAudience();
        assertEquals("788339d7-1c44-4732-97c9-134cb201f01f", audience.get(0));
        assertEquals("live.com#" + getLogin(), idTokenProfile.getAttribute("unique_name"));
        assertNotNull(idTokenProfile.getAttribute("nbf"));
        assertEquals("live.com", idTokenProfile.getIdp());
        assertNotNull(idTokenProfile.getExpirationDate());
        assertNotNull(idTokenProfile.getAttribute("ipaddr"));
        assertNotNull(idTokenProfile.getIssuedAt());
        assertEquals(16, idTokenProfile.getAttributes().size());
    }
}
