FROM asoc-dtr2.rsa.lab.emc.com/rsa/base
COPY repos/* /etc/yum.repos.d/
COPY *.rpm /tmp
RUN yum -y install openssl
RUN yum -y --disablerepo=* --enablerepo=nginx install nginx-1.12.1-1.el7.ngx.x86_64
ARG RPM_FILE
RUN rpm -ivh /tmp/$RPM_FILE

COPY cert/* /etc/nginx/
COPY conf/nginx.conf /etc/nginx/nginx.conf
COPY conf/saui.conf /etc/nginx/conf.d/saui.conf

# forward request logs to Docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log
RUN ln -sf /dev/stderr /var/log/nginx/error.log

EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]
