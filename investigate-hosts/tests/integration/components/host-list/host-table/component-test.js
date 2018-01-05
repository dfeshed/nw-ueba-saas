import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import endpoint from '../../state/schema';

let initState;
moduleForComponent('host-list/host-table', 'Integration | Component | endpoint host-list/host table', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    initState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});
test('it renders data table with column sorted by name', function(assert) {
  new ReduxDataHelper(initState)
    .columns(endpoint.schema)
    .build();
  this.render(hbs`{{host-list/host-table}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-cell:eq(1)').text().trim(), 'Host Name', 'Second column should be host name');
    assert.equal(this.$('.rsa-data-table-header-cell:eq(3)').text().trim(), 'Agent Version', 'Fourth column should be Agent Version');
    assert.equal(this.$('.rsa-data-table-header-cell:eq(5)').text().trim(), 'Operating System', 'Fourth column should be Operating System');
  });
});
