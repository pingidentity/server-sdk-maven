[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.unboundid/server-sdk-maven-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.unboundid/server-sdk-maven-parent) [![Build Status](https://travis-ci.org/pingidentity/server-sdk-maven.svg?branch=master)](https://travis-ci.org/pingidentity/server-sdk-maven) [![Javadocs](https://www.javadoc.io/badge/com.unboundid/server-sdk-maven-parent.svg)](https://www.javadoc.io/doc/com.unboundid/server-sdk-maven-parent)
# UnboundID Server SDK Maven Helpers

The UnboundID Server SDK is a set of APIs for developing extensions to the
PingData server products, including PingDirectory and PingDataGovernance.

This repository provides components that can be used to develop and package 
[UnboundID Server SDK](https://docs.ping.directory/server-sdk/latest/) 
extensions as Maven projects. The project is provided as a convenient but 
unsupported alternative to the Server SDK's official Ant-based build mechanisms.

The included components are:

| Component | Description |
| --- | --- |
| server-sdk-archetype | Generates a Maven project for building extension bundles. |
| server-sdk-docs-maven-plugin | Generates an extension bundle's HTML documentation. |

Typically, only the archetype needs to be used directly; the project that it
generates will use the docs plugin automatically.

## Usage

[![asciicast](https://asciinema.org/a/105217.png)](https://asciinema.org/a/105217)

### 1. Generate a project using the archetype

Use the archetype to generate a Maven project, providing your own values for 
`groupId` and `artifactId`:

```
mvn archetype:generate -DarchetypeGroupId=com.unboundid \
  -DarchetypeArtifactId=server-sdk-archetype \
  -DarchetypeVersion=1.0.14 \
  -DgroupId=com.example -DartifactId=my-extension \
  -DinteractiveMode=false
```

### 2. Implement the Server SDK extension

The new project will contain an example extension class in `src/main/java`. 
At a minimum, you will need to customize or replace this class, as well 
as the generated `pom.xml`.

If you'd like other assets to appear in the extension bundle, place 
them in a subdirectory of `src/main/assembly` and configure `assembly.xml` 
to include the subdirectory (documentation 
[here](http://maven.apache.org/plugins/maven-assembly-plugin/)). Any 
files that you place in `src/main/assembly/config` will be automatically 
copied over.

```
.
├── pom.xml # Extension metadata and extensions go here.
└── src
    └── main
        ├── assembly # Custom files may be placed here; configure with assembly.xml.
        │   ├── assembly.xml # Determines the contents of the extension bundle.
        │   ├── config 
        │   │   └── update
        │   │       └── file-directives.properties # Governs how files are replaced when updating.
        │   └── docs
        │       ├── images
        │       │   ├── favicon.ico
        │       │   └── vendor-name-on-white.png
        │       └── ping.css
        ├── java
        │   └── com
        │       └── example
        │           └── MyExampleExtension.java # Replace this with your extension.
        └── resources
            ├── javadoc
            │   └── ping-javadoc-stylesheet.css
            └── velocity
                ├── extension.html.vm
                └── index.html.vm
```

### 3. Build the extension bundle

When you are ready to build an extension bundle, run `mvn package`. 
The extension bundle will be created as a zip in the `target` directory.

### 4. Install the extension bundle

Install the extension bundle using the 
[manage-extension](https://docs.ping.directory/PingDirectory/latest/cli/manage-extension.html) 
tool.

```bash
$ bin/manage-extension --install com.example.my-extension-1.0-SNAPSHOT.zip
```

After installing the extension bundle, you'll need to configure the PingData 
server to use it. The necessary configuration varies depending on the specifics 
of your extension.

## Developer notes

The following notes may be useful to developers making changes to this project. 
If you're a user of this project (i.e., an extension developer), you can ignore 
this section.

### Using a local copy

If you've made changes locally that you wish to test, don't forget to 
install the local copy:

```
mvn install
```

When generating a project from your local archetype, you need to tell 
Maven to use the local archetype catalog with the `archetypeCatalog` option:

```
mvn archetype:generate -DarchetypeGroupId=com.unboundid \
  -DarchetypeArtifactId=server-sdk-archetype \
  -DarchetypeVersion=1.0.15-SNAPSHOT \
  -DgroupId=com.example -DartifactId=my-extension \
  -DinteractiveMode=false -DarchetypeCatalog=local
```

### Automatic changes to server-sdk-archetype/pom.xml

When you build the project, the [Maven Replacer Plugin](https://github.com/beiliubei/maven-replacer-plugin) 
will perform a substitution on a `${server-sdk-docs-maven-plugin.version}` token 
in the `server-sdk-archetype/pom.xml` file. This allows the server-sdk-docs-maven-plugin 
version to be set correctly in the archetype POM. You should not commit this 
change to `server-sdk-archetype/pom.xml`. Please see the comment in `project.build.plugins` 
for more details.  

### Updating the Server SDK version

To update the Server SDK version to correspond to a new server release, 
you must make two changes.

The `server-sdk.version` property in the [parent POM](./pom.xml) 
determines the Server SDK version used by the server-sdk-docs-maven-plugin 
and server-sdk-archetype components. It does _not_ affect the Server SDK 
version used by projects generated from the archetype. That change should 
be made to the `server-sdk.version` property in the [archetype POM](./server-sdk-archetype/pom.xml).

## License

This is licensed under the Apache License 2.0.
