import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { updateEditableField } from '../../../../helpers/editable-field';

moduleForComponent('rsa-incidents/inspector-header', 'Integration | Component | Incident Inspector Header', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('The rsa-incidents/inspector-header component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-incidents/inspector-header updateItem=update}}`);
  assert.equal(this.$('.incident-inspector-header').length, 1, 'The incident inspector header is found in the DOM');
});

test('The rsa-incidents/inspector-header contains the expected data for display', function(assert) {
  this.set('info', {
    id: 'INC-1234',
    name: 'Something Wicked This Way Comes'
  });
  this.render(hbs`{{rsa-incidents/inspector-header info=info}}`);
  assert.equal(this.$('.incident-inspector-header .id').text().trim(), 'INC-1234', 'The ID appears as expected');
  assert.equal(this.$('.incident-inspector-header .name .editable-field__value').text().trim(), 'Something Wicked This Way Comes', 'The Name appears as expected');
});

test('Changing the field calls the update action', function(assert) {
  this.set('info', {
    id: 'INC-1234',
    name: 'Something Wicked This Way Comes'
  });
  this.set('update', () => {
    assert.ok(true);
  });
  this.render(hbs`{{rsa-incidents/inspector-header info=info updateItem=update}}`);
  return updateEditableField('.incident-inspector-header', 'Something Wicked This Way Went');
});