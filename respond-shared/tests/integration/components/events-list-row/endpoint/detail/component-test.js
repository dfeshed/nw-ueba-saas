import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { emptyEndpointEvent } from '../../empty-data';
import { endpointRelatedLinkOne, endpointRelatedLinkTwo, endpointEventId, getAllEvents, getServices } from '../../../events-list/data';
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
    this.set('services', getServices());

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand) services=services}}`);

    endpoint.assertDetailColumns(assert, {
      total: 13,
      children: 17
    });

    const sourceElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    const sourceDeviceElement = endpoint.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      label: 'Device',
      value: ''
    });

    const sourceGeolocationElement = endpoint.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: sourceGeolocationElement,
      label: '',
      value: 'N/A'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 2,
      label: 'Filename',
      value: 'dtf.exe'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 3,
      label: 'Hash',
      value: '6fccf2a31310ea8b1eb2f4607ae881551c6b9df8755384d7a7f71b5f22124ad6'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 4,
      label: 'Launch Argument',
      value: 'dtf.exe  -dll:ioc.dll -testcase:353'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 5,
      label: 'Path',
      value: 'C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\'
    });

    const sourceUserElement = endpoint.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 6,
      label: 'User',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: sourceUserElement,
      label: 'Username',
      value: 'CORP\\menons4'
    });

    const targetElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    const targetDeviceElement = endpoint.assertDetailRowChild(assert, {
      parentElement: targetElement,
      label: 'Device',
      value: ''
    });

    const targetGeolocationElement = endpoint.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      label: '',
      value: 'N/A'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 2,
      label: 'Filename',
      value: 'cmd.EXE'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 3,
      label: 'Hash',
      value: '9f7ebb79def0bf8cccb5a902db11746375af3fe618355fe5a69c69e4bcd50ac9'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 4,
      label: 'Launch Argument',
      value: 'cmd.EXE /C COPY /Y C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\dtf.exe C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\MSHTA.EXE'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 5,
      label: 'Path',
      value: 'C:\\WINDOWS\\System32\\'
    });

    const targetUserElement = endpoint.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 6,
      label: 'User',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: targetUserElement,
      label: '',
      value: 'N/A'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 3,
      label: 'Domain/Host',
      value: 'INENMENONS4L2C'
    });

    const detectorElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Detector',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: detectorElement,
      label: 'Device Class',
      value: 'Windows Hosts'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: detectorElement,
      childRow: 2,
      label: 'IP Address',
      value: '10.6.66.141'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: detectorElement,
      childRow: 3,
      label: 'Product Name',
      value: 'nwendpoint'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 5,
      label: 'Size',
      value: '41'
    });

    const dataElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 6,
      label: 'Data',
      value: ''
    });

    const dataEmptyElement = endpoint.assertDetailRowChild(assert, {
      parentElement: dataElement,
      label: '',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 1,
      label: 'Filename',
      value: 'test_filename',
      metaKey: 'filename'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 2,
      label: 'Hash',
      value: 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa',
      metaKey: 'hash'
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      childRow: 3,
      label: 'Size',
      value: '41',
      metaKey: 'size'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 7,
      label: 'Agent ID',
      value: 'C593263F-E2AB-9168-EFA4-C683E066A035'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 8,
      label: 'Alias Host',
      value: 'WIN7ENTX64'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 9,
      label: 'Device Type',
      value: 'nwendpoint'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 10,
      label: 'Target Domain',
      value: 'nist.gov'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 11,
      label: 'Source Domain',
      value: 'corp.rsa'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 12,
      label: 'Event Source',
      value: '10.63.0.117:56005'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 13,
      label: 'Event Source ID',
      value: '857775'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 14,
      label: 'Target Host',
      value: 'test_host_dst'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 15,
      label: 'Source Host',
      value: 'test_host_src'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 16,
      label: 'User',
      value: 'CORP\\menons4'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 17,
      label: 'User Src',
      value: 'CORP\\menons4'
    });

    endpoint.assertRelatedLinks(assert, {
      column: 2,
      row: 1,
      values: [
        'Investigate Original Event',
        'Investigate Destination Domain'
      ],
      urls: [
        endpointRelatedLinkOne,
        endpointRelatedLinkTwo
      ]
    });

    endpoint.assertProcessAnalysisLink(assert, {
      value: 'Analyze Process'
    });
  });

  test('renders detail for empty endpoint event', async function(assert) {
    this.set('expandedId', endpointEventId);
    this.set('item', emptyEndpointEvent);

    await render(hbs`{{events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    endpoint.assertDetailColumns(assert, {
      total: 12,
      children: 5
    });

    const sourceElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 1,
      label: 'Source',
      value: ''
    });

    const sourceDeviceElement = endpoint.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      label: 'Device',
      value: ''
    });

    const sourceGeolocationElement = endpoint.assertDetailRowChild(assert, {
      parentElement: sourceDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: sourceGeolocationElement,
      label: '',
      value: 'N/A'
    });

    const sourceUserElement = endpoint.assertDetailRowChild(assert, {
      parentElement: sourceElement,
      childRow: 2,
      label: 'User',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: sourceUserElement,
      label: '',
      value: 'N/A'
    });

    const targetElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Target',
      value: ''
    });

    const targetDeviceElement = endpoint.assertDetailRowChild(assert, {
      parentElement: targetElement,
      label: 'Device',
      value: ''
    });

    const targetGeolocationElement = endpoint.assertDetailRowChild(assert, {
      parentElement: targetDeviceElement,
      label: 'Geolocation',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: targetGeolocationElement,
      label: '',
      value: 'N/A'
    });

    const targetUserElement = endpoint.assertDetailRowChild(assert, {
      parentElement: targetElement,
      childRow: 2,
      label: 'User',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: targetUserElement,
      label: '',
      value: 'N/A'
    });

    const detectorElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
      label: 'Detector',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: detectorElement,
      label: '',
      value: 'N/A'
    });

    const dataElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Data',
      value: ''
    });

    const dataEmptyElement = endpoint.assertDetailRowChild(assert, {
      parentElement: dataElement,
      label: '',
      value: ''
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: dataEmptyElement,
      label: '',
      value: 'N/A'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 5,
      label: 'Device Type',
      value: 'nwendpoint'
    });

    endpoint.assertNoRelatedLinks(assert, {
      column: 2
    });
  });

});
