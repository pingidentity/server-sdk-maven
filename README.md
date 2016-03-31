# UnboundID Server SDK Maven Helpers

This repository provides two components that can be used to develop and package 
[UnboundID Server SDK](http://blog.arnaudlacour.com/2011/01/introducing-unboundid-server-sdk-future.html) 
extensions using Maven. This is provided as a convenient but unsupported 
alternative to the Server SDK's official Ant-based build mechanisms.

The components are:

| Component | Description |
| --- | --- |
| unboundid-server-sdk-archetype | Generates a Maven project for building extension bundles. |
| unboundid-server-sdk-docs-maven-plugin | Generates an extension bundle's HTML documentation. |

## Note

The UnboundID Server SDK is not currently available on Maven Central; for the 
time being, these components may only be used from the UnboundID internal 
network.

## Usage

Use the archetype to generate a Maven project, providing your own values for 
`groupId` and `artifactId`:

```
mvn archetype:generate -DarchetypeGroupId=com.unboundid.sdk \
  -DarchetypeArtifactId=unboundid-server-sdk-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.example -DartifactId=myNewProject \
  -DinteractiveMode=false
```

The new project will contain an example extension, which you should customize 
or replace. You will also need to customize the generated `pom.xml`.

When you are ready to build an extension bundle, run `mvn package`. 
The extension bundle will be created as a zip in the `target` directory.

## License

This is licensed under the Apache License 2.0.