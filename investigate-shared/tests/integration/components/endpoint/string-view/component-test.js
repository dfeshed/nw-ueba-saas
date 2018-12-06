import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/string-view', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('string-view component renders', async function(assert) {
    const fileData = [
      {
        text: 'acpipmi.sys',
        offset: '0x00002c38',
        unicode: true
      },
      {
        text: 'H.data',
        offset: '0x0000022f',
        unicode: false
      },
      {
        text: 'ProductName',
        offset: '0x00002d16',
        unicode: true
      },
      {
        text: 'INFO',
        offset: '0x00002a7c',
        unicode: true
      },
      {
        text: 'InternalName',
        offset: '0x00002c1e',
        unicode: true
      },
      {
        text: 'Rich5',
        offset: '0x000000c8',
        unicode: false
      }
    ];
    this.set('fileData', fileData);
    this.set('searchText', '');
    await render(hbs`{{endpoint/string-view fileData=fileData searchText=searchText}}`);

    assert.equal(findAll('.string-view').length, 1, 'String view component has rendered.');
    assert.equal(findAll('.rsa-data-table-body-row').length, 6, 'String view component has rendered 6 rows.');
    assert.equal(findAll('.rsa-icon-check-2-filled').length, 4, 'rsa-icon-check-2-filled present only for the rows where unicode is true');
    assert.equal(findAll('.unicode-false').length, 2, 'unicode-false present only for the rows where unicode is false');
  });

  test('string-view component renders with filtered data set', async function(assert) {
    const fileData = [
      {
        text: 'acpipmi.sys',
        offset: '0x00002c38',
        unicode: true
      },
      {
        text: 'H.data',
        offset: '0x0000022f',
        unicode: false
      },
      {
        text: 'ProductName',
        offset: '0x00002d16',
        unicode: true
      },
      {
        text: 'INFO',
        offset: '0x00002a7c',
        unicode: true
      },
      {
        text: 'InternalName',
        offset: '0x00002c1e',
        unicode: true
      },
      {
        text: 'Rich5',
        offset: '0x000000c8',
        unicode: false
      }
    ];
    this.set('fileData', fileData);
    this.set('searchText', 'ch');
    await render(hbs`{{endpoint/string-view fileData=fileData searchText=searchText}}`);

    assert.equal(findAll('.rsa-data-table-body-row').length, 1, 'String view component has rendered 1 row as filtered set has one item.');
    assert.equal(findAll('.rsa-icon-check-2-filled').length, 0, 'rsa-icon-check-2-filled not present for the filtered data set as unicode is false');
    assert.equal(findAll('.unicode-false').length, 1, 'unicode-false present for the row as unicode is false');
  });

});
