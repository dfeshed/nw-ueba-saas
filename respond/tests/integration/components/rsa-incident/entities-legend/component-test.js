import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import wait from 'ember-test-helpers/wait';
import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import { click, findAll, find, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | Incident Entities Legend', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      const fullState = { respond: { incident: state } };
      patchReducer(this, Immutable.from(fullState));
    };
  });

  const data = [
    { key: 'ip', value: 2 },
    { key: 'host', value: 3 },
    { key: 'domain', value: 4 }
  ];

  const selection = { type: '', ids: [] };


  test('it renders counts and sometimes renders selection info when appropriate', async function(assert) {
    this.setProperties({
      data,
      selection
    });
    await render(hbs`{{rsa-incident/entities-legend data=data selection=selection}}`);

    return wait()
      .then(() => {
        const $el = findAll('.rsa-incident-entities-legend');
        assert.equal($el.length, 1, 'Expected to find root element in DOM.');

        const $counts = findAll('.datum');
        assert.ok($counts.length, 'Expected to find at least one count element in DOM.');

        const $noSelection = findAll('.selection');
        assert.notOk($noSelection.length, 'Expected not to find any selection DOM when no selection has been applied.');

        this.set('selection', { type: 'indicator', ids: ['indicatorId1'] });
        return wait();
      })
      .then(() => {
        const $selection = findAll('.rsa-incident-entities-legend .selection');
        assert.ok($selection.length, 'Expected to find selection DOM when an indicator has been selected.');

        this.set('selection', { type: 'event', ids: ['eventId1'] });
        return wait();
      })
      .then(() => {
        const $selection2 = findAll('.rsa-incident-entities-legend .selection');
        assert.ok($selection2.length, 'Expected to find selection DOM when an event has been selected.');

        this.set('selection', { type: 'node', ids: ['nodeId1'] });
        return wait();
      })
      .then(() => {
        const $selection3 = findAll('.rsa-incident-entities-legend .selection');
        assert.notOk($selection3.length, 'Expected to not find selection DOM when a node has been selected.');
      });
  });

  test('it shows as checked those entity types in the legend that are visible to the user in the graph', async function(assert) {
    setState({
      visibleEntityTypes: ['host', 'ip', 'mac_address', 'user', 'domain', 'file_name', 'file_hash']
    });
    this.setProperties({
      data,
      selection
    });
    await render(hbs`{{rsa-incident/entities-legend data=data selection=selection}}`);
    assert.equal(find('.datum.ip label').classList.contains('checked'), true);
    assert.equal(find('.datum.host label').classList.contains('checked'), true);
    assert.equal(find('.datum.domain label').classList.contains('checked'), true);
  });

  test('it does not show as checked those entity types in the legend that are invisible to the user in the graph', async function(assert) {
    setState({
      visibleEntityTypes: []
    });
    this.setProperties({
      data,
      selection
    });
    await render(hbs`{{rsa-incident/entities-legend data=data selection=selection}}`);
    assert.equal(find('.datum.ip label').classList.contains('checked'), false);
    assert.equal(find('.datum.host label').classList.contains('checked'), false);
    assert.equal(find('.datum.domain label').classList.contains('checked'), false);
  });

  test('clicking on the entity checkbox toggles its visiblity', async function(assert) {
    const ipCheckboxInputSelector = '.datum.ip input';
    const ipLabelSelector = '.datum.ip label';
    setState({
      visibleEntityTypes: []
    });
    this.setProperties({
      data,
      selection
    });
    await render(hbs`{{rsa-incident/entities-legend data=data selection=selection}}`);
    assert.equal(find(ipLabelSelector).classList.contains('checked'), false);
    await click(ipCheckboxInputSelector);
    assert.equal(find(ipLabelSelector).classList.contains('checked'), true);
    await click(ipCheckboxInputSelector);
    assert.equal(find(ipLabelSelector).classList.contains('checked'), false);
  });
});

