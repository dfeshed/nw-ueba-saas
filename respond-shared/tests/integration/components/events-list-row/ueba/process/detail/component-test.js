import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { processEventId, getAllEvents, getServices } from '../../../../events-list/data';
import * as process from '../../../ueba/process/detail/helpers';

module('Integration | Component | events-list-row/ueba/process/detail', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('expandedId', null);
    this.set('expand', () => {
    });
  });

  test('renders detail for ueba process event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === processEventId);

    this.set('expandedId', processEventId);
    this.set('item', item);
    this.set('services', getServices());

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand) services=services}}`);

    process.assertDetailColumns(assert, {
      total: 10,
      children: 13
    });

    const sourceElement = process.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    process.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      label: 'Process Directory',
      value: 'C:\\Program Files\\McAfee\\Agent'
    });

    process.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 2,
      label: 'Process Certificate Issuer',
      value: 'Entrust'
    });

    const sourceUserElement = process.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 3,
      label: 'User',
      value: ''
    });

    process.assertDetailRowChild(assert, {
      parentElement: sourceUserElement,
      label: 'Username',
      value: 'proc_qa_1_3'
    });

    /*
    process.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 4,
      label: 'Device',
      value: ''
    });
    */

    const targetElement = process.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    process.assertDetailRowChild(assert, {
      parentElement: targetElement,
      label: 'Process Directory',
      value: 'C:\\Windows\\System32'
    });

    process.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 2,
      label: 'Process Categories',
      value: 'RECONNAISSANCE_TOOL'
    });

    process.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 3,
      label: 'Device',
      value: ''
    });

    process.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 4,
      label: 'Process File Name',
      value: 'TOOL_17.exe'
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
      label: 'Data',
      value: ''
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Schema',
      value: 'PROCESS'
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 5,
      label: 'Scores',
      value: ''
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 6,
      label: 'Updated Date',
      value: '2018-11-02T07:30:12.100.000Z'
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 7,
      label: 'Machine Name',
      value: 'host_3'
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 8,
      label: 'Event Time',
      value: '2018-10-29T10:17:00.000.000Z'
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 9,
      label: 'Additional Info',
      value: ''
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 10,
      label: 'Updated By',
      value: 'hourlyOutputProcessorRun2018-10-29T10:00:00Z'
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 11,
      label: 'Created Date',
      value: '2018-11-02T07:30:12.100.000Z'
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 12,
      label: 'Operation Type',
      value: 'CREATE_PROCESS'
    });

    process.assertDetailRowParent(assert, {
      column: 1,
      row: 13,
      label: 'Data Source',
      value: 'Netwitness Endpoint'
    });

  });

});
