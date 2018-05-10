import { module, test } from 'qunit';
import Service from '@ember/service';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { computed } from '@ember/object';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, render, findAll, find } from '@ember/test-helpers';

const transitions = [];
const eventType = 'Instant IOC';
const linkTo = '.recon-link-to';
const selector = '[test-id=respondReconLink]';

module('Integration | Component | rsa alerts table recon link', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    patchReducer(this, Immutable.from({
      respond: {
        recon: {
          serviceData: {
            '555d9a6fe4b0d37c827d402e': {
              displayName: 'loki-concentrator',
              host: '10.4.61.33',
              id: '555d9a6fe4b0d37c827d402d',
              name: 'CONCENTRATOR',
              port: 56005,
              version: '11.2.0.0'
            }
          }
        }
      }
    }));
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));
    this.owner.register('service:accessControl', Service.extend({
      hasReconAccess: computed(function() {
        return true;
      })
    }));
  });

  test('event_source_id and event_source must be available and found in core services to render link', async function(assert) {
    assert.expect(9);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: '10.4.61.33:56005',
      event_source_id: '150',
      type: 'Instant IOC'
    });

    await render(hbs`{{rsa-alerts-table/recon-link item=item}}`);

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 1);

    await click('.recon-link-to');

    assert.deepEqual(transitions, [{
      name: 'incident.recon',
      queryParams: {
        endpointId: '150',
        eventId: '555d9a6fe4b0d37c827d402d'
      }
    }]);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: null,
      event_source_id: '150',
      type: 'Instant IOC'
    });
    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: '10.4.61.33:56005',
      event_source_id: '150',
      type: 'Instant IOC'
    });

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 1);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: '10.4.61.33:56005',
      event_source_id: null,
      type: 'Instant IOC'
    });

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);
  });

  test('when no core services are found that match event source the link fails to render', async function(assert) {
    assert.expect(2);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: 'sandbox:56005',
      event_source_id: '150',
      type: 'Instant IOC'
    });

    await render(hbs`{{rsa-alerts-table/recon-link item=item}}`);

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);
  });

  test('when event source format fails to match regex pattern the link fails to render', async function(assert) {
    assert.expect(2);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: 'x',
      event_source_id: '150',
      type: 'Instant IOC'
    });

    await render(hbs`{{rsa-alerts-table/recon-link item=item}}`);

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);
  });

  test('when event source is non string type the link fails to render', async function(assert) {
    assert.expect(2);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: 9,
      event_source_id: '150',
      type: 'Instant IOC'
    });

    await render(hbs`{{rsa-alerts-table/recon-link item=item}}`);

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);
  });

  test('without core services the link fails to render', async function(assert) {
    assert.expect(2);

    patchReducer(this, Immutable.from({}));

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: '10.4.61.33:56005',
      event_source_id: '150',
      type: 'Instant IOC'
    });

    await render(hbs`{{rsa-alerts-table/recon-link item=item}}`);

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);
  });

  test('without event source property the link fails to render', async function(assert) {
    assert.expect(2);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source_id: '150',
      type: 'Instant IOC'
    });

    await render(hbs`{{rsa-alerts-table/recon-link item=item}}`);

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);
  });

  test('without event source id property the link fails to render', async function(assert) {
    assert.expect(2);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: '10.4.61.33:56005',
      type: 'Instant IOC'
    });

    await render(hbs`{{rsa-alerts-table/recon-link item=item}}`);

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);
  });

  test('without recon permissions the link fails to render', async function(assert) {
    assert.expect(2);

    this.set('item', {
      id: '586ecf95ecd25950034e1312:0',
      indicatorId: '586ecf95ecd25950034e1312',
      event_source: '10.4.61.33:56005',
      event_source_id: '150',
      type: 'Instant IOC'
    });

    this.owner.register('service:accessControl', Service.extend({
      hasReconAccess: computed(function() {
        return false;
      })
    }));

    await render(hbs`{{rsa-alerts-table/recon-link item=item}}`);

    assert.equal(find(selector).textContent.trim(), eventType);
    assert.equal(findAll(linkTo).length, 0);
  });
});
