import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Content Accordion',
      'subtitle': 'Simple accordion component',
      'description': '',
      'testFilter': 'rsa-content-accordion',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-content-accordion.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_content-accordion.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-content-accordion.hbs'
    };
  }

});
