import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Content Badge Icon',
      'subtitle': 'Simple badge with icon and label',
      'description': '',
      'testFilter': 'rsa-content-badge-icon',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-content-badge-icon.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_content-badge-icon.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-content-badge-icon.hbs'
    };
  }

});
