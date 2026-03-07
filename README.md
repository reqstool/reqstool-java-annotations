[![Commit Activity](https://img.shields.io/github/commit-activity/m/reqstool/reqstool-java-annotations?label=commits&style=for-the-badge)](https://github.com/reqstool/reqstool-java-annotations/pulse)
[![GitHub Issues](https://img.shields.io/github/issues/reqstool/reqstool-java-annotations?style=for-the-badge&logo=github)](https://github.com/reqstool/reqstool-java-annotations/issues)
[![License](https://img.shields.io/github/license/reqstool/reqstool-java-annotations?style=for-the-badge&logo=opensourceinitiative)](https://opensource.org/license/mit/)
[![Build](https://img.shields.io/github/actions/workflow/status/reqstool/reqstool-java-annotations/build.yml?style=for-the-badge&logo=github)](https://github.com/reqstool/reqstool-java-annotations/actions/workflows/build.yml)
[![Documentation](https://img.shields.io/badge/Documentation-blue?style=for-the-badge&link=docs)](https://reqstool.github.io)

# Reqstool Java Annotations

Java annotations for [reqstool](https://github.com/reqstool/reqstool-client) requirements traceability.

## Overview

Provides `@Requirements` and `@SVCs` annotations for linking Java code to requirements and software verification cases. Used together with the [Maven Plugin](https://github.com/reqstool/reqstool-java-maven-plugin) or [Gradle Plugin](https://github.com/reqstool/reqstool-java-gradle-plugin).

## Installation

Add the dependency to your project:

### Maven

```xml
<dependency>
    <groupId>io.github.reqstool</groupId>
    <artifactId>reqstool-java-annotations</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.reqstool:reqstool-java-annotations:1.0.0'
```

## Usage

```java
import io.github.reqstool.annotations.Requirements;
import io.github.reqstool.annotations.SVCs;

@Requirements({"REQ_001", "REQ_002"})
public class MyService {
    // Implementation
}

@Test
@SVCs("SVC_001")
public void testMyService() {
    // Test
}
```

## Documentation

Full documentation can be found [here](https://reqstool.github.io).

## Contributing

See the organization-wide [CONTRIBUTING.md](https://github.com/reqstool/.github/blob/main/CONTRIBUTING.md).

## License

MIT License.
