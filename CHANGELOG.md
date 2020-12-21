# Change Log
All notable changes to this project will be documented in this file.

## [1.0.16] - 2020/12/21
### Changed
- Archetype: Updated default Server SDK version to 8.2.0.0.
- Archetype: Move frequently used POM properties to the top.

## [1.0.15] - 2020/12/07
### Changed
- General: Updated source code copyrights.
- General: Updated README with details about using the archetype.  
- Archetype: Updated default Java version to Java 8.
- Archetype: Updated default Server SDK version to 8.1.0.0.
- Archetype: Fixed problem with extension bundle manifest when using -EA versions. (Issue #25)
- Docs Maven Plugin: Updated Maven Plugin Plugin version to 3.6.0.

## [1.0.14] - 2017/07/06
### Changed
- General: Updated source code copyrights and hyperlinks; renamed `unboundid.css` to `ping.css`.
- Archetype: Updated Maven Assembly Plugin version to 3.0.0.
- Archetype: Fixed a Maven Assembly Plugin warning that was caused by using both zip and dir output format. The solution is to use zip only, but this can be manually undone, if desired.
- Archetype: Set compiler flag -Xpkginfo:always in the generated POM. This prevents the Server SDK Docs Maven Plugin from emitting a harmless warning about a ClassNotFoundException when it encounters a package-info.java file during builds.
- Docs Maven Plugin: Use the logging facility for Maven plugins instead of printing directly to standard output.

## [1.0.13] - 2017/04/10
### Changed
- Docs Maven Plugin: Don't attempt to create docs for non-public or abstract classes. 

## [1.0.12] - 2017/03/06
### Changed
- General: Added instructions for customizing the contents of the extension bundle.
- Archetype: When packaging the extension bundle, copy the entire `config` directory instead of only `config/update`.

## [1.0.11] - 2017/03/01
### Changed
- Archetype: Set the default Server SDK version to 6.0.1.0.

## [1.0.10] - 2016/10/19
### Fixed
- Archetype: Removed dependency on a snapshot version of the docs plugin.

## [1.0.8] - 2016/10/04
### Changed
- General: This was a test release for an updated build process and contains no changes to functionality.

## [1.0.4] - 2016/10/04
### Added
- General: Updated POMs to produce sources and Javadoc jars.

## [1.0.3] - 2016/10/03
### Changed
- General: Updated Maven coordinates and module names for consistency with the Server SDK.
- Archetype: Updated generated docs to use Ping Identity branding.