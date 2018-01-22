import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

import engineResolverFor from '../../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';

import services from '../../../state/autoruns.service';
import hostDetails from '../../../state/overview.hostdetails';

const initState = Immutable.from({
  endpoint: {
    ...services,
    ...hostDetails.linux
  }
});

moduleForComponent('host-detail/autoruns/services', 'Integration | Component | endpoint host-detail/autoruns/services', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('dateFormat');
    this.inject.service('timeFormat');
    this.inject.service('timezone');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');
    applyPatch(initState);
    this.inject.service('redux');
  },
  afterEach() {
    revertPatch();
  }
});

test('it yields service and property data', function(assert) {
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    {{#host-detail/autoruns/services as |output|}}
      <span id='tableItemsLength'>
        {{output.tableItems.length}}
      </span>
      <span id='status'>
        {{output.status}}
      </span>
      <span id='tableItemData'>
        {{#each output.tableItems as |item|}}
          {{item.id}}
        {{/each}}
      </span>
      <span id='propertyData'>
        {{#each-in output.propertyData as |prop data|}}
          {{prop}} {{data}}
        {{/each-in}}
      </span>
    {{/host-detail/autoruns/services}}
  `);

  return wait().then(() => {
    assert.equal(this.$('#tableItemsLength').html().trim(), '6', 'yields table data');
    assert.equal(this.$('#status').html().trim(), 'false', 'yields table status');

    // yank all the ids out and just the id numbers
    const allTableData = this.$('#tableItemData').html().trim().replace(/(systemds_|\n|\s)/g, '');
    assert.equal(allTableData, '563412', 'yields table data');

    const allPropertyData = this.$('#propertyData').html().trim();
    assert.equal((/\/TEST\/DATA\/PATH\/YAY/).test(allPropertyData), false, 'yields property data');
  });
});
