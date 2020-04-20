import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Panels',
      'subtitle': 'Styles for static and slide in panels.',
      'description': 'A panel has four distinct sections, the header label, controls, actions, and the panel content. Panels can be implemented as a static content wrapper, or by utilizing the layoutManager service which allows the panel to transition in and out of screen.',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/application/_application-panels.scss'
    };
  }

});
