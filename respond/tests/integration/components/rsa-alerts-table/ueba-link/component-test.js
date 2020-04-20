import { module, test } from 'qunit';
import Service from '@ember/service';
import hbs from 'htmlbars-inline-precompile';
import { computed } from '@ember/object';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, render, findAll, find } from '@ember/test-helpers';

const transitions = [];
const linkTo = '.ueba-link-to';
const selector = '[test-id=respondUebaLink]';
const storyPointId = '586ecfc0ecd25950034e1318';
const alertId = '1486f9ac-974d-4be6-8641-1b0826097854';
const alertSource = 'User Entity Behavior Analytics';
const entityId = '1c86c083-d82d-47f4-8930-187473ddad13';
const classifierId = '3af8801b-0979-4066-b906-6330eaca2337';

module('Integration | Component | rsa alerts table ueba link', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
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
      hasUEBAAccess: computed(function() {
        return true;
      })
    }));
  });

  test('entity_id and classifier_id must be available on the alert to render link', async function(assert) {
    assert.expect(11);

    this.set('storyPointId', storyPointId);
    this.set('alert', {
      'entity_id': entityId,
      'classifier_id': classifierId,
      'source': alertSource
    });

    await render(hbs`{{rsa-alerts-table/ueba-link alert=alert storyPointId=storyPointId}}`);

    assert.ok(find(selector).classList.contains('respond-ueba-link'));
    assert.equal(find(selector).textContent.trim(), alertSource);
    assert.equal(findAll(linkTo).length, 1);

    await click('.ueba-link-to');

    assert.deepEqual(transitions, [{
      name: 'incident.ueba',
      queryParams: {
        ueba: `/user/${entityId}/alert/${classifierId}`,
        selection: '586ecfc0ecd25950034e1318'
      }
    }]);

    this.set('alert', {
      'entity_id': entityId,
      'classifier_id': '',
      'source': alertSource
    });
    assert.equal(find(selector).textContent.trim(), alertSource);
    assert.equal(findAll(linkTo).length, 0);

    this.set('alert', {
      'id': alertId,
      'entity_id': entityId,
      'classifier_id': classifierId,
      'source': alertSource
    });
    assert.equal(find(selector).textContent.trim(), alertSource);
    assert.equal(findAll(linkTo).length, 1);

    await click('.ueba-link-to');

    assert.deepEqual(transitions, [{
      name: 'incident.ueba',
      queryParams: {
        ueba: `/user/${entityId}/alert/${classifierId}`,
        selection: '586ecfc0ecd25950034e1318'
      }
    }, {
      name: 'incident.ueba',
      queryParams: {
        ueba: `/user/${entityId}/alert/${classifierId}/indicator/${alertId}`,
        selection: '586ecfc0ecd25950034e1318'
      }
    }]);

    this.set('alert', {
      'id': alertId,
      'entity_id': '',
      'classifier_id': classifierId,
      'source': alertSource
    });
    assert.equal(find(selector).textContent.trim(), alertSource);
    assert.equal(findAll(linkTo).length, 0);
  });

  test('without ueba permissions the link fails to render', async function(assert) {
    assert.expect(2);

    this.set('storyPointId', storyPointId);
    this.set('alert', {
      'id': alertId,
      'entity_id': entityId,
      'classifier_id': classifierId,
      'source': alertSource
    });

    this.owner.register('service:accessControl', Service.extend({
      hasUEBAAccess: computed(function() {
        return false;
      })
    }));

    await render(hbs`{{rsa-alerts-table/ueba-link alert=alert storyPointId=storyPointId}}`);

    assert.equal(find(selector).textContent.trim(), alertSource);
    assert.equal(findAll(linkTo).length, 0);
  });
});
