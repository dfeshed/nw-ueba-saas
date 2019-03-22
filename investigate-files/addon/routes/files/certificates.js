import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeCertificateView, bootstrapInvestigateCertificates } from 'investigate-files/actions/certificate-data-creators';

export default Route.extend({
  redux: service(),

  contextualHelp: service(),

  queryParams: {
    /**
     * thumbPrint for selected file
     * @type {string}
     * @public
     */
    thumbprint: {
      refreshModel: false
    }
  },

  model(params) {
    const redux = this.get('redux');
    const { id } = params; // Thumbprint of the selected file
    redux.dispatch(bootstrapInvestigateCertificates());
    redux.dispatch(initializeCertificateView(id));
  },

  deactivate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.invFiles'));
  },

  actions: {
    navigateToCertificateView(thumbprint) {
      this.set('contextualHelp.topic', this.get('contextualHelp.invEndpointCertificates'));
      this.transitionTo({
        queryParams: {
          thumbprint
        }
      });
    }

  }
});
