import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { storyLineEvents, endpointEventId } from '../../../events-list/data';
import { storyDatasheet } from 'respond/selectors/storyline';
import * as endpoint from '../../generic/detail/helpers';

module('Integration | Component | events-list-row/endpoint/detail', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    patchReducer(this, Immutable.from(storyLineEvents));
    this.set('expandedId', null);
    this.set('expand', () => {
    });
  });

  test('renders detail for endpoint event', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const [ item ] = events.filter((e) => e.id === endpointEventId);

    this.set('expandedId', endpointEventId);
    this.set('item', item);

    await render(hbs`{{rsa-incident/events-list-row item=item expandedId=expandedId expand=(action expand)}}`);

    endpoint.assertDetailColumns(assert, {
      total: 4,
      children: 12
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 1,
      label: 'Domain/Host',
      value: 'INENMENONS4L2C'
    });

    const detectorRowElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 2,
      label: 'Detector',
      value: '',
      childKeys: 2,
      childValues: 2,
      nestedColumns: 1
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: detectorRowElement,
      label: 'Product Name',
      value: 'nwendpoint',
      childKeys: 1,
      childValues: 1,
      nestedColumns: 0
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 3,
      label: 'Size',
      value: '41'
    });

    const dataRowElement = endpoint.assertDetailRowParent(assert, {
      column: 1,
      row: 4,
      label: 'Data',
      value: '',
      childKeys: 3,
      childValues: 3,
      nestedColumns: 2
    });

    const dataRowChildOneElement = endpoint.assertDetailRowChild(assert, {
      parentElement: dataRowElement,
      label: '',
      value: '',
      childKeys: 2,
      childValues: 2,
      nestedColumns: 1
    });

    endpoint.assertDetailRowChild(assert, {
      parentElement: dataRowChildOneElement,
      label: 'Size',
      value: '41',
      childKeys: 1,
      childValues: 1,
      nestedColumns: 0
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 5,
      label: 'Agent ID',
      value: 'C593263F-E2AB-9168-EFA4-C683E066A035'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 6,
      label: 'Launch Argument',
      value: 'cmd.EXE /C COPY /Y C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\dtf.exe C:\\Users\\menons4\\Documents\\NWE\\Sunila\\amd64\\MSHTA.EXE'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 7,
      label: 'Device Type',
      value: 'nwendpoint'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 8,
      label: 'Event Source',
      value: '10.63.0.117:56005'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 9,
      label: 'Event Source ID',
      value: '857775'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 10,
      label: 'Source User Account',
      value: 'CORP\\menons4'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 11,
      label: 'User',
      value: 'CORP\\menons4'
    });

    endpoint.assertDetailRow(assert, {
      column: 1,
      row: 12,
      label: 'User Src',
      value: 'CORP\\menons4'
    });
  });

});
