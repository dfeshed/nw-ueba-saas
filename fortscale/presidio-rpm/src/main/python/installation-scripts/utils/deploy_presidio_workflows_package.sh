#!/usr/bin/env bash
export PYTHONPATH=/usr/local/lib/python2.7/site-packages
chown presidio:presidio -R /usr/local/lib/python2.7/site-packages/
cd /home/presidio/presidio-core/packages/
/usr/local/bin/easy_install -U --install-dir=/usr/local/lib/python2.7/site-packages/ presidio_workflows_extension*
/usr/local/bin/easy_install -U --install-dir=/usr/local/lib/python2.7/site-packages/ presidio_workflows-*
chown presidio:presidio -R /usr/local/lib/python2.7/site-packages/