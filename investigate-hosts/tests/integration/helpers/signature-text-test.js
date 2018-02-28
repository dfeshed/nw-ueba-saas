import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../helpers/engine-resolver';
import $ from 'jquery';

moduleForComponent('signature-text', 'Integration | Helper | signature text', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts')
});

test('it renders signature text helper', function(assert) {
  this.render(hbs`{{signature-text}}`);
  assert.equal($('.ember-view')[0].innerText, 'unsigned', 'signature-text is rendered');
});

test('it renders signature text with value', function(assert) {
  this.set('text', 'microsoft,signed');
  this.render(hbs`{{signature-text text}}`);
  assert.equal($('.ember-view')[0].innerText, 'microsoft,signed', 'signature-text with value and signer is rendered');
});

test('it renders signature text with value and signer', function(assert) {
  this.set('text', 'microsoft,signed');
  this.set('signer', 'verified');
  this.render(hbs`{{signature-text text signer}}`);
  assert.equal($('.ember-view')[0].innerText, 'microsoft,signed,verified', 'signature-text with value and signer is rendered');
});
