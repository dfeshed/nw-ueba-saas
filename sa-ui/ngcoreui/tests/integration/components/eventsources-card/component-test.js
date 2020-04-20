import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | eventsources-card', function(hooks) {
  setupRenderingTest(hooks);

  test('eventsources-card renders', async function(assert) {

    await render(hbs`{{eventsources-card}}`);
    assert.equal(this.element.querySelectorAll('.rsa-data-table').length, 1);

    const headers = this.element.querySelectorAll('.rsa-data-table .rsa-data-table-header-cell');
    let headersActual = [];
    headers.forEach((x) => headersActual.push(x.textContent.trim()));
    headersActual = headersActual.filter(function(el) {
      return el != '';
    });
    const headersExpected = ['Protocol', 'Total Events', 'Event Rate', 'Total Bytes', 'Error Count'];
    assert.deepEqual(headersExpected, headersActual);

    const noEventSourcesMsg = this.element.querySelectorAll('.rsa-data-table .no-results-message').length;
    assert.equal(noEventSourcesMsg, 1);
  });
});

