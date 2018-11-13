import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/load-more', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('load more renders', async function(assert) {

    await render(hbs`{{endpoint/load-more}}`);

    assert.equal(findAll('.endpoint-load-more').length, 1, 'it renders endpoint load-more');
  });

  test('endpoint load more renders data-table load more', async function(assert) {

    this.set('count', 1000);
    this.set('status', 'stopped');
    this.set('serverId', '123');
    this.set('servers', [{ id: '123', name: 'endpoint-server' }]);
    await render(hbs`{{endpoint/load-more count=count status=status servers=servers serverId=serverId}}`);

    assert.equal(find('.rsa-data-table-load-more button').textContent.trim(), 'Load More', 'it renders load more button');
  });

  test('endpoint load more renders broker message', async function(assert) {

    this.set('count', 1000);
    this.set('status', 'completed');
    this.set('serverId', '123');
    this.set('servers', [{ id: '123', name: 'endpoint-broker-server' }]);

    await render(hbs`{{endpoint/load-more count=count status=status servers=servers serverId=serverId}}`);

    assert.equal(find('.rsa-data-table-load-more').textContent.trim(), 'Reached maximum results supported in a broker view.', 'it renders load more button');
  });
});
