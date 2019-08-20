import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';


module('Integration | Helper |is-timestomped', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  test('it renders is-timestomped helper', async function(assert) {
    this.set('field', 'creationTime');
    this.set('item', { id: 2 });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'false', 'defaulat is-timestomped false');

    this.set('field', 'creationTime');
    this.set('item', { id: 2, creationTimeStomped: true });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'true', 'creationTIme is-timestomped true');

    this.set('field', 'creationTimeSi');
    this.set('item', { id: 2, creationTimeStomped: true });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'true', 'creationTImeSI is-timestomped true');

    this.set('field', 'alteredTime');
    this.set('item', { id: 2, alteredTimeStomped: true });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'true', 'alteredTime is-timestomped true');


    this.set('field', 'alteredTimeSi');
    this.set('item', { id: 2, alteredTimeStomped: true });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'true', 'alteredTimeSi is-timestomped true');

    this.set('field', 'fileReadTime');
    this.set('item', { id: 2, fileReadTimeStomped: true });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'true', 'fileReadTime is-timestomped true');

    this.set('field', 'fileReadTimeSi');
    this.set('item', { id: 2, fileReadTimeStomped: true });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'true', 'fileReadTimeSi is-timestomped true');

    this.set('field', 'mftChangedTime');
    this.set('item', { id: 2, mftChangedTimeStomped: true });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'true', 'mftChangedTime is-timestomped true');

    this.set('field', 'mftChangedTimeSi');
    this.set('item', { id: 2, mftChangedTimeStomped: true });
    await render(hbs`{{is-timestomped item field}}`);
    assert.equal(findAll('.ember-view')[0].textContent.trim(), 'true', 'mftChangedTimeSi is-timestomped true');

  });

});