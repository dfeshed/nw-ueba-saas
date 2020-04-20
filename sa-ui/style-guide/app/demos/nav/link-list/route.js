import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Nav Link List',
      'subtitle': 'A simple list of links with a title',
      'description': '',
      'testFilter': 'rsa-nav-link-list',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-nav-link-list.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/nav/_nav-link-list.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-nav-link-list.hbs'
    };
  }

});
