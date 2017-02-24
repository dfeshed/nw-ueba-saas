import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';

moduleForComponent('force-layout/link', 'Integration | Component | Force Layout Link', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const source = {
  id: 'source1',
  r: 50,
  xObservable: 100,
  yObservable: 100,
  isSelected: false,
  isHidden: false
};
const target = {
  id: 'target1',
  r: 50,
  xObservable: 200,
  yObservable: 200
};
const link = {
  id: 'link1',
  type: 'foo',
  text: 'bar',
  source,
  target,
  isSelected: false,
  isHidden: false
};

test('it renders the DOM correctly', function(assert) {
  this.set('link', link);
  this.render(hbs`{{rsa-force-layout/link datum=link}}`);
  const $el = this.$('.rsa-force-layout-link');

  assert.equal($el.length, 1, 'Unable to find the root component DOM element.');

  assert.ok($el.hasClass('foo'), 'Expected to find a type CSS class.');
  assert.notOk($el.hasClass('is-hidden'), 'Expected to not find is-hidden CSS class.');
  assert.notOk($el.hasClass('is-selected'), 'Expected to not find is-selected CSS class.');

  assert.equal($el.find('text').text().trim(), link.text, 'Expected link text to be rendered as text in DOM.');

  const $line = $el.find('line');
  assert.equal($line.length, 1, 'Expected to find a line element.');
  assert.notOk(isNaN($line.attr('x1')), 'Unexpected line endpoint coordinate.');
  assert.notOk(isNaN($line.attr('y1')), 'Unexpected line endpoint coordinate.');
  assert.notOk(isNaN($line.attr('x2')), 'Unexpected line endpoint coordinate.');
  assert.notOk(isNaN($line.attr('y2')), 'Unexpected line endpoint coordinate.');
});

test('it renders appropriate class names for selected & hidden links', function(assert) {
  const link2 = {
    ...link,
    isSelected: true,
    isHidden: true
  };
  this.set('link2', link2);
  this.render(hbs`{{rsa-force-layout/link datum=link2}}`);

  const $el = this.$('.rsa-force-layout-link');
  assert.ok($el.hasClass('is-hidden'), 'Expected to find is-hidden CSS class.');
  assert.ok($el.hasClass('is-selected'), 'Expected to find is-selected CSS class.');
});

test('it fires its click action and passes back the datum when DOM is clicked', function(assert) {
  assert.expect(2);

  const clickAction = function(arg) {
    assert.ok(true, 'Expected click action to be invoked.');
    assert.equal(arg, link, 'Expected callback to receive the link datum.');
  };
  this.setProperties({ link, clickAction });
  this.render(hbs`{{rsa-force-layout/link datum=link clickAction=clickAction}}`);

  const $el = this.$('.rsa-force-layout-link');
  $el.click();
});
