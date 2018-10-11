import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, click, render, triggerKeyEvent } from '@ember/test-helpers';
import { uebaEventId, reEventId, networkEventId, endpointEventId, getAllEvents, getAllAlerts } from '../events-list/data';
import * as generic from './helpers/generic';
import * as endpoint from './helpers/endpoint';

const ENTER_KEY = 13;

module('Integration | Component | events-list-row', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('expandedId', null);
    this.set('expand', () => {
    });
  });

  test('renders generic row for reporting engine event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === reEventId);

    this.set('item', item);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertRowPresent(assert);

    generic.assertRowAlertDetails(assert, {
      name: 'country_dst',
      summary: '(Event 1 of 3)',
      score: '70'
    });

    generic.assertRowHeader(assert, {
      eventType: 'Network',
      detectorIp: '',
      fileName: '',
      fileHash: ''
    });

    generic.assertTableColumns(assert);

    generic.assertTableSource(assert, {
      ip: '192.168.100.185',
      port: '123',
      host: '',
      mac: '00:00:46:8F:F4:20',
      user: ''
    });

    generic.assertTableTarget(assert, {
      ip: '129.6.15.28',
      port: '123',
      host: '',
      mac: '00:00:00:00:5E:00',
      user: ''
    });
  });

  test('renders generic row for network event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === networkEventId);

    this.set('item', item);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertRowPresent(assert);

    generic.assertRowAlertDetails(assert, {
      name: 'test',
      summary: '(Event 1 of 1)',
      score: '90'
    });

    generic.assertRowHeader(assert, {
      eventType: 'Network',
      detectorIp: '',
      fileName: '',
      fileHash: ''
    });

    generic.assertTableColumns(assert);

    generic.assertTableSource(assert, {
      ip: '10.4.61.97',
      port: '36749',
      host: '',
      mac: '00:50:56:33:18:18',
      user: ''
    });

    generic.assertTableTarget(assert, {
      ip: '10.4.61.44',
      port: '5671',
      host: '',
      mac: '00:50:56:33:18:15',
      user: ''
    });
  });

  test('renders endpoint row for endpoint event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === endpointEventId);

    this.set('item', item);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    endpoint.assertRowPresent(assert);

    endpoint.assertRowAlertDetails(assert, {
      name: 'Unsigned Open Process and Runs Command Shell',
      summary: '(Event 1 of 8)',
      score: '50'
    });

    endpoint.assertRowHeader(assert, {
      eventType: 'Log',
      category: 'Process Event',
      action: 'createProcess',
      hostname: 'INENMENONS4L2C',
      userAccount: 'foobar',
      operatingSystem: 'macOS',
      fileHash: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9'
    });

    endpoint.assertTableColumns(assert);

    endpoint.assertTableSource(assert, {
      fileName: 'dtf.exe',
      launch: 'dtf.exe  -dll:ioc.dll -testcase:353',
      path: '/foo/bar',
      hash: '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6'
    });

    endpoint.assertTableTarget(assert, {
      fileName: 'cmd.EXE',
      launch: 'PowerShell.exe --run',
      path: '/bar/baz',
      hash: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9'
    });
  });

  test('renders generic row for ueba event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === uebaEventId);

    this.set('item', item);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertRowPresent(assert);

    generic.assertRowAlertDetails(assert, {
      name: 'abnormal_object_change_operation',
      summary: '(Event 2 of 2)',
      score: '4'
    });

    generic.assertRowHeader(assert, {
      eventType: '',
      detectorIp: '',
      fileName: '',
      fileHash: ''
    });

    generic.assertTableColumns(assert);

    generic.assertTableSource(assert, {
      ip: '',
      port: '',
      host: '',
      mac: '',
      user: ''
    });

    generic.assertTableTarget(assert, {
      ip: '',
      port: '',
      host: '',
      mac: '',
      user: ''
    });
  });

  test('event summary renders correctly with invalid eventIndex value', async function(assert) {
    const events = getAllEvents();
    const [ original ] = events.filter((e) => e.id === uebaEventId);

    const item = {
      ...original,
      eventIndex: '0'
    };

    this.set('item', item);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    const score = '4';
    const summary = '(Event 1 of 2)';
    const name = 'abnormal_object_change_operation';
    generic.assertRowAlertDetails(assert, { name, summary, score });

    this.set('item', { ...original, eventIndex: null });
    generic.assertRowAlertDetails(assert, { name, summary, score });

    this.set('item', { ...original, eventIndex: undefined });
    generic.assertRowAlertDetails(assert, { name, summary, score });

    this.set('item', { ...original, eventIndex: NaN });
    generic.assertRowAlertDetails(assert, { name, summary, score });

    this.set('item', { ...original, eventIndex: [] });
    generic.assertRowAlertDetails(assert, { name, summary, score });

    this.set('item', { ...original, eventIndex: [{ id: 1 }] });
    generic.assertRowAlertDetails(assert, { name, summary, score });

    this.set('item', { ...original, eventIndex: 'yolo' });
    generic.assertRowAlertDetails(assert, { name, summary, score });

    this.set('item', { ...original, eventIndex: '08' });
    generic.assertRowAlertDetails(assert, {
      name,
      summary: '(Event 9 of 2)',
      score
    });
  });

  test('each element in the row has the correct aria attributes', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === endpointEventId);

    this.set('item', item);
    this.set('expand', (id) => this.set('expandedId', id));

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    const rowSelector = '[test-id=eventsListRow]';
    const triggerSelector = '[test-id=eventRowTrigger]';
    const childSelector = '[test-id=endpointEventHeader]';

    const row = find(rowSelector);
    const guid = row.getAttribute('id');

    // IE11 will focus on the svg without this attribute
    const riskScore = find('[test-id=eventsAlertScore]');
    assert.equal(riskScore.getAttribute('focusable'), 'false');

    const trigger = find(triggerSelector);
    const detailsId = `${guid}-row-details`;
    assert.equal(trigger.tagName, 'DIV');
    assert.equal(trigger.getAttribute('role'), 'button');
    assert.equal(trigger.getAttribute('tabIndex'), '0');
    assert.equal(trigger.getAttribute('aria-controls'), detailsId);
    assert.equal(trigger.getAttribute('aria-expanded'), 'false');
    assert.equal(trigger.getAttribute('aria-pressed'), 'false');

    const details = find(`[id='${detailsId}']`);
    assert.equal(details.getAttribute('tabIndex'), '-1');
    assert.equal(details.getAttribute('aria-hidden'), 'true');
    assert.equal(details.getAttribute('hidden'), '');
    assert.equal(details.querySelector('.sr-only').textContent, 'Event Details');

    await click(childSelector);

    assert.equal(trigger.getAttribute('aria-expanded'), 'true');
    assert.equal(trigger.getAttribute('aria-pressed'), 'true');
    assert.equal(details.getAttribute('tabIndex'), '0');
    assert.equal(details.getAttribute('aria-hidden'), 'false');
    assert.equal(details.getAttribute('hidden'), null);
  });

  test('keyDown will also toggle the event row to show details', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === endpointEventId);

    this.set('item', item);
    this.set('expand', (id) => this.set('expandedId', id));

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    const triggerSelector = '[test-id=eventRowTrigger]';
    const childSelector = '[test-id=endpointEventHeader]';

    const trigger = find(triggerSelector);
    assert.equal(trigger.getAttribute('aria-expanded'), 'false');

    await triggerKeyEvent(childSelector, 'keydown', ENTER_KEY);

    assert.equal(trigger.getAttribute('aria-expanded'), 'true');
  });
});
