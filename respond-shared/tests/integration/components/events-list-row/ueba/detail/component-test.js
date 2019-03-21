import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { normalizedUebaEventId, getAllEvents, getServices } from '../../../events-list/data';
import * as ueba from '../../ueba/detail/helpers';

module('Integration | Component | events-list-row/ueba/detail', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('expandedId', null);
    this.set('expand', () => {
    });
  });

  test('renders detail for ueba event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === normalizedUebaEventId);

    this.set('expandedId', normalizedUebaEventId);
    this.set('item', item);
    this.set('services', getServices());

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand) services=services}}`);

    ueba.assertDetailColumns(assert, {
      total: 11,
      children: 14
    });

    const sourceElement = ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    const sourceUserElement = ueba.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 1,
      label: 'User',
      value: ''
    });

    ueba.assertDetailRowChild(assert, {
      parentElement: sourceUserElement,
      label: 'Username',
      value: 'ad_qa_1_3'
    });

    ueba.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 2,
      label: 'Device',
      value: ''
    });

    const targetElement = ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    ueba.assertDetailRowChild(assert, {
      parentElement: targetElement,
      label: 'Device',
      value: ''
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
      label: 'Data',
      value: ''
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Created Date',
      value: '2018-11-02T07:30:10.635.000Z'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 5,
      label: 'Event Time',
      value: '2018-10-29T10:59:00.000.000Z'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 6,
      label: 'Schema',
      value: 'ACTIVE_DIRECTORY'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 7,
      label: 'Operation Type',
      value: 'COMPUTER_ACCOUNT_CREATED'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 8,
      label: 'Data Source',
      value: '4741'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 9,
      label: 'Result',
      value: 'FAILURE'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 10,
      label: 'Updated By',
      value: 'hourlyOutputProcessorRun2018-10-29T10:00:00Z'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 11,
      label: 'Updated Date',
      value: '2018-11-02T07:30:10.635.000Z'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 12,
      label: 'Operation Type Categories',
      value: ''
    });

    const scoresElement = ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 13,
      label: 'Scores',
      value: ''
    });

    ueba.assertDetailRowChild(assert, {
      parentElement: scoresElement,
      childRow: 1,
      label: 'Operation Type',
      value: '3'
    });

    ueba.assertDetailRowParent(assert, {
      column: 1,
      row: 14,
      label: 'Additional Info',
      value: ''
    });

  });

});
