# Building TG-KITPVP

This guide will help you compile the TG-KITPVP plugin for your Paper Minecraft server.

## Prerequisites

Before building, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
   - Verify installation: `java -version`

2. **Apache Maven 3.6 or higher**
   - Download from [Maven website](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`

3. **Git** (optional, for cloning)
   - Download from [Git website](https://git-scm.com/downloads)

## Building Steps

### 1. Get the Source Code

If you have the files in a folder, navigate to that folder in your terminal/command prompt.

### 2. Build the Plugin

Open a terminal/command prompt in the project directory (where `pom.xml` is located) and run:

```bash
mvn clean package
```

This command will:
- Download all dependencies (Paper API, Vault API)
- Compile the Java source code
- Package everything into a JAR file
- Run any tests (if present)

### 3. Locate the Output

After successful build, you'll find the compiled plugin at:
```
target/TG-KITPVP-1.0.0.jar
```

### 4. Install on Server

Copy `TG-KITPVP-1.0.0.jar` to your Paper server's `plugins/` folder and restart the server.

## Build Options

### Clean Build (recommended)
```bash
mvn clean package
```
Removes old compiled files before building.

### Skip Tests
```bash
mvn clean package -DskipTests
```
Faster build if you don't need to run tests.

### Install to Local Maven Repository
```bash
mvn clean install
```
Installs the plugin to your local Maven repository for use in other projects.

## IDE Setup

### IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select "Open" and choose the project folder
3. IntelliJ will automatically detect the Maven project
4. Wait for dependencies to download
5. To build: Go to Maven panel (right side) → Lifecycle → package

### Eclipse
1. Open Eclipse
2. File → Import → Maven → Existing Maven Projects
3. Select the project folder
4. Wait for dependencies to download
5. To build: Right-click project → Run As → Maven build → Goals: "clean package"

### VS Code
1. Open VS Code
2. Install "Extension Pack for Java" and "Maven for Java" extensions
3. Open the project folder
4. Wait for dependencies to download
5. To build: Open Command Palette (Ctrl+Shift+P) → "Maven: Execute commands" → "package"

## Troubleshooting

### "Java version not supported"
Ensure you're using JDK 17 or higher. Check with `java -version`.

### "mvn command not found"
Maven is not installed or not in your system PATH. Install Maven and add it to PATH.

### "Could not resolve dependencies"
- Check your internet connection (Maven needs to download dependencies)
- Try running `mvn clean` first
- Delete `.m2/repository` folder and rebuild

### Build fails with compilation errors
- Ensure all source files are present in `src/main/java/`
- Check that `pom.xml` is not modified incorrectly
- Verify Java compiler version in `pom.xml` matches your JDK

### Plugin doesn't load on server
- Verify you're using Paper 1.20+ (not Spigot/Bukkit/CraftBukkit)
- Check server logs for specific error messages
- Ensure the JAR file is not corrupted (try rebuilding)

## Development Workflow

For active development:

1. Make code changes
2. Run `mvn clean package` to compile
3. Copy new JAR to server's `plugins/` folder
4. Reload the plugin with a plugin manager or restart server
5. Test changes in-game

### Hot Reload (Development)
Some plugin managers like PlugMan allow hot-reloading without server restart:
```
/plugman unload TG-KITPVP
/plugman load TG-KITPVP
```

## Version Updates

To change the plugin version:

1. Edit `pom.xml`:
   ```xml
   <version>1.1.0</version>
   ```

2. Rebuild with `mvn clean package`

3. The output JAR will be named `TG-KITPVP-1.1.0.jar`

## Additional Resources

- [Paper API Documentation](https://docs.papermc.io/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Vault API](https://github.com/MilkBowl/VaultAPI)
- [Spigot Plugin Development](https://www.spigotmc.org/wiki/spigot-plugin-development/)

## Support

If you encounter issues during building:
1. Check console output for error messages
2. Verify all prerequisites are installed correctly
3. Ensure you're in the correct directory (with `pom.xml`)
4. Try deleting `target/` folder and rebuilding
