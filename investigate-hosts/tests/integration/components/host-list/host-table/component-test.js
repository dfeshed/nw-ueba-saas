import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import endpoint from '../../state/schema';

let initState;

module('Integration | Component | host-list/host-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders data table with column sorted by name', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .build();
    await render(hbs`{{host-list/host-table}}`);
    assert.equal(find('.rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'Hostname', 'Second column should be hostname');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Risk Score', 'Third column should be Risk Score');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Agent Version', 'Fourth column should be Agent Version');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(8)').textContent.trim(), 'Operating System', 'Sixth column should be Operating System');
  });
});
