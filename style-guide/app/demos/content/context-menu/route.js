import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Context Menu',
      'subtitle': 'Right-click activated context menu',
      'description': '',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-context-menu/component.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_content-context-menu.scss'
    };
  }

});
