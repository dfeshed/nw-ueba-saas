ARG DTR=asoc-dtr2.rsa.lab.emc.com
ARG NGINX_VERSION=1.12.1-1


FROM ${DTR}/3rd/nginx:${NGINX_VERSION}-latest
# RPM file name with full path
COPY *.rpm /tmp
RUN yum clean all
ARG RPM_FILE_NAME
RUN rpm -ivh /tmp/$RPM_FILE_NAME

EXPOSE 80 443
RUN ln -sf /dev/stdout /var/log/nginx/access.log
RUN ln -sf /dev/stderr /var/log/nginx/error.log
CMD ["nginx", "-g", "daemon off;"]
