import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchFetch } from '../../../helpers/patch-fetch';
import { Promise } from 'rsvp';

module('Integration | Component | entity-details-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('entity-details')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return { data: [''] };
          }
        });
      });
    });
  });

  test('it renders', async function(assert) {

    await render(hbs`{{entity-details-container entityId='123' entityType='user' alertId='alert-1' indicatorId='ind-1'}}`);

    assert.equal(this.element.textContent.trim(), 'Watch Profile');
  });
});
