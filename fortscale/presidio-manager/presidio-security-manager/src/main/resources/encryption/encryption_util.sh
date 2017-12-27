#!/bin/bash

echo $1  |  openssl enc -d -base64   | openssl enc -d -aes-256-cbc  -salt -pass pass:mj23
