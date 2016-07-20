import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'RSA Logo',
      'subtitle': 'Animated RSA with optional title',
      'description': 'The rendered logo will expand/contract to fill the available screen space.',
      'testFilter': 'rsa-logo',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-logo.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_logo.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-logo.hbs'
    };
  }

});
