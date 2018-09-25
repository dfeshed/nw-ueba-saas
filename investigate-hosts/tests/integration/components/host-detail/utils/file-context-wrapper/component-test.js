import { module, test, setupRenderingTest } from 'ember-qunit';

import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';

const config = [
  {
    'dataType': 'checkbox',
    'width': 20,
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox'
  },
  {
    field: 'fileName',
    title: 'File Name'
  },
  {
    field: 'timeModified',
    title: 'LAST MODIFIED TIME',
    format: 'DATE'
  },
  {
    field: 'signature.features',
    title: 'Signature',
    format: 'SIGNATURE'
  }
];

module('Integration | Component | host-detail/utils/file-context-wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.set('storeName', 'fileContextDrivers');
    this.set('tabName', 'DRIVER');
    this.set('columnConfig', config);
  });


  test('it should render the action bar, table and property panel', async function(assert) {
    this.set('propertyConfig', [{}]);
    await render(hbs`{{host-detail/utils/file-context-wrapper propertyConfig=propertyConfig storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    assert.equal(findAll('.rsa-data-table').length, 1, 'Table is rendered');
    assert.equal(findAll('.host-property-panel').length, 1, 'Property panel rendered');
    assert.equal(findAll('.file-actionbar').length, 1, 'action bar rendered');
  });

  test('property panel not rendered', async function(assert) {
    await render(hbs`{{host-detail/utils/file-context-wrapper storeName=storeName tabName=tabName columnsConfig=columnConfig}}`);
    assert.equal(findAll('.rsa-data-table').length, 1, 'Table is rendered');
    assert.equal(findAll('.host-property-panel').length, 0, 'No property panel');
    assert.equal(findAll('.file-actionbar').length, 1, 'action bar rendered');

  });
});
