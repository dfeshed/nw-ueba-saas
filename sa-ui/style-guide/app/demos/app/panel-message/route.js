import Route from '@ember/routing/route';

export default Route.extend({
  model() {
    return {
      'title': 'RSA Panel Message',
      'subtitle': 'Component and Styles for displaying messages in a panel.',
      'description': 'Component and Styles for displaying messages in a panel.  This component will display text in the center and middle of the panel by default.',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-panel-message/component.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/application/_application-panel-message.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-panel-message/template.hbs'
    };
  }
});