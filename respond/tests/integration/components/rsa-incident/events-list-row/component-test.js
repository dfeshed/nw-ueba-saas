import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { storyLineEvents, reEventId, networkEventId } from '../events-list/data';
import { storyDatasheet } from 'respond/selectors/storyline';
import * as generic from './helpers/generic';

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
    const [ item ] = events.filter((e) => e.id === reEventId);

    this.set('item', item);

    await render(hbs`{{rsa-incident/events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertRowPresent(assert);

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
    const [ item ] = events.filter((e) => e.id === networkEventId);

    this.set('item', item);

    await render(hbs`{{rsa-incident/events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    generic.assertRowPresent(assert);

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
});
