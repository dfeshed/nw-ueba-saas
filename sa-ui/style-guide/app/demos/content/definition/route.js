import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Content Definition',
      'subtitle': 'Simple data and title structure',
      'description': '',
      'testFilter': 'rsa-content-definition',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-content-definition.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_content-definition.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-content-definition.hbs'
    };
  }

});
