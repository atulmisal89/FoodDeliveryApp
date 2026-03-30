# Maven Build Failure - SSL Certificate Fix

## Problem
Maven build failing with SSL certificate validation error:
```
PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: 
unable to find valid certification path to requested target
```

## Root Cause
Maven cannot verify SSL certificates when downloading dependencies from the central repository.

---

## Solution 1: Use Maven Settings File (Recommended)

A `.mvn/settings.xml` file has been created with alternative repositories.

### Option A: Use Aliyun Mirror (Fastest)
```bash
mvn clean install -s .mvn/settings.xml
```

### Option B: Skip SSL Verification (Not Recommended for Production)
```bash
mvn clean install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true
```

---

## Solution 2: Update Global Maven Settings

Edit your Maven `settings.xml` file (usually at `~/.m2/settings.xml`):

```xml
<mirrors>
    <mirror>
        <id>central-mirror</id>
        <mirrorOf>central</mirrorOf>
        <name>Aliyun Central Repository</name>
        <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
</mirrors>

<repositories>
    <repository>
        <id>central</id>
        <name>Central Repository</name>
        <url>https://maven.aliyun.com/repository/central</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
</repositories>
```

---

## Solution 3: Update pom.xml with Repositories

Add to parent `pom.xml`:

```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Central Repository</name>
        <url>https://maven.aliyun.com/repository/central</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
    
    <repository>
        <id>spring</id>
        <name>Spring Repository</name>
        <url>https://repo.spring.io/release</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
</repositories>

<pluginRepositories>
    <pluginRepository>
        <id>central</id>
        <name>Central Plugin Repository</name>
        <url>https://maven.aliyun.com/repository/central</url>
        <releases>
            <enabled>true</enabled>
        </releases>
    </pluginRepository>
</pluginRepositories>
```

---

## Solution 4: Clear Maven Cache and Retry

```bash
# Clear Maven cache
mvn clean

# Remove .m2 repository cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install
```

---

## Solution 5: Use Different Maven Repository

### Option A: Maven Central (HTTP)
```bash
mvn clean install -Drepository.url=http://repo1.maven.org/maven2
```

### Option B: JCenter Repository
```bash
mvn clean install -Drepository.url=https://jcenter.bintray.com
```

---

## Recommended Build Commands

### Using Project Settings File
```bash
mvn clean install -s .mvn/settings.xml -DskipTests
```

### Using Global Settings
```bash
mvn clean install -DskipTests
```

### With Verbose Output
```bash
mvn clean install -s .mvn/settings.xml -X
```

---

## Troubleshooting

### If Still Getting SSL Errors

1. **Check Java Certificate Store**
   ```bash
   # Verify Java is properly installed
   java -version
   
   # Check if certificates are installed
   keytool -list -v -keystore $JAVA_HOME/lib/security/cacerts
   ```

2. **Update Java Certificates**
   - Download latest JDK with updated certificates
   - Or manually import certificates

3. **Check Network Connection**
   ```bash
   # Test connectivity to Maven repository
   curl -v https://maven.aliyun.com/repository/central
   ```

4. **Check Proxy Settings**
   - If behind corporate proxy, configure proxy in `.mvn/settings.xml`

---

## Alternative Repositories

| Repository | URL | Speed | Reliability |
|------------|-----|-------|-------------|
| Aliyun | https://maven.aliyun.com/repository/central | Fast | High |
| Spring | https://repo.spring.io/release | Medium | High |
| Maven Central | https://repo.maven.apache.org/maven2 | Medium | High |
| JCenter | https://jcenter.bintray.com | Medium | Medium |

---

## Build Commands to Try (In Order)

```bash
# 1. Try with project settings file
mvn clean install -s .mvn/settings.xml -DskipTests

# 2. If that fails, try skipping SSL verification
mvn clean install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -DskipTests

# 3. If that fails, clear cache and retry
mvn clean
rm -rf ~/.m2/repository
mvn clean install -s .mvn/settings.xml -DskipTests

# 4. If still failing, try without parent POM repackage
mvn clean install -DskipTests -pl user-service,restaurant-service,order-service,payment-service,delivery-service,notification-service,api-gateway
```

---

## Success Indicators

When build succeeds, you'll see:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
[INFO] Finished at: YYYY-MM-DD HH:MM:SS
```

---

## Next Steps After Successful Build

1. Start infrastructure (MySQL, Kafka, Eureka)
2. Run services using `mvn spring-boot:run` in each module
3. Access API Gateway at http://localhost:8080/swagger-ui.html

---

**Created**: March 30, 2026
**Status**: Ready to resolve SSL certificate issues
