export default {
  subscriptionDestination: '/user/queue/content/parser/highlight',
  requestDestination: '/ws/content/parser/highlight',
  message(/* frame */) {
    return {
      code: 0,
      data: 'date=2017-08-12 type=traffic subtype=violation user=matt status=deny src=192.168.24.49 dst=192.56.43.56 dstdomain=com sent=0 src_port=4135 dst_port=443 smac=06-02-00-00-00-00 dmac=06-02-00-00-00-01 src_int=internal dst_int=external group=SSO_Guest_Users proto=6 rcvd=583\n\n' +
      'May 5 2018 15:55:49 switch : %ACE-4-4000: IDS:1000 IP Option Bad Option user: admin@test.com from 10.100.229.59 port 12345.\n\n' +
      '%IIS-4-440: 2017-08-12 13:53:34 192.170.28.192 - W4S31 url=https://test.domain.edu/exchange GET /exchweb/bin/auth/owalogon.asp 440\n\n' +
      'Dec 20 13:20:20 instance1 info mod=mail from=matt@rsa.com to=alex@dell.com\n\n'
    };
  }
};
