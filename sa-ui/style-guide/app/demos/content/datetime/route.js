import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Content Datetime',
      'subtitle': 'Component for rendering dates and times that respects format preferences',
      'description': '',
      'testFilter': 'rsa-content-datetime',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-content-datetime.js',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-content-datetime.hbs'
    };
  }

});
