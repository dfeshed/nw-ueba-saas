import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { waitUntil, settled, find, click, render, triggerKeyEvent } from '@ember/test-helpers';
import { processEventId, normalizedUebaEventId, reEventId, networkEventId, endpointEventId, getAllEvents, getAllAlerts } from '../events-list/data';
import { emptyNetworkEvent, emptyEndpointEvent } from './empty-data';
import * as generic from './helpers/generic';
import * as endpoint from './helpers/endpoint';
import * as ueba from './helpers/ueba';
import * as process from './helpers/process';

const ENTER_KEY = 13;

module('Integration | Component | events-list-row', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    const investigatePageService = this.owner.lookup('service:investigatePage');
    investigatePageService.set('legacyEventsEnabled', true);
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

    const riskScore = find('[test-id=eventsAlertScore]');
    assert.equal(riskScore.getAttribute('focusable'), 'false');

    generic.assertRowPresent(assert);

    generic.assertRowAlertDetails(assert, {
      name: 'country_dst',
      summary: '(Event 1 of 3)',
      score: '70'
    });

    generic.assertRowHeader(assert, {
      eventType: 'Network',
      detectorIp: '127.0.0.1',
      fileName: 'foobarbaz.sh',
      fileHash: '123987def'
    });

    generic.assertRowHeaderContext(assert, {
      detectorIp: '127.0.0.1',
      fileName: 'foobarbaz.sh',
      fileHash: '123987def'
    });

    generic.assertTableColumns(assert);

    generic.assertTableSource(assert, {
      ip: '192.168.100.185',
      port: '123',
      host: 'N/A',
      mac: '00:00:46:8F:F4:20',
      user: 'tbozo'
    });

    generic.assertTableSourceContext(assert, {
      ip: '192.168.100.185',
      mac: '00:00:46:8F:F4:20',
      user: 'tbozo'
    });

    generic.assertTableTarget(assert, {
      ip: '129.6.15.28',
      port: '123',
      host: 'N/A',
      mac: '00:00:00:00:5E:00',
      user: 'xor'
    });

    generic.assertTableTargetContext(assert, {
      ip: '129.6.15.28',
      mac: '00:00:00:00:5E:00',
      user: 'xor'
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
      detectorIp: 'N/A',
      fileName: 'N/A',
      fileHash: 'N/A'
    });

    generic.assertRowHeaderContext(assert, {
      detectorIp: '',
      fileName: '',
      fileHash: ''
    });

    generic.assertTableColumns(assert);

    generic.assertTableSource(assert, {
      ip: '10.4.61.97',
      port: '36749',
      host: 'N/A',
      mac: '00:50:56:33:18:18',
      user: 'N/A'
    });

    generic.assertTableSourceContext(assert, {
      ip: '10.4.61.97',
      mac: '00:50:56:33:18:18',
      user: ''
    });

    generic.assertTableTarget(assert, {
      ip: '10.4.61.44',
      port: '5671',
      host: 'N/A',
      mac: '00:50:56:33:18:15',
      user: 'N/A'
    });

    generic.assertTableTargetContext(assert, {
      ip: '10.4.61.44',
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
      eventType: 'Endpoint',
      category: 'Process Event',
      action: 'createProcess',
      hostname: 'INENMENONS4L2C',
      userAccount: 'foobar',
      operatingSystem: 'windows',
      hash: 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa'
    });

    endpoint.assertRowHeaderContext(assert, {
      hostname: 'INENMENONS4L2C',
      userAccount: 'foobar',
      hash: 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa'
    });

    endpoint.assertTableColumns(assert);

    endpoint.assertTableSource(assert, {
      fileName: 'dtf.exe',
      launch: 'dtf.exe  -dll:ioc.dll -testcase:353',
      path: 'C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\',
      hash: '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6'
    });

    endpoint.assertTableSourceContext(assert, {
      fileName: 'dtf.exe',
      hash: '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6'
    });

    endpoint.assertTableTarget(assert, {
      fileName: 'cmd.EXE',
      launch: 'cmd.EXE /C COPY /Y C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\dtf.exe C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\MSHTA.EXE',
      path: 'C:\\WINDOWS\\System32\\',
      hash: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9'
    });

    endpoint.assertTableTargetContext(assert, {
      fileName: 'cmd.EXE',
      hash: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9'
    });
  });

  test('renders ueba row for ueba event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === normalizedUebaEventId);

    this.set('item', item);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    ueba.assertRowPresent(assert);

    ueba.assertRowAlertDetails(assert, {
      name: 'abnormal_object_change_operation',
      summary: '(Event 2 of 4)',
      score: '4'
    });

    ueba.assertRowHeader(assert, {
      eventType: 'UEBA',
      category: 'ACTIVE_DIRECTORY',
      username: 'ad_qa_1_3',
      operationType: 'COMPUTER_ACCOUNT_CREATED',
      eventCode: '4741',
      result: 'FAILURE'
    });

    ueba.assertRowHeaderContext(assert, {
      username: 'ad_qa_1_3'
    });
  });

  test('renders process row for ueba process event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === processEventId);

    this.set('item', item);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    process.assertRowPresent(assert);

    process.assertRowAlertDetails(assert, {
      name: 'abnormal_object_change_operation',
      summary: '(Event 3 of 4)',
      score: '4'
    });

    process.assertRowHeader(assert, {
      eventType: 'UEBA',
      category: 'PROCESS',
      username: 'proc_qa_1_3',
      operationType: 'CREATE_PROCESS',
      dataSource: 'Netwitness Endpoint',
      result: ''
    });

    process.assertRowHeaderContext(assert, {
      username: 'proc_qa_1_3'
    });

    process.assertTableColumns(assert);

    process.assertTableSource(assert, {
      fileName: 'macmnsvc.exe',
      checksum: '120EA8A25E5D487BF68B5F7096440019',
      directory: 'C:\\Program Files\\McAfee\\Agent',
      username: 'proc_qa_1_3',
      categories: 'N/A'
    });

    process.assertTableTarget(assert, {
      fileName: 'TOOL_17.exe',
      checksum: 'CE114E4501D2F4E2DCEA3E17B546F339',
      directory: 'C:\\Windows\\System32',
      username: 'N/A',
      categories: 'RECONNAISSANCE_TOOL'
    });

    process.assertTableSourceContext(assert, {
      fileName: 'macmnsvc.exe',
      checksum: '120EA8A25E5D487BF68B5F7096440019',
      username: 'proc_qa_1_3'
    });

    process.assertTableTargetContext(assert, {
      fileName: 'TOOL_17.exe',
      checksum: 'CE114E4501D2F4E2DCEA3E17B546F339',
      username: ''
    });

  });

  test('event summary renders correctly with invalid eventIndex value', async function(assert) {
    const events = getAllEvents();
    const [ original ] = events.filter((e) => e.id === normalizedUebaEventId);

    const item = {
      ...original,
      eventIndex: '0'
    };

    this.set('item', item);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    const score = '4';
    const summary = '(Event 1 of 4)';
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
      summary: '(Event 9 of 4)',
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
    assert.equal(riskScore, null, 'Alert score is unavailable as alerts data is not passed or does not match');

    const trigger = find(triggerSelector);
    const detailsId = `${guid}-row-details`;
    assert.equal(trigger.tagName, 'DIV');
    assert.equal(trigger.getAttribute('role'), 'button');
    assert.equal(trigger.getAttribute('tabIndex'), '0');
    assert.equal(trigger.getAttribute('aria-controls'), detailsId);
    assert.equal(trigger.getAttribute('aria-expanded'), 'false');
    assert.equal(trigger.getAttribute('aria-pressed'), 'false');
    assert.notOk(find(rowSelector).classList.contains('expanded'));

    const details = find(`[id='${detailsId}']`);
    assert.equal(details.getAttribute('tabIndex'), '-1');
    assert.equal(details.getAttribute('aria-hidden'), 'true');
    assert.equal(details.getAttribute('hidden'), '');
    assert.equal(details.querySelector('.sr-only').textContent, 'Event Details');

    await click(childSelector);

    await waitUntil(() => details.getAttribute('aria-hidden') === 'false', { timeout: 5000 });
    await settled();

    assert.equal(trigger.getAttribute('aria-expanded'), 'true');
    assert.equal(trigger.getAttribute('aria-pressed'), 'true');
    assert.equal(details.getAttribute('tabIndex'), '0');
    assert.equal(details.getAttribute('aria-hidden'), 'false');
    assert.equal(details.getAttribute('hidden'), null);
    assert.ok(find(rowSelector).classList.contains('expanded'));
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

  test('renders N/A for each endpoint header & footer value when event is empty', async function(assert) {
    this.set('item', emptyEndpointEvent);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    const riskScore = find('[test-id=eventsAlertScore]');
    assert.equal(riskScore.getAttribute('focusable'), 'false');

    endpoint.assertRowPresent(assert);

    endpoint.assertRowAlertDetails(assert, {
      name: 'Unsigned Open Process and Runs Command Shell',
      summary: '(Event 1 of 8)',
      score: '50'
    });

    endpoint.assertRowHeader(assert, {
      eventType: 'N/A',
      category: 'N/A',
      action: 'N/A',
      hostname: 'N/A',
      userAccount: 'N/A',
      operatingSystem: 'N/A',
      hash: 'N/A'
    });

    endpoint.assertRowHeaderContext(assert, {
      hostname: '',
      userAccount: '',
      hash: ''
    });

    endpoint.assertTableColumns(assert);

    endpoint.assertTableSource(assert, {
      fileName: 'N/A',
      launch: 'N/A',
      path: 'N/A',
      hash: 'N/A'
    });

    endpoint.assertTableSourceContext(assert, {
      fileName: '',
      hash: ''
    });

    endpoint.assertTableTarget(assert, {
      fileName: 'N/A',
      launch: 'N/A',
      path: 'N/A',
      hash: 'N/A'
    });

    endpoint.assertTableTargetContext(assert, {
      fileName: '',
      hash: ''
    });
  });

  test('renders N/A for each generic header & footer value when event is empty', async function(assert) {
    this.set('item', emptyNetworkEvent);
    this.set('alerts', getAllAlerts());

    await render(hbs`{{events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertRowPresent(assert);

    generic.assertRowAlertDetails(assert, {
      name: 'test',
      summary: '(Event 1 of 1)',
      score: '90'
    });

    generic.assertRowHeader(assert, {
      eventType: 'N/A',
      detectorIp: 'N/A',
      fileName: 'N/A',
      fileHash: 'N/A'
    });

    generic.assertRowHeaderContext(assert, {
      detectorIp: '',
      fileName: '',
      fileHash: ''
    });

    generic.assertTableColumns(assert);

    generic.assertTableSource(assert, {
      ip: 'N/A',
      port: 'N/A',
      host: 'N/A',
      mac: 'N/A',
      user: 'N/A'
    });

    generic.assertTableSourceContext(assert, {
      ip: '',
      mac: '',
      user: ''
    });

    generic.assertTableTarget(assert, {
      ip: 'N/A',
      port: 'N/A',
      host: 'N/A',
      mac: 'N/A',
      user: 'N/A'
    });

    generic.assertTableTargetContext(assert, {
      ip: '',
      mac: '',
      user: ''
    });
  });
});
