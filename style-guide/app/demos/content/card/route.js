import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Content Card',
      'subtitle': 'Card for summarizing and possibly prioritizing complex data',
      'description': '',
      'testFilter': 'rsa-content-card',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-content-card.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_content-card.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-content-card.hbs'
    };
  }

});
