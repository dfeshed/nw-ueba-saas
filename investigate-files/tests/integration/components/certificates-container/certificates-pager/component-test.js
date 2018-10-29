import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let initState;

module('Integration | Component | certificates-container/certificate-pager', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.dateFormat = this.owner.lookup('service:dateFormat');
    this.timeFormat = this.owner.lookup('service:timeFormat');
    this.timezone = this.owner.lookup('service:timezone');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('footer has rendering', async function(assert) {
    new ReduxDataHelper(initState)
      .certificatesItems(new Array(100))
      .totalCertificates(200)
      .build();
    await render(hbs`{{certificates-container/certificates-pager}}`);
    assert.equal(findAll('.certificates-pager .certificates-info').length, 1, 'Footer has rendered.');
    assert.equal(find('.certificates-info').textContent.trim(), 'Showing 100 of 200 | 0 selected', 'Footer shows certificates number aptly.');
  });
});
