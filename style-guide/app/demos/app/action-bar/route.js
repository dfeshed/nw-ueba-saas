import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Application Action Bar',
      'subtitle': 'Application actions wrapper for use with rsa-application-header, rsa-application-content, and rsa-application-footer',
      'description': 'The Application Action Bar is a single use component that generally lives in the application template of the project. Because of this, it\'s unlikely you will need to work with it directly. However, in the case that you do, an example can be found below. Because this component is absolutely positioned within the document and not needed in this application, we do not demo the component with the code example. However you can observe it\'s behavior in the SA application.',
      'testFilter': 'rsa-application-action-bar',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-application-action-bar.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_application-action-bar.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-application-action-bar.hbs'
    };
  }
});
