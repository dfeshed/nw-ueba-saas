import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { storyLineEvents, reEventId } from '../events-list/data';
import { storyDatasheet } from 'respond/selectors/storyline';
import { selectors, generic } from './selectors';

module('Integration | Component | events-list-row', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    patchReducer(this, Immutable.from(storyLineEvents));
    this.set('expanded', false);
    this.set('expand', () => {
    });
  });

  test('renders generic row for reporting engine event', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    const events = storyDatasheet(redux.getState());
    const [ item ] = events.filter((e) => e.id === reEventId);

    this.set('item', item);

    await render(hbs`{{rsa-incident/events-list-row item=item expanded=expanded expand=(action expand)}}`);

    assert.equal(findAll(selectors.row).length, 1);
    assert.equal(findAll(selectors.genericRow).length, 1);
    assert.equal(findAll(selectors.genericMain).length, 1);
    assert.equal(findAll(selectors.genericDetail).length, 0);

    assert.equal(find(generic.eventTimeLabel).textContent.trim(), 'EVENT TIME');
    assert.ok(find(generic.eventTimeValue).textContent.trim() !== '');
    assert.equal(find(generic.eventTypeLabel).textContent.trim(), 'EVENT TYPE');
    assert.equal(find(generic.eventTypeValue).textContent.trim(), 'Network');
    assert.equal(find(generic.eventDetectorIpLabel).textContent.trim(), 'DETECTOR IP');
    assert.equal(find(generic.eventDetectorIpValue).textContent.trim(), '');
    assert.equal(find(generic.eventFileNameLabel).textContent.trim(), 'FILE NAME');
    assert.equal(find(generic.eventFileNameValue).textContent.trim(), '');
    assert.equal(find(generic.eventFileHashLabel).textContent.trim(), 'FILE HASH');
    assert.equal(find(generic.eventFileHashValue).textContent.trim(), '');

    assert.equal(find(generic.eventTableIpLabel).textContent.trim(), 'IP');
    assert.equal(find(generic.eventTablePortLabel).textContent.trim(), 'PORT');
    assert.equal(find(generic.eventTableHostLabel).textContent.trim(), 'HOST');
    assert.equal(find(generic.eventTableMacLabel).textContent.trim(), 'MAC');
    assert.equal(find(generic.eventTableUserLabel).textContent.trim(), 'USER');

    assert.equal(find(generic.eventSourceLabel).textContent.trim(), 'Source');
    assert.equal(find(generic.eventSourceIpValue).textContent.trim(), '192.168.100.185');
    assert.equal(find(generic.eventSourcePortValue).textContent.trim(), '123');
    assert.equal(find(generic.eventSourceHostValue).textContent.trim(), '');
    assert.equal(find(generic.eventSourceMacValue).textContent.trim(), '00:00:46:8F:F4:20');
    assert.equal(find(generic.eventSourceUserValue).textContent.trim(), '');

    assert.equal(find(generic.eventTargetLabel).textContent.trim(), 'Target');
    assert.equal(find(generic.eventTargetIpValue).textContent.trim(), '129.6.15.28');
    assert.equal(find(generic.eventTargetPortValue).textContent.trim(), '123');
    assert.equal(find(generic.eventTargetHostValue).textContent.trim(), '');
    assert.equal(find(generic.eventTargetMacValue).textContent.trim(), '00:00:00:00:5E:00');
    assert.equal(find(generic.eventTargetUserValue).textContent.trim(), '');
  });
});
