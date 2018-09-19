import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { run } from '@ember/runloop';
import { set } from '@ember/object';
import { settled, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { storyLineEvents, reEventId, networkEventId } from '../../../events-list/data';
import { storyDatasheet } from 'respond/selectors/storyline';
import * as generic from './helpers';

module('Integration | Component | events-list-row/generic/detail', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    patchReducer(this, Immutable.from(storyLineEvents));
    this.set('expandedId', null);
    this.set('expand', () => {
    });
  });

  test('renders detail for network event', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const [ item ] = events.filter((e) => e.id === networkEventId);

    this.set('expandedId', networkEventId);
    this.set('item', item);

    await render(hbs`{{rsa-incident/events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertDetailColumns(assert, {
      total: 12,
      children: 9
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 1,
      label: 'Detector',
      value: '',
      nestedColumns: 1
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 2,
      label: 'Size',
      value: '4175'
    });

    const dataRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
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
      value: '4175'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 4,
      label: 'Analysis Service',
      value: 'ssl over non-standard port'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 5,
      label: 'Analysis Session',
      value: 'ratio medium transmitted'
    });

    const destRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 6,
      label: 'Destination',
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
      value: '10.4.61.44'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 3,
      label: 'MAC Address',
      value: '00:50:56:33:18:15'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 4,
      label: 'Port',
      value: '5671'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowElement,
      subRowIndex: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 7,
      label: 'Event Source',
      value: '10.4.61.33:56005'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 8,
      label: 'Event Source ID',
      value: '150'
    });

    const sourceRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 9,
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
      value: '10.4.61.97'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 3,
      label: 'MAC Address',
      value: '00:50:56:33:18:18'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 4,
      label: 'Port',
      value: '36749'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowElement,
      subRowIndex: 2,
      label: 'User',
      value: ''
    });
  });

  test('event labels properly update when locale is changed', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const [ item ] = events.filter((e) => e.id === networkEventId);

    this.set('expandedId', networkEventId);
    this.set('item', item);

    await render(hbs`{{rsa-incident/events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    const detectorInGerman = 'Detektor';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'de-de', { 'respond.eventDetails.labels.detector': detectorInGerman });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 1,
      label: 'Detector',
      value: '',
      nestedColumns: 1
    });

    set(i18n, 'locale', 'de-de');

    return settled().then(async () => {
      generic.assertDetailRow(assert, {
        column: 1,
        row: 1,
        label: detectorInGerman,
        value: '',
        nestedColumns: 1
      });
    });
  });

  test('renders detail for reporting engine event', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const [ item ] = events.filter((e) => e.id === reEventId);

    this.set('expandedId', reEventId);
    this.set('item', item);

    await render(hbs`{{rsa-incident/events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertDetailColumns(assert, {
      total: 12,
      children: 9
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 1,
      label: 'Domain/Host',
      value: 'zap'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 2,
      label: 'Detector',
      value: '',
      nestedColumns: 1
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 3,
      label: 'Size',
      value: '180'
    });

    const dataRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
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
      value: '180'
    });

    const destRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 5,
      label: 'Destination',
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
      value: 'Gaithersburg'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 2,
      label: 'Country',
      value: 'United States'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 3,
      label: 'Domain/Host',
      value: 'nist.gov'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 4,
      label: 'Latitude',
      value: '39'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 5,
      label: 'Longitude',
      value: '-77'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildTwoElement,
      subRowIndex: 6,
      label: 'Organization',
      value: 'National Bureau of Standards'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 2,
      label: 'IP Address',
      value: '129.6.15.28'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 3,
      label: 'MAC Address',
      value: '00:00:00:00:5E:00'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowChildOneElement,
      subRowIndex: 4,
      label: 'Port',
      value: '123'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: destRowElement,
      subRowIndex: 2,
      label: 'User',
      value: ''
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 6,
      label: 'Domain Dst',
      value: 'nist.gov'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 7,
      label: 'Event Source',
      value: '10.25.51.157:50105'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 8,
      label: 'Event Source ID',
      value: '47560522'
    });

    const sourceRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 9,
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
      value: '192.168.100.185'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 3,
      label: 'MAC Address',
      value: '00:00:46:8F:F4:20'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowChildOneElement,
      subRowIndex: 4,
      label: 'Port',
      value: '123'
    });

    generic.assertDetailRowChild(assert, {
      parentElement: sourceRowElement,
      subRowIndex: 2,
      label: 'User',
      value: ''
    });
  });
});
