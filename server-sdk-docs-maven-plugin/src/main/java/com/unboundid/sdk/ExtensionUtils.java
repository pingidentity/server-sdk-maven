/*
 * Copyright 2016-2017 Ping Identity Corporation
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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.unboundid.directory.sdk.common.internal.UnboundIDExtension;


/**
 * This class provides utility methods used by {@link ExtensionDocsMojo} to
 * locate and instantiate Server SDK extension classes.
 */
public final class ExtensionUtils
{
  /**
   * Finds the classes for all extensions built from source in the specified
   * location.
   *
   * @param sourceRoot
   *          The path to the directory structure containing the
   *          source files for which to find the associated classes.
   *
   * @return A list of all extension classes below the provided source root.
   */
  public static List<UnboundIDExtension> findExtensions(final File sourceRoot)
  {
    final LinkedList<UnboundIDExtension> extensionList = new LinkedList<>();
    findExtensionClasses(sourceRoot, null, extensionList);
    return extensionList;
  }


  /**
   * Finds all extensions built from source in the specified location.
   *
   * @param  srcDirectory
   *           The directory to examine for extension source files.
   * @param  pkg
   *           The Java package associated with the provided directory.
   *           May be {@code null}.
   * @param  extensionList
   *           The list to which any extensions should be added.
   */
  private static void findExtensionClasses(final File srcDirectory,
                                           final String pkg,
                                           final List<UnboundIDExtension> extensionList)
  {
    if (!srcDirectory.exists())
    {
      return;
    }

    if (!srcDirectory.isDirectory())
    {
      return;
    }

    for (final File file : listFiles(srcDirectory))
    {
      if (file.isFile())
      {
        final String name = file.getName();
        if (!name.endsWith(".java"))
        {
          continue;
        }

        final Class<?> c;
        final String prefixedPkg = pkg != null ? pkg + "." : "";
        final String className = prefixedPkg +
            name.substring(0, (name.length() - 5));
        try
        {
          c = Class.forName(className);
        }
        catch (final Throwable t)
        {
          t.printStackTrace();
          continue;
        }

        if (UnboundIDExtension.class.isAssignableFrom(c))
        {
          try
          {
            extensionList.add((UnboundIDExtension) c.newInstance());
          }
          catch (final IllegalAccessException | InstantiationException e)
          {
            // Ignore non-public or abstract classes.
          }
        }
      }
      else if (file.isDirectory())
      {
        final String newPkg;
        if (pkg == null)
        {
          newPkg = file.getName();
        }
        else
        {
          newPkg = pkg + '.' + file.getName();
        }

        findExtensionClasses(file, newPkg, extensionList);
      }
    }
  }


  /**
   * Returns files from the provided directory.
   *
   * @param directory
   *          The directory from which files are listed.
   * @return The files from the provided directory or an empty array if no files
   *         can be found. This method never returns {@code null}.
   */
  public static File[] listFiles(final File directory)
  {
    if (directory != null)
    {
      final File[] files = directory.listFiles();
      if (files != null)
      {
        return files;
      }
    }

    return new File[0];
  }
}
