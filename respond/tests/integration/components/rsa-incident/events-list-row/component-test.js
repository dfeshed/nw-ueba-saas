import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, click, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { storyLineEvents, reEventId, networkEventId, endpointEventId } from '../events-list/data';
import { storyDatasheet, getAlertsWithIndicatorId } from 'respond/selectors/storyline';
import * as generic from './helpers/generic';
import * as endpoint from './helpers/endpoint';

module('Integration | Component | events-list-row', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    patchReducer(this, Immutable.from(storyLineEvents));
    this.set('expandedId', null);
    this.set('expand', () => {
    });
  });

  test('renders generic row for reporting engine event', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const alerts = getAlertsWithIndicatorId(redux.getState());
    const [ item ] = events.filter((e) => e.id === reEventId);

    this.set('item', item);
    this.set('alerts', alerts);

    await render(hbs`{{rsa-incident/events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

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
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const alerts = getAlertsWithIndicatorId(redux.getState());
    const [ item ] = events.filter((e) => e.id === networkEventId);

    this.set('item', item);
    this.set('alerts', alerts);

    await render(hbs`{{rsa-incident/events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

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
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const alerts = getAlertsWithIndicatorId(redux.getState());
    const [ item ] = events.filter((e) => e.id === endpointEventId);

    this.set('item', item);
    this.set('alerts', alerts);

    await render(hbs`{{rsa-incident/events-list-row alerts=alerts item=item expandedId=expandedId expand=(action expand)}}`);

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

  test('each element in the row has the correct aria attributes', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const [ item ] = events.filter((e) => e.id === endpointEventId);

    this.set('item', item);
    this.set('expand', (id) => this.set('expandedId', id));

    await render(hbs`{{rsa-incident/events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    const rowSelector = '[test-id=eventsListRow]';
    const triggerSelector = '[test-id=eventRowTrigger]';
    const childSelector = '[test-id=endpointEventMain]';

    const row = find(rowSelector);
    const guid = row.getAttribute('id');

    const trigger = find(triggerSelector);
    const detailsId = `${guid}-row-details`;
    assert.equal(trigger.tagName, 'BUTTON');
    assert.equal(trigger.getAttribute('aria-controls'), detailsId);
    assert.equal(trigger.getAttribute('aria-expanded'), 'false');

    const details = find(`[id='${detailsId}']`);
    assert.equal(details.getAttribute('tabIndex'), '0');
    assert.equal(details.getAttribute('aria-hidden'), 'true');
    assert.equal(details.getAttribute('hidden'), '');
    assert.equal(details.querySelector('.sr-only').textContent, 'Event Details');

    await click(childSelector);

    assert.equal(trigger.getAttribute('aria-expanded'), 'true');
    assert.equal(details.getAttribute('aria-hidden'), 'false');
    assert.equal(details.getAttribute('hidden'), null);
    assert.equal(document.activeElement.id, trigger.getAttribute('id'));
  });
});
