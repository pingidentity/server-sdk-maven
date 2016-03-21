package ${package};

import com.unboundid.directory.sdk.ds.api.IdentityMapper;
import com.unboundid.directory.sdk.ds.config.IdentityMapperConfig;
import com.unboundid.directory.sdk.ds.types.DirectoryServerContext;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;

/**
 * An example UnboundID Server SDK extension that implements a simple identity
 * mapper.
 */
public class MyExampleExtension extends IdentityMapper
{
  private String emailDomain;

  @Override
  public String getExtensionName()
  {
    return "Example Identity Mapper";
  }


  @Override
  public String[] getExtensionDescription()
  {
    return new String[] {
        "This is an example identity mapper extension that converts a " +
            "username to an email address.",
        "This is a second paragraph."
    };
  }


  @Override()
  public void defineConfigArguments(final ArgumentParser parser)
      throws ArgumentException
  {
    parser.addArgument(new StringArgument(
        null, "emailDomain",
        true, 1, "emailDomain",
        "The domain name to be used when constructing an email address."));
  }


  @Override()
  public void initializeIdentityMapper(
      final DirectoryServerContext serverContext,
      final IdentityMapperConfig config,
      final ArgumentParser parser)
      throws LDAPException
  {
    final StringArgument emailDomainArgument =
        parser.getStringArgument("emailDomain");
    emailDomain = emailDomainArgument.getValue();
  }


  @Override
  public String mapUsername(String username) throws LDAPException
  {
    return String.format("%s@%s", username, emailDomain);
  }
}