import { module, test } from 'qunit';
import Service from '@ember/service';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { settled } from '@ember/test-helpers';
import CertificatesRoute from 'investigate-files/routes/files/certificates';
import { computed } from '@ember/object';
import CertificateCreators from 'investigate-files/actions/certificate-data-creators';
import sinon from 'sinon';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let redux;


module('Unit | Route | certificates.index', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const setupRoute = function() {
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'investigate-files/certificates'
    }));

    redux = this.owner.lookup('service:redux');

    const PatchedRoute = CertificatesRoute.extend({
      redux: computed(function() {
        return redux;
      })
    });
    return PatchedRoute.create();
  };

  test('model hook should call initializeCertificateView', async function(assert) {
    assert.expect(2);
    const mock1 = sinon.stub(CertificateCreators, 'bootstrapInvestigateCertificates');
    const mock2 = sinon.stub(CertificateCreators, 'initializeCertificateView');
    patchReducer(this, Immutable.from({}));
    const route = setupRoute.call(this);
    await route.model({ thumbporint: 'abcd' });
    await settled();

    assert.ok(mock1.callCount === 1, 'bootstrapInvestigateCertificates method is called');
    assert.ok(mock2.callCount === 1, 'initializeCertificateView method is called');
  });
});
