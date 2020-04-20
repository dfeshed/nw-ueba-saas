import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Risk Score',
      'subtitle': 'Risk score with different color based on score',
      'description': 'A risk score will show circle with different color based on the score. There are 4 range for which color will changed.',
      'testFilter': 'rsa-page-layout',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/risk-score.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_rsa-risk-score.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/risk-score.hbs'
    };
  }
});