import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import $ from 'jquery';

moduleForComponent('signature-text', 'Integration | Helper | is-selected', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts')
});

test('it renders is-selected helper', function(assert) {
  this.set('selectedList', [ { id: 0 }, { id: 1 } ]);
  this.set('item', { id: 2 });
  this.render(hbs`{{is-selected selectedList item}}`);
  assert.equal($('.ember-view')[0].innerText, 'false', 'is-selected should false');

  this.set('item', { id: 0 });
  this.render(hbs`{{is-selected selectedList item}}`);
  assert.equal($('.ember-view')[0].innerText, 'true', 'is-selected should true');

});