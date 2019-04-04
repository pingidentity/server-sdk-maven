package com.pingidentity.util;

import com.unboundid.directory.sdk.common.api.ManageExtensionPlugin;
import com.unboundid.directory.sdk.common.types.ExtensionBundle;
import com.unboundid.directory.sdk.common.types.InstallExtensionDetails;
import com.unboundid.directory.sdk.common.types.PostManageExtensionPluginResult;
import com.unboundid.directory.sdk.common.types.UpdateExtensionDetails;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by arnaudlacour on 5/17/17.
 */
public class Installer extends ManageExtensionPlugin
{
    /**
     * Returns the extension name
     * @return the extension name
     */
    @Override
    public String getExtensionName()
    {
        return "Generic Extension Installer";
    }

    /**
     * Provides descriptions for the extension
     * @return an array of descriptive paragraphs
     */
    @Override
    public String[] getExtensionDescription()
    {
        return new String[]{"Installs the extension this installer is bundled with"};
    }

    /**
     * Performs the necessary processing after the extension has been updated
     * @param details the extension bundle details
     * @return SUCCESS if the update was successful
     */
    @Override
    public PostManageExtensionPluginResult postUpdate(UpdateExtensionDetails details)
    {
        File instanceRoot = details.getExtensionBundle().getExtensionBundleDir().getParentFile().getParentFile();
        String confBatch = details.getExtensionBundle().getConfigDir() + "/update.dsconfig";
        File configurationBatchFile = new File(confBatch);
        if (configurationBatchFile.exists() && configurationBatchFile.isFile())
        {
            System.out.println();
            System.out.println("You can get started more quickly by running this command: ");
            System.out.println(instanceRoot.toString() + "/bin/dsconfig --no-prompt --batch-file " + confBatch);
        }
        return PostManageExtensionPluginResult.SUCCESS;
    }

    /**
     * Performs the necessary processing after the extension has been installed
     * This currently includes things like laying down schema files, updating documentation, attempting to open
     * a browser to the extension documentation page and displaying the appropriate commands to run
     * @param details the extension bundle details
     * @return SUCCESS if the install was successful
     */
    @Override
    public PostManageExtensionPluginResult postInstall(
            InstallExtensionDetails details)
    {
        File instanceRoot = details.getExtensionBundle().getExtensionBundleDir().getParentFile().getParentFile();

        /* copy schema files included in the extension if any */
        copySchema(details, instanceRoot);

        /* copy the documentation so it can be readily available for users */
        copyDocumentation(instanceRoot, details);

        /* Update the docs index to add a link for convenience */
        updateIndex(instanceRoot, details);

        /* if there is a browser to be opened, attempt to open the documentation directly */
        openBrowser(details.getExtensionBundle());


        String confBatch = details.getExtensionBundle().getConfigDir() + "/install.dsconfig";
        File configurationBatchFile = new File(confBatch);
        if (configurationBatchFile.exists() && configurationBatchFile.isFile())
        {
            System.out.println();
            System.out.println("You can get started more quickly by running this command: ");
            System.out.println(instanceRoot.toString() + "/bin/dsconfig --no-prompt --batch-file " + confBatch);
            System.out.println("Executing this command will create basic configuration objects.");
        }
        return PostManageExtensionPluginResult.SUCCESS;
    }

    /**
     * This method performs the necessary processing to copy schema to the instance
     *
     * @param details      the extension details
     * @param instanceRoot the instance root
     */
    private void copySchema(final InstallExtensionDetails details, File instanceRoot)
    {
        System.out.println();
        System.out.println("Schema preparation");
        String schemaAddress = details.getExtensionBundle().getConfigDir() + "/schema";
        File schemaDirectory = new File(schemaAddress);
        if (schemaDirectory.exists() && schemaDirectory.isDirectory())
        {
            Path schemaSourcePath = Paths.get(schemaAddress);
            try
            {
                DirectoryStream<Path>
                        schemaDirectoryStream = Files.newDirectoryStream(
                        schemaSourcePath, "??-*.ldif");
                for (Path schemaFile : schemaDirectoryStream)
                {
                    try
                    {
                        System.out.println("Copying schema file " + schemaFile.toString());
                        Files.copy(schemaFile, Paths.get(instanceRoot + "/config/schema/" + schemaFile.getFileName())
                                , REPLACE_EXISTING);
                    } catch (IOException e)
                    {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Schema preparation: done.");
    }

    /**
     * This method performs the necessary processing to attempt opening a browser window for the user at
     * installation time and pop the documentation up.
     *
     * @param bundle the extension bundle to get the information from
     */
    private void openBrowser(final ExtensionBundle bundle)
    {
        System.out.println();
        System.out.println("Documentation convenience");
        String docUrl = "file://" + bundle.getDocumentationDir() + "/index.html";
        System.out.println("You may find the documentation for this extension here:");
        System.out.println(docUrl);
        String OS = System.getProperty("os.name").toLowerCase();
        try
        {
            if (OS.contains("mac"))
            {
                Runtime.getRuntime().exec("open " + docUrl);
            } else if (OS.contains("win"))
            {
                Runtime.getRuntime().exec("cmd /c start " + docUrl);
            } else if (OS.contains("nux"))
            {
                Runtime.getRuntime().exec("xdg-open " + docUrl);
            }
        } catch (IOException e)
        {
        }
        System.out.println("Documentation convenience: done.");
    }


    /**
     * This method performs the necessary processing to attempt copying the documentation bundled with the extension
     * into a folder that can be reached by the docs servlet
     *
     * @param instanceRoot     the instance installation root file
     * @param extensionDetails the extension detail information
     */
    private void copyDocumentation(final File instanceRoot,
                                   InstallExtensionDetails extensionDetails)
    {
        System.out.println();
        System.out.println("Documentation preparation");

        String extensionNewDocRoot = instanceRoot + "/docs/extensions/" + extensionDetails.getExtensionBundle()
                .getBundleId();

        File newDocDir = new File(extensionNewDocRoot);
        newDocDir.mkdirs();

        Path srcDocPath = Paths.get(extensionDetails.getExtensionBundle().getDocumentationDir().getPath());
        Path dstDocPath = Paths.get(extensionNewDocRoot);

        TreeCopier tc = new TreeCopier(srcDocPath, dstDocPath);
        try
        {
            Files.walkFileTree(srcDocPath, tc);
        } catch (IOException e)
        {
            System.out.println(e);
        }
        System.out.println("Documentation preparation: done.");
    }

    /**
     * This method performs the necessary processing to attempt updating the default documentation html page to
     * add a
     * direct link to the freshly copied extension documentation.
     * Avoids duplicating links when updating the same extension version, for example during development
     *
     * @param extensionDetails the bundle details
     */
    private void updateIndex(final File syncRoot, InstallExtensionDetails extensionDetails)
    {
        System.out.println();
        System.out.println("Documentation index update");

        Path path = Paths.get(syncRoot.getPath() + "/docs/index.html");
        Charset charset = StandardCharsets.UTF_8;
        String content;
        try
        {
            content = new String(Files.readAllBytes(path), charset);
            String bundleId = extensionDetails.getExtensionBundle().getBundleId();
            if (!content.contains("extensions/" + bundleId))
            {
                content = content.replaceAll("product.", "product.\n<BR><BR><BR><A HREF=\"extensions/" + bundleId +
                        "/\">" + extensionDetails.getExtensionBundle().getTitle() + " " + extensionDetails
                        .getExtensionBundle().getVersion
                                () + "</A>");
                Files.write(path, content.getBytes(charset));
            }
        } catch (IOException e)
        {
            System.out.println(e);
        }
        System.out.println("Documentation index update: done.");
    }

    /**
     * Copy source file to target location. If {@code prompt} is true then
     * prompt user to overwrite target if it exists. The {@code preserve}
     * parameter determines if file attributes should be copied/preserved.
     */
    static void copyFile(Path source, Path target)
    {
        CopyOption[] options = new CopyOption[]{REPLACE_EXISTING};
        if (Files.notExists(target))
        {
            try
            {
                Files.copy(source, target, options);
            } catch (IOException x)
            {
                System.err.format("Unable to copy: %s: %s%n", source, x);
            }
        }
    }

    /**
     * A {@code FileVisitor} that copies a file-tree ("cp -r")
     */
    static class TreeCopier implements FileVisitor<Path>
    {
        private final Path source;
        private final Path target;

        TreeCopier(Path source, Path target)
        {
            this.source = source;
            this.target = target;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        {
            // before visiting entries in a directory we copy the directory
            // (okay if directory already exists).
            CopyOption[] options = new CopyOption[0];

            Path newdir = target.resolve(source.relativize(dir));
            try
            {
                Files.copy(dir, newdir, options);
            } catch (FileAlreadyExistsException x)
            {
                // ignore
            } catch (IOException x)
            {
                System.err.format("Unable to create: %s: %s%n", newdir, x);
                return SKIP_SUBTREE;
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        {
            copyFile(file, target.resolve(source.relativize(file)));
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc)
        {
            // fix up modification time of directory when done
            if (exc == null)
            {
                Path newdir = target.resolve(source.relativize(dir));
                try
                {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newdir, time);
                } catch (IOException x)
                {
                    System.err.format("Unable to copy all attributes to: %s: %s%n", newdir, x);
                }
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
        {
            if (exc instanceof FileSystemLoopException)
            {
                System.err.println("cycle detected: " + file);
            } else
            {
                System.err.format("Unable to copy: %s: %s%n", file, exc);
            }
            return CONTINUE;
        }
    }
}
