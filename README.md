# Presidio UI - Read Me


Presidio UI is the backend and the forntend dashboard of Presido-NW UEBA.

This project is the main utils for the analysts to investigate the alerts and anomalies in the system.

To build the project execute the following:
`build.sh` `build.bat` - build the whole project, include tests
`build-production.sh` `build-production.bat` - build the whole projcts + minify the web resources.
`build-skip-ui-compilation.sh` `build-skip-ui-compilation.bat` - build the java and copy the web resources from presidio-ui-ui but not compiling presidio-ui-ui, save a lot of time, recommended to use when you only make changes in the java code.


Spring profiles:

`mock-data` - load all the alerts, events & indicators from mock data, does not require elastic search and not presidio-output.

`mock-conf` - supply alternative configuration rather of using spring config server

`mock-mongo` - work with Fongo, instead of require mongo.

`mock-authentication`  - will work with any access_token and not validate the roles and integrity of the token.

URL: <http://localhost:8883/index.html>

## Build
#### Jenkins on [Bedford-tier2](https://rsabwlabauth.corp.emc.com:900/):

* Pull request triggered java UT jobs: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-ui-pr/

* Manually triggered java UT jobs: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-ui/

* Package building job: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-ui-packages/


## Installation

See package [readme file](/package/README.md)



