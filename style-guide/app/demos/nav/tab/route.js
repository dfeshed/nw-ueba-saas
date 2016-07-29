import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Navigation Tab',
      'subtitle': 'Tab style navigation.',
      'testFilter': 'rsa-nav-tab',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-nav-tab.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_nav-tab.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-nav-tab.hbs'
    };
  }

});
