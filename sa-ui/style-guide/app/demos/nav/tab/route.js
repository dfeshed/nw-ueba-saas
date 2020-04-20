import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Navigation Tab',
      'subtitle': 'Tab style navigation.',
      'testFilter': 'rsa-nav-tab',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-nav-tab.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/nav/_nav-tab.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-nav-tab.hbs'
    };
  }

});
