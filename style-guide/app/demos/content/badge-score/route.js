import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Content Badge Score',
      'subtitle': 'Simple badge with score and label',
      'description': '',
      'testFilter': 'rsa-content-badge-score',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-content-badge-score.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_content-badge-score.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-content-badge-score.hbs'
    };
  }

});
