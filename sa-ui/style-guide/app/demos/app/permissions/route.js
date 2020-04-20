import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Permissions and Access Control',
      'subtitle': 'A service for describing what functionality the user has access to.',
      'description': 'Permissions are fetched when accessing the protected route of the application. Once fetched, the roles property of this service will be populated will all available permissions, and getters for specific features will be updated.',
      'testFilter': 'access-control',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/services/access-control.js'
    };
  }

});
