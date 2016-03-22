package ${package};

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


  @Override
  public Map<List<String>, String> getExamplesArgumentSets()
  {
    List<String> exampleArguments1 = Arrays.asList("emailDomain", "mydomain.com");
    List<String> exampleArguments2 = Collections.emptyList();
    Map<List<String>, String> exampleArgumentSets = new HashMap<>();
    exampleArgumentSets.put(exampleArguments1,
                            "Sets 'mydomain.com' as the domain to be used " +
                                "when contructing email addresses.");
    exampleArgumentSets.put(exampleArguments2,
                            "Uses the default email domain of 'example.com'.");
    return exampleArgumentSets;
  }


  @Override()
  public void defineConfigArguments(final ArgumentParser parser)
      throws ArgumentException
  {
    parser.addArgument(new StringArgument(
        null, "emailDomain",
        true, 1, "emailDomain",
        "The domain name to be used when constructing an email address.",
        "example.com"));
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
