/*
 * Copyright 2016 UnboundID Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.unboundid.sdk;

import com.unboundid.directory.sdk.common.internal.Configurable;
import com.unboundid.directory.sdk.common.internal.ExampleUsageProvider;
import com.unboundid.directory.sdk.common.internal.UnboundIDExtension;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import org.apache.velocity.exception.VelocityException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Server SDK-related utility methods that are made available to the Velocity
 * templates used when generating Server SDK extension documentation.
 *
 * @author jacobc
 */
public final class VelocityUtils
{
  /**
   * Generates a documentation filename to use for the provided extension.
   *
   * @param extension
   *          The extension for which to generate documentation.
   *
   * @return The filename to use for the documentation generated for the
   *         provided extension.
   */
  public static String getExtensionFilename(final UnboundIDExtension extension)
  {
    return extension.getClass().getName().replace('.', '_') + ".html";
  }


  /**
   * Gets an extension's arguments.
   *
   * @param extension
   *          The extension for which to generate documentation.
   * @return The extension's arguments. Never returns {@code null}.
   * @throws VelocityException if an error occurs while handling the extension's
   *         arguments.
   */
  public static List<Argument> getExtensionArguments(final UnboundIDExtension extension)
  {
    List<Argument> arguments = new ArrayList<>();
    if (extension instanceof Configurable)
    {
      final Configurable configurable = (Configurable) extension;

      List<Argument> allArguments;
      try
      {
        final ArgumentParser argumentParser = new ArgumentParser("", "");
        configurable.defineConfigArguments(argumentParser);

        allArguments = argumentParser.getNamedArguments();
      }
      catch (final ArgumentException e)
      {
        throw new VelocityException(String.format(
            "A problem occurred while attempting to interact with the " +
                "arguments for extension %s: %s", extension.getClass().getName(),
            StaticUtils.getExceptionMessage(e)), e);
      }

      for (final Argument argument : allArguments)
      {
        if (argument.isHidden())
        {
          continue;
        }

        final String argName = argument.getLongIdentifier();
        if ((argName == null) || (argName.length() == 0))
        {
          throw new VelocityException(String.format(
              "Extension '%s' includes argument '%s' without a long identifier.",
              extension.getClass().getName(), argument));
        }

        arguments.add(argument);
      }
    }
    return arguments;
  }


  /**
   * Gets an extension's example usages.
   *
   * @param extension
   *          The extension for which to generate documentation.
   * @return The extension's example usages, if any, as a map where the key is
   *         a list of example arguments, and the value is a description of the
   *         example usage. Never returns {@code null}.
   */
  public static Map<List<String>, String> getExampleUsages(
      final UnboundIDExtension extension)
  {
    Map<List<String>, String> exampleUsages = new HashMap<>();
    if (extension instanceof ExampleUsageProvider)
    {
      final ExampleUsageProvider exampleUsageProvider =
          (ExampleUsageProvider) extension;
      if (exampleUsageProvider.getExamplesArgumentSets() != null)
      {
        exampleUsages = exampleUsageProvider.getExamplesArgumentSets();
      }
    }
    return exampleUsages;
  }
}
