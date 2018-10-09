import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { endpointEventId, getAllEvents } from '../../../events-list/data';
import * as endpoint from '../../generic/detail/helpers';

module('Integration | Component | events-list-row/endpoint/detail', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('expandedId', null);
    this.set('expand', () => {
    });
  });

  test('renders detail for endpoint event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === endpointEventId);

    this.set('expandedId', endpointEventId);
    this.set('item', item);

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    endpoint.assertDetailColumns(assert, {
      total: 4,
      children: 10
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 1,
      label: 'Domain/Host',
      value: 'INENMENONS4L2C'
    });

    const detectorRowElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Detector',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: detectorRowElement,
      label: 'Product Name',
      value: 'nwendpoint'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 3,
      label: 'Size',
      value: '41'
    });

    const dataRowElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Data',
      value: ''
    });

    const dataRowChildOneElement = endpoint.assertDetailRowChild(assert, {
      parentElement: dataRowElement,
      label: '',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: dataRowChildOneElement,
      label: 'Size',
      value: '41'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 5,
      label: 'Agent ID',
      value: 'C593263F-E2AB-9168-EFA4-C683E066A035'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 6,
      label: 'Device Type',
      value: 'nwendpoint'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 7,
      label: 'Event Source',
      value: '10.63.0.117:56005'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 8,
      label: 'Event Source ID',
      value: '857775'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 9,
      label: 'User',
      value: 'CORP\\menons4'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 10,
      label: 'User Src',
      value: 'CORP\\menons4'
    });
  });

});
