import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../helpers/engine-resolver';
import $ from 'jquery';
import Ember from 'ember';

moduleForComponent('translate-title', 'Integration | Helper | translate title', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('i18n');
  }
});

test('it renders translate title helper', function(assert) {
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
  this.render(hbs`{{translate-title column}}`);
  assert.equal($('.ember-view')[0].innerText, 'Machine Name', 'translate title is rendered');
});
