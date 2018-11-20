import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { run } from '@ember/runloop';
import { set } from '@ember/object';
import { settled, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { emptyNetworkEvent } from '../../empty-data';
import { reRelatedLinkOne, networkRelatedLinkOne, reEventId, ecatEventId, networkEventId, getAllEvents } from '../../../events-list/data';
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
      total: 13,
      children: 9
    });

    const sourceElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    const sourceDeviceElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      label: 'Device',
      value: ''
    });

    const sourceGeolocationElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceGeolocationElement,
      label: '',
      value: 'N/A'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      childRow: 2,
      label: 'IP Address',
      value: '10.4.61.97',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      childRow: 3,
      label: 'MAC Address',
      value: '00:50:56:33:18:18',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      childRow: 4,
      label: 'Port',
      value: '36749',
      metaKey: 'port'
    });

    const sourceUserElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceUserElement,
      label: '',
      value: 'N/A'
    });

    const targetElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    const targetDeviceElement = generic.assertDetailRowChild(assert, {
      parentElement: targetElement,
      label: 'Device',
      value: ''
    });

    const targetGeolocationElement = generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      label: '',
      value: 'N/A'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      childRow: 2,
      label: 'IP Address',
      value: '10.4.61.44',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      childRow: 3,
      label: 'MAC Address',
      value: '00:50:56:33:18:15',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      childRow: 4,
      label: 'Port',
      value: '5671',
      metaKey: 'port'
    });

    const targetUserElement = generic.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetUserElement,
      label: '',
      value: 'N/A'
    });

    const detectorElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
      label: 'Detector',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorElement,
      label: '',
      value: 'N/A'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 4,
      label: 'Size',
      value: '4175'
    });

    const dataElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 5,
      label: 'Data',
      value: ''
    });

    const dataEmptyElement = generic.assertDetailRowChild(assert, {
      parentElement: dataElement,
      label: '',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
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

    generic.assertRelatedLinks(assert, {
      column: 2,
      row: 1,
      values: [
        'Investigate Original Event'
      ],
      urls: [
        networkRelatedLinkOne
      ]
    });
  });

  test('event labels properly update when locale is changed', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === networkEventId);

    this.set('expandedId', networkEventId);
    this.set('item', item);

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    const detectorInGerman = 'Detektor';
    const naInGerman = 'nee';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'de-de', { 'respond.eventDetails.labels.detector': detectorInGerman });
    run(i18n, 'addTranslations', 'de-de', { 'respond.eventsList.na': naInGerman });

    const detectorElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
      label: 'Detector',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorElement,
      label: '',
      value: 'N/A'
    });

    set(i18n, 'locale', 'de-de');

    return settled().then(async () => {
      const detectorElement = generic.assertDetailRowParent(assert, {
        column: 1,
        row: 3,
        label: detectorInGerman,
        value: ''
      });

      generic.assertDetailRowChild(assert, {
        parentElement: detectorElement,
        label: '',
        value: naInGerman
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
      total: 13,
      children: 9
    });

    const sourceElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    const sourceDeviceElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      label: 'Device',
      value: ''
    });

    const sourceGeolocationElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceGeolocationElement,
      label: '',
      value: 'N/A'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      childRow: 2,
      label: 'IP Address',
      value: '192.168.100.185',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      childRow: 3,
      label: 'MAC Address',
      value: '00:00:46:8F:F4:20',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      childRow: 4,
      label: 'Port',
      value: '123',
      metaKey: 'port'
    });

    const sourceUserElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceUserElement,
      label: 'Username',
      value: 'tbozo'
    });

    const targetElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    const targetDeviceElement = generic.assertDetailRowChild(assert, {
      parentElement: targetElement,
      label: 'Device',
      value: ''
    });

    const targetGeolocationElement = generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      childRow: 1,
      label: 'City',
      value: 'Gaithersburg',
      metaKey: 'city'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      childRow: 2,
      label: 'Country',
      value: 'United States',
      metaKey: 'country'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      childRow: 3,
      label: 'Domain/Host',
      value: 'nist.gov',
      metaKey: 'domain'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      childRow: 4,
      label: 'Latitude',
      value: '39',
      metaKey: 'latitude'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      childRow: 5,
      label: 'Longitude',
      value: '-77',
      metaKey: 'longitude'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      childRow: 6,
      label: 'Organization',
      value: 'National Bureau of Standards',
      metaKey: 'organization'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      childRow: 2,
      label: 'IP Address',
      value: '129.6.15.28',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      childRow: 3,
      label: 'MAC Address',
      value: '00:00:00:00:5E:00',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      childRow: 4,
      label: 'Port',
      value: '123',
      metaKey: 'port'
    });

    const targetUserElement = generic.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetUserElement,
      label: 'Username',
      value: 'xor'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 3,
      label: 'Domain/Host',
      value: 'zap'
    });

    const detectorElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Detector',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorElement,
      label: 'IP Address',
      value: '127.0.0.1'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 5,
      label: 'Size',
      value: '180'
    });

    const dataElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 6,
      label: 'Data',
      value: ''
    });

    const dataEmptyElement = generic.assertDetailRowChild(assert, {
      parentElement: dataElement,
      label: '',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 1,
      label: 'Filename',
      value: 'foobarbaz.sh',
      metaKey: 'filename'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 2,
      label: 'Hash',
      value: '123987def',
      metaKey: 'hash'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 3,
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

    generic.assertRelatedLinks(assert, {
      column: 2,
      row: 1,
      values: [
        'Investigate Original Event'
      ],
      urls: [
        reRelatedLinkOne
      ]
    });
  });

  test('renders detail for legacy ecat event', async function(assert) {
    const events = getAllEvents();
    const [ item ] = events.filter((e) => e.id === ecatEventId);

    this.set('expandedId', ecatEventId);
    this.set('item', item);

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertDetailColumns(assert, {
      total: 4,
      children: 4
    });

    const detectorRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Detector',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorRowElement,
      childRow: 1,
      label: 'Host',
      value: 'it_laptop1.eng.matrix.com',
      metaKey: 'dns_hostname'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorRowElement,
      childRow: 2,
      label: 'NWE Agent ID',
      value: '26C5C21F-4DA8-3A00-437C-AB7444987430',
      metaKey: 'ecat_agent_id'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorRowElement,
      childRow: 3,
      label: 'IP Address',
      value: '100.3.36.242',
      metaKey: 'ip_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorRowElement,
      childRow: 4,
      label: 'MAC Address',
      value: 'B8-4B-2F-08-6A-AD-5A-C7',
      metaKey: 'mac_address'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorRowElement,
      childRow: 5,
      label: 'Operating System',
      value: 'Windows 7',
      metaKey: 'os'
    });

    generic.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Size',
      value: '23562'
    });

    const dataElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
      label: 'Data',
      value: ''
    });

    const dataEmptyElement = generic.assertDetailRowChild(assert, {
      parentElement: dataElement,
      label: '',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 1,
      label: 'Bit9 Status',
      value: 'bad',
      metaKey: 'bit9_status'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 2,
      label: 'Filename',
      value: 'AppIdPolicyEngineApi.dll',
      metaKey: 'filename'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 3,
      label: 'Hash',
      value: 'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3',
      metaKey: 'hash'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 4,
      label: 'Module Signature',
      value: 'ABC Inc.',
      metaKey: 'module_signature'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 5,
      label: 'OPSWAT Result',
      value: 'OPSWAT result here',
      metaKey: 'opswat_result'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 6,
      label: 'Size',
      value: '23562',
      metaKey: 'size'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 7,
      label: 'YARA Result',
      value: 'N YARA rules matched',
      metaKey: 'yara_result'
    });

    generic.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Score',
      value: '1-2-3-4'
    });

    generic.assertNoRelatedLinks(assert, {
      column: 2
    });
  });

  test('renders detail for empty network event', async function(assert) {
    this.set('expandedId', networkEventId);
    this.set('item', emptyNetworkEvent);

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertDetailColumns(assert, {
      total: 12,
      children: 4
    });

    const sourceElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    const sourceDeviceElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      label: 'Device',
      value: ''
    });

    const sourceGeolocationElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceGeolocationElement,
      label: '',
      value: 'N/A'
    });

    const sourceUserElement = generic.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceUserElement,
      label: '',
      value: 'N/A'
    });

    const targetElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    const targetDeviceElement = generic.assertDetailRowChild(assert, {
      parentElement: targetElement,
      label: 'Device',
      value: ''
    });

    const targetGeolocationElement = generic.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      label: '',
      value: 'N/A'
    });

    const targetUserElement = generic.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: targetUserElement,
      label: '',
      value: 'N/A'
    });

    const detectorElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
      label: 'Detector',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: detectorElement,
      label: '',
      value: 'N/A'
    });

    const dataElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Data',
      value: ''
    });

    const dataEmptyElement = generic.assertDetailRowChild(assert, {
      parentElement: dataElement,
      label: '',
      value: ''
    });

    generic.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      label: '',
      value: 'N/A'
    });

    generic.assertNoRelatedLinks(assert, {
      column: 2
    });
  });
});
