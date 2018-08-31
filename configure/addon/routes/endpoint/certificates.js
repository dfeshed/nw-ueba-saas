import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { getCertificates } from 'configure/actions/creators/endpoint/certificates-creator';

export default Route.extend({
  redux: inject(),

  model() {
    const redux = this.get('redux');
    redux.dispatch(getCertificates());
  }
});
