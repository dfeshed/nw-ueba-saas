# presidio-test-utils

## Introduction

presidio test utililities for used for data generators.

# dependency
```xml            
<dependency>
                <groupId>presidio-test-utils</groupId>
                <artifactId>presidio-data-generators</artifactId>
                <version>${presidio-data-generators.version}</version>
                <scope>test</scope>
</dependency>
```

## Build
#### Jenkins on [Bedford-tier2](https://rsabwlabauth.corp.emc.com:900/):
* on push to master tirggered java UT & deploy jobs: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-test-utils-master/

* Pull request triggered java UT jobs: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-test-utils-pr/

* Manually triggered java UT jobs: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-test-utils/

## Artifacts
* Java artifacts are available at artifactory:
  * http://repo1.rsa.lab.emc.com:8081/artifactory/webapp/#/artifacts/browse/tree/General/asoc-snapshots
  * http://repo1.rsa.lab.emc.com:8081/artifactory/webapp/#/artifacts/browse/tree/General/asoc-releases
