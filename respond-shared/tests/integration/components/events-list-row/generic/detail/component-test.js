import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { run } from '@ember/runloop';
import { set } from '@ember/object';
import { settled, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { reEventId, networkEventId, getAllEvents } from '../../../events-list/data';
import * as generic from './helpers';

module('Integration | Component | events-list-row/generic/detail', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('expandedId', null);
    this.set('expand', () => {
    });
  });

  test('renders detail for network event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === networkEventId);

    this.set('expandedId', networkEventId);
    this.set('item', item);

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertDetailColumns(assert, {
      total: 12,
      children: 9
    });

    const sourceRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    const sourceRowChildOneElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceRowElement,
      subRowIndex: 1,
      label: 'Device',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 1,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 2,
      label: 'IP Address',
      value: '10.4.61.97',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 3,
      label: 'MAC Address',
      value: '00:50:56:33:18:18',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 4,
      label: 'Port',
      value: '36749',
      metaKey: 'port'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowElement,
      subRowIndex: 2,
      label: 'User',
      value: ''
    });

    const destRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    const destRowChildOneElement = generic.assertDetailRowChild(assert, {
      parentElement: destRowElement,
      subRowIndex: 1,
      label: 'Device',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 1,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 2,
      label: 'IP Address',
      value: '10.4.61.44',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 3,
      label: 'MAC Address',
      value: '00:50:56:33:18:15',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 4,
      label: 'Port',
      value: '5671',
      metaKey: 'port'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowElement,
      subRowIndex: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 3,
      label: 'Detector',
      value: '',
      nestedColumns: 1
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 4,
      label: 'Size',
      value: '4175'
    });

    const dataRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 5,
      label: 'Data',
      value: ''
    });

    const dataRowChildOneElement = generic.assertDetailRowChild(assert, {
      parentElement: dataRowElement,
      subRowIndex: 1,
      label: '',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataRowChildOneElement,
      subRowIndex: 1,
      label: 'Size',
      value: '4175',
      metaKey: 'size'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 6,
      label: 'Analysis Service',
      value: 'ssl over non-standard port'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 7,
      label: 'Analysis Session',
      value: 'ratio medium transmitted'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 8,
      label: 'Event Source',
      value: '10.4.61.33:56005'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 9,
      label: 'Event Source ID',
      value: '150'
    });
  });

  test('event labels properly update when locale is changed', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === networkEventId);

    this.set('expandedId', networkEventId);
    this.set('item', item);

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    const detectorInGerman = 'Detektor';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'de-de', { 'respond.eventDetails.labels.detector': detectorInGerman });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 3,
      label: 'Detector',
      value: '',
      nestedColumns: 1
    });

    set(i18n, 'locale', 'de-de');

    return settled().then(async () => {
      generic.assertDetailRow(assert, {
        column: 1,
        row: 3,
        label: detectorInGerman,
        value: '',
        nestedColumns: 1
      });
    });
  });

  test('renders detail for reporting engine event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === reEventId);

    this.set('expandedId', reEventId);
    this.set('item', item);

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertDetailColumns(assert, {
      total: 12,
      children: 9
    });

    const sourceRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    const sourceRowChildOneElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceRowElement,
      subRowIndex: 1,
      label: 'Device',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 1,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 2,
      label: 'IP Address',
      value: '192.168.100.185',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 3,
      label: 'MAC Address',
      value: '00:00:46:8F:F4:20',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 4,
      label: 'Port',
      value: '123',
      metaKey: 'port'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowElement,
      subRowIndex: 2,
      label: 'User',
      value: ''
    });

    const destRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    const destRowChildOneElement = generic.assertDetailRowChild(assert, {
      parentElement: destRowElement,
      subRowIndex: 1,
      label: 'Device',
      value: ''
    });

    const destRowChildTwoElement = generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 1,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 1,
      label: 'City',
      value: 'Gaithersburg',
      metaKey: 'city'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 2,
      label: 'Country',
      value: 'United States',
      metaKey: 'country'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 3,
      label: 'Domain/Host',
      value: 'nist.gov',
      metaKey: 'domain'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 4,
      label: 'Latitude',
      value: '39',
      metaKey: 'latitude'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 5,
      label: 'Longitude',
      value: '-77',
      metaKey: 'longitude'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 6,
      label: 'Organization',
      value: 'National Bureau of Standards',
      metaKey: 'organization'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 2,
      label: 'IP Address',
      value: '129.6.15.28',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 3,
      label: 'MAC Address',
      value: '00:00:00:00:5E:00',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 4,
      label: 'Port',
      value: '123',
      metaKey: 'port'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowElement,
      subRowIndex: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 3,
      label: 'Domain/Host',
      value: 'zap'
    });

    const detectorRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Detector',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorRowElement,
      subRowIndex: 1,
      label: 'IP Address',
      value: '127.0.0.1'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 5,
      label: 'Size',
      value: '180'
    });

    const dataRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 6,
      label: 'Data',
      value: ''
    });

    const dataRowChildOneElement = generic.assertDetailRowChild(assert, {
      parentElement: dataRowElement,
      subRowIndex: 1,
      label: '',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataRowChildOneElement,
      subRowIndex: 1,
      label: 'Filename',
      value: 'foobarbaz.sh',
      metaKey: 'filename'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataRowChildOneElement,
      subRowIndex: 2,
      label: 'Hash',
      value: '123987def',
      metaKey: 'hash'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataRowChildOneElement,
      subRowIndex: 3,
      label: 'Size',
      value: '180',
      metaKey: 'size'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 7,
      label: 'Target Domain',
      value: 'nist.gov'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 8,
      label: 'Event Source',
      value: '10.25.51.157:50105'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 9,
      label: 'Event Source ID',
      value: '47560522'
    });
  });
});
