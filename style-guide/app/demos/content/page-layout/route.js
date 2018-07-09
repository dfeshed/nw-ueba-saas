import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Page Layout',
      'subtitle': 'Page Layout with left, center and right zone',
      'description': 'A page layout has 3 sections, the left, center, right zone. Left and right zone have headers. Custom header can also be set. Open and close on the left/right panel can be done through an action',
      'testFilter': 'rsa-page-layout',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-page-layout.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/rsa-page-layout/_manifest.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-page-layout.hbs'
    };
  }
});