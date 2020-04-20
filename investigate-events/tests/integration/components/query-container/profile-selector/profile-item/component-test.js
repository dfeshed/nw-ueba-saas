import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Profile Item', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const itemSelector = '.option-name';
  const profile1 = {
    id: '3333',
    name: 'Another Profile',
    columnGroupView: 'CUSTOM',
    columnGroup: {
      id: 'cg1',
      name: 'Column Group 1'
    },
    metaGroup: {
      id: 'mg1',
      name: 'Meta Group 1'
    },
    preQuery: 'service=80',
    contentType: 'USER'
  };

  test('it renders profile name correctly', async function(assert) {
    this.set('profile', profile1);
    await render(hbs`
      {{query-container/profile-selector/profile-item profile=profile}}
    `);
    assert.equal(findAll(itemSelector).length, 1, 'Shall render profile item');
    assert.equal(find(itemSelector).innerText.trim(), profile1.name, 'Shall render profile name correctly');
  });
});
