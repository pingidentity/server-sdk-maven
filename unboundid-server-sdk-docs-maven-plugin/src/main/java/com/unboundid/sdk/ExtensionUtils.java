/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * docs/licenses/cddl.txt
 * or http://www.opensource.org/licenses/cddl1.php.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * docs/licenses/cddl.txt.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2010-2016 UnboundID Corp.
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
          catch (final Exception e)
          {
            e.printStackTrace();
            // This shouldn't happen, but if it does, then there's not much that
            // can be done about it.
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
