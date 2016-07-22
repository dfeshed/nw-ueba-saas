import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Content Tooltips',
      'subtitle': 'Quick popup tooltips',
      'description': '',
      'testFilter': 'rsa-content-tooltip',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-content-tooltip.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_content-tooltip.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-content-tooltip.hbs'
    };
  }

});
