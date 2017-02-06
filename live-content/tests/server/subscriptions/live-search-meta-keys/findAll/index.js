const data = ['threat.category', 'threat.desc', 'threat.source', 'asn.dst', 'asn.src', 'alert.id', 'crypto',
  'risk.warning', 'action', 'alias.host', 'error', 'password', 'server', 'tcp.dstport', 'tunnel.ip.dst', 'username',
  'content', 'ficonstype', 'risk.suspicious', 'ssl.ca', 'ssl.subject', 'alias.ip', 'message', 'buddy', 'filename',
  'cc.number', 'risk.info', 'query', 'client', 'email.url.host', 'attachment', 'email', 'fullname', 'subject', 'version',
  'language', 'directory', 'extension', 'proxy.ip.dst', 'proxy.ip.src', 'referer', 'group', 'alert', 'org', 'OS',
  'search.text', 'tld', 'user.input', 'vis.level', 'orig_ip', 'browser', 'dns.querytype', 'dns.responsetype',
  'ein.number', 'formdata.element', 'alias.ipv6', 'eth.dst.vendor', 'eth.src.vendor', 'query.element'];

export default {
  subscriptionDestination: '/cms/search/get-resource-meta-keys',
  requestDestination: '/ws/cms/search/get-resource-meta-keys',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};


