import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { run } from '@ember/runloop';
import { set } from '@ember/object';
import { settled, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { storyLineEvents, networkEventId } from '../../../events-list/data';
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
      total: 4,
      children: 7
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

    const thirdRowElement = generic.assertDetailRowParent(assert, {
      column: 1,
      row: 3,
      label: 'Data',
      value: '',
      childKeys: 3,
      childValues: 3,
      nestedColumns: 2
    });

    const thirdRowChildOneElement = generic.assertDetailRowChild(assert, {
      parentElement: thirdRowElement,
      label: '',
      value: '',
      childKeys: 2,
      childValues: 2,
      nestedColumns: 1
    });

    generic.assertDetailRowChild(assert, {
      parentElement: thirdRowChildOneElement,
      label: 'Size',
      value: '4175',
      childKeys: 1,
      childValues: 1,
      nestedColumns: 0
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

    generic.assertDetailRow(assert, {
      column: 1,
      row: 6,
      label: 'Event Source',
      value: '10.4.61.33:56005'
    });

    generic.assertDetailRow(assert, {
      column: 1,
      row: 7,
      label: 'Event Source ID',
      value: '150'
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

});
