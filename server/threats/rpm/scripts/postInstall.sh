#!/bin/bash

ln -s "${project.serviceLocation}/${project.serviceFile}" "/etc/init.d/${project.serviceName}"
