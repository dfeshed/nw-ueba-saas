import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render, findAll, click } from '@ember/test-helpers';
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
    assert.expect(5);
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .build();
    await render(hbs`{{host-list/host-table}}`);
    assert.equal(findAll('.rsa-data-table-header-cell').length, 7, 'Total 7 columns are rendered. checkbox + fields');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'Hostname', 'Second column should be hostname');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Risk Score', 'Third column should be Risk Score');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Agent Version', 'fifth column should be Agent version');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Operating System', 'Sixth column should be Operating system');
  });

  test('column chooser do not have default fields', async function(assert) {
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .build();
    await render(hbs`{{host-list/host-table}}`);
    await click('.rsa-icon-cog-filled');
    assert.equal(endpoint.schema.length, 6, '6 columns are passed to the table');
    assert.equal(findAll('.column-chooser-lists li').length, 56, '56 fields present in columns chooser (excluding score and machine name)');
  });
});
