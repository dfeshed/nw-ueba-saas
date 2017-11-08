import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';

import endpoint from '../../state/hostlist.filter';

const initState = Immutable.from({
  endpoint: {
    filter: {
      expresionList: [],
      lastFilterAdded: null,
      schemas: endpoint.schema
    }
  }
});

moduleForComponent('host-list/content-filter', 'Integration | Component | endpoint host-list/content-filter', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    applyPatch(initState);
    this.inject.service('redux');
  },
  afterEach() {
    revertPatch();
  }
});

test('host-list content-filter renders default filters', function(assert) {

  // set height to get all lazy rendered items on the page
  this.render(hbs`{{host-list/content-filter}}`);

  return wait().then(() => {
    const textFilters = this.$('.content-filter').find('.text-filter');
    assert.equal(textFilters.length, 2);
  });
});
