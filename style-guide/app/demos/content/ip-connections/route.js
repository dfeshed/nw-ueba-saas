import Route from '@ember/routing/route';

export default Route.extend({

  singleIP: ['127.0.0.1'],

  multipleIPs: ['127.0.0.1', '127.0.0.2', '127.0.0.3'],

  model() {
    return {
      'title': 'IP Connections',
      'subtitle': 'Simple component for displaying IP Connections.',
      'description': 'This component supports click to copy, and multiple addresses. An Array is expected for either toIPs and fromIPs in the case of single or multiple connections.',
      'testFilter': 'rsa-content-ip-connections',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-content-ip-connections.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_content-definition.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-content-ip-connections.hbs',
      'singleIP': this.get('singleIP'),
      'multipleIPs': this.get('multipleIPs')
    };
  }

});
