
## Deployment

### 1. Git

- Start the agent
```bash
eval "$(ssh-agent -s)"
```

- Check key is available 
```bash
ssh-add -l
```

- Add the key (if needed)
```
cd ~/.ssh
ssh-add githubff4j
```

### 2. Update versions and dependencies

- Check for dependency updates and update the `pom.xml` file when relevant.

```bash
mvn versions:display-dependency-update
```

- Update project version
```bash
mvn versions:set -DnewVersion=0.4
```

- When ok commit
```bash
mvn versions:commit
```

- Make sure everything is in sync
```
find . -type f -name *.DS_Store -ls -delete
git pull
git add -A
git commit -m "Preparing for deployment"
```

### 3. Build and Tests

- Generate Jacoco report and push to Coverall and get results at [Coveralls.io](https://coveralls.io/github/datastax/astra-cli)

```bash
mvn clean test jacoco:report coveralls:report
```
 
- Generate Sonar report and push to sonar cloud and get results at [SonarCloud.io](https://sonarcloud.io/summary/overall?id=clun_astra-cli)

```bash
export SONAR_TOKEN=...
mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=clun_astra-cli
```

### 4. Packaging

- Install proper java and graal VM if needed

```bash
sdk install java 22.3.1.r17-grl
java --version
```

- Build project

```
mvn clean install
```

- Check `reflect-config.json` and `resource-config.json` are updated with all classes and commands

```json
{
    "name":"com.dtsx.astra.cli.org.OrgIdCmd",
    "allDeclaredFields":true,
    "methods":[{"name":"<init>","parameterTypes":[] }]
}
```
- Build project a native image

```
mvn clean install -Pnative
```

- Chmod everything execution files

```bash
chmod 755 src/main/dist/astra-init.sh
```

- Copy `dist/astra-init.sh` (_generated by a test)_ on each distribution folder


- Copy the shaded and put it in Java folder, rename to `astra-cli.jar`


- Packaging

```
export ASTRA_CLI_VERSION=0.6
tar -czf astra-cli-0.6-x86_64_linux.bottle.tar.gz *
zip astra-cli-$ASTRA_CLI_VERSION-linux.zip *
```

```
zip astra-cli-0.6.zip *
zip astra-cli-0.6-linux.zip *
zip astra-cli-0.6-mac.zip *
tar -czf astra-cli-0.6-homebrew.tar.gz *
brew create https://github.com/datastax/astra-cli/releases/download/0.6/astra-cli-0.6-homebrew.tar.gz
```

- Edit Releases

```
https://github.com/datastax/astra-cli/releases
```

### Homebrew

```
export ASTRA_CLI_VERSION=0.5
tar -czf astra-cli-$ASTRA_CLI_VERSION.x86_64_linux.bottle.tar.gz *
shasum -a 256 astra-cli-$ASTRA_CLI_VERSION.x86_64_linux.bottle.tar.gz
zip astra-cli-$ASTRA_CLI_VERSION-linux.zip *
```

```
```


