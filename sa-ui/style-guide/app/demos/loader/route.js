import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Loader',
      'subtitle': 'RSA loader component examples',
      'description': 'Below is a list of implementation examples for the RSA loader. Please note that while all of the sizes featured below can use the label attribute, only the top four sizes should make use of it.',
      'testFilter': 'rsa-loader',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-loader.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_loader.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-loader.hbs'
    };
  }

});
