import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Tethered panels',
      'subtitle': 'Quick popup panels',
      'description': '',
      'testFilter': 'rsa-content-tethered-panel',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-content-tethered-panel.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_content-tethered-panel.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-content-tethered-panel.hbs'
    };
  }

});
