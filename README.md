#Presidio Core

##Introduction

Presidio is an embedded by security infrastructure solutions to deliver the visibility and risk-based analysis they need to make better, smarter security decisions. 
Leveraging the engine from user and entity behavioral analytics (UEBA) platform, Presidio enables SIEM, EDR/EPP, DLP, CASB, IAM and other security infrastructure products to quickly and simply incorporate risk-based intelligence on the activity of users and entities within the customer’s environment to enhance their own analysis and security enforcement.

Presidio core is extended by [presidio-netwitness](https://github.rsa.lab.emc.com/asoc/presidio-netwitness)


##Modules

1) Fortscale
    
    This module has the the REST Apis and batch applications (java).
    
2) Presidio workflows

   This module has the python packages that defines a set of airflow DAGs

3) Presidio workflows extension

   Dummy module to be overriden by extension
   
4) Package

   This module contains has the rpm's pom.xml files

##Build
####Jenkins on [Bedford-tier2](https://rsabwlabauth.corp.emc.com:900/):

* Pull request triggered java UT jobs: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-core-pr/

* Pull request triggered python UT jobs: TODO

* Manually triggered java UT jobs: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-core/

* Manually triggered python UT jobs: TODO

* Package building job: https://asoc-esa-jenkins.rsa.lab.emc.com/view/UEBA/job/presidio-core-packages/

####Integration tests jenkins on [Herzelia-tier2](https://rsahzlabauth.corp.emc.com:900/)

TODO

##Installation

See package [readme file](/package/README.md)
