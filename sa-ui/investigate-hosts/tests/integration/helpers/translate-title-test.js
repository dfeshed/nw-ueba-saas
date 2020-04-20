import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Ember from 'ember';

module('Integration | Helper | translate title', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.i18n = this.owner.lookup('service:i18n');
  });

  test('it renders translate title helper', async function(assert) {
    const { Object: EmberObject } = Ember;
    const column = EmberObject.create({
      visible: true,
      dataType: 'string',
      field: 'machineName',
      searchable: true,
      values: 'win6418',
      title: 'investigateHosts.hosts.column.machineName',
      width: '10',
      description: 'Machine Name'
    });
    this.set('column', column);
    await render(hbs`{{translate-title column}}`);
    assert.equal(document.querySelectorAll('.ember-view')[0].textContent.trim(), 'Machine Name', 'translate title is rendered');
  });
});
