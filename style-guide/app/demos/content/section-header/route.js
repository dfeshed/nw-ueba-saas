import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Content Section Header',
      'subtitle': 'Simple header for a section of related data',
      'description': '',
      'testFilter': 'rsa-content-section-header',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-content-section-header.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_content-section-header.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-content-section-header.hbs'
    };
  }

});
