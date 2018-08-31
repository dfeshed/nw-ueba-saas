import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | certificates-pager', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('configure')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('footer has rendering', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems(new Array(100))
      .totalCertificates(200)
      .build();
    await render(hbs`{{endpoint/certificates-pager}}`);
    assert.equal(findAll('.certificates-pager .certificates-info').length, 1, 'Footer has rendered.');
    assert.equal(find('.certificates-pager .certificates-info').textContent.trim(), '100 of 200', 'Footer shows certificates number aptly.');
  });
});
