import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('force-layout/node', 'Integration | Component | Force Layout Node', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const datum = {
  x: 100,
  y: 200,
  r: 25,
  xObservable: 100,
  yObservable: 200,
  isSelected: false,
  isHidden: false,
  type: 'foo',
  text: 'bar'
};

test('it renders the DOM with correct CSS classes', function(assert) {
  this.set('datum', datum);
  this.render(hbs`{{rsa-force-layout/node datum=datum}}`);

  const $el = this.$('.rsa-force-layout-node');
  assert.equal($el.length, 1, 'Unable to find the root component DOM element.');

  assert.ok($el.hasClass('foo'), 'Expected to not a type CSS class.');

  assert.notOk($el.hasClass('is-hidden'), 'Expected to not find is-hidden CSS class.');
  assert.notOk($el.hasClass('is-selected'), 'Expected to not find is-selected CSS class.');
  assert.equal($el.find('text').text().trim(), datum.text, 'Expected node text to be rendered as text in DOM.');
});

test('it renders appropriate class names for selected & hidden nodes', function(assert) {
  const datum2 = {
    ...datum,
    isSelected: true,
    isHidden: true
  };
  this.set('datum2', datum2);
  this.render(hbs`{{rsa-force-layout/node datum=datum2}}`);

  const $el = this.$('.rsa-force-layout-node');
  assert.ok($el.hasClass('is-hidden'), 'Expected to find is-hidden CSS class.');
  assert.ok($el.hasClass('is-selected'), 'Expected to find is-selected CSS class.');
});

test('it fires its click action and passes back the datum when DOM is clicked', function(assert) {
  assert.expect(2);

  const clickAction = function(arg) {
    assert.ok(true, 'Expected click action to be invoked.');
    assert.equal(arg, datum, 'Expected callback to receive datum.');
  };
  this.setProperties({ datum, clickAction });
  this.render(hbs`{{rsa-force-layout/node datum=datum clickAction=clickAction}}`);

  const $el = this.$('.rsa-force-layout-node');
  $el.click();
});
