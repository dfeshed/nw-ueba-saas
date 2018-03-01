import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import EmberObject, { set } from '@ember/object';
import $ from 'jquery';

moduleForComponent('rsa-group-table-group-header', 'Integration | Component | rsa group table group header', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const group = {
  id: 'id1',
  title: 'foo',
  items: (new Array(10)).fill({})
};

const index = 1;

const table = EmberObject.create();

test('it renders the group value by default when no block is given', function(assert) {

  set(group, 'isOpen', true);
  this.setProperties({ group, index, table });

  this.render(hbs`{{rsa-group-table/group-header
    group=group
    index=index
    table=table
  }}`);

  return wait()
    .then(() => {
      const cell = this.$('.rsa-group-table-group-header');
      assert.ok(cell.length, 'Expected to find root DOM node');
      assert.equal(cell.text().trim(), group.title);
      assert.ok(cell.hasClass('is-open'), 'Expected open group to have is-open css class');

      set(group, 'isOpen', false);
      return wait();
    })
    .then(() => {
      const cell = this.$('.rsa-group-table-group-header');
      assert.ok(cell.hasClass('is-not-open'), 'Expected closed group to have is-not-open css class');
    });
});

test('it yields the group & index when a block is given', function(assert) {

  set(group, 'isOpen', true);
  this.setProperties({ group, index, table });

  this.render(hbs`{{#rsa-group-table/group-header
    group=group
    index=index
    table=table
    as |header|
  }}
    <span class="group-title">{{header.group.title}}</span>
    <span class="index">{{header.index}}</span>
    <span class="is-open">{{header.group.isOpen}}</span>
    <span class="toggle-action" {{action header.toggleAction}}>Toggle</span>
  {{/rsa-group-table/group-header}}`);

  return wait()
    .then(() => {
      const el = this.$('.rsa-group-table-group-header');

      assert.equal(el.find('.index').text().trim(), index);
      assert.equal(el.find('.group-title').text().trim(), group.title);

      const elIsOpen = el.find('.is-open');
      assert.ok(elIsOpen.length);
      assert.equal(elIsOpen.text().trim(), 'true');

      const elToggle = el.find('.toggle-action');
      assert.ok(elToggle.length);

      elToggle.trigger('click');
      return wait();
    })
    .then(() => {
      const elIsOpen2 = this.$('.rsa-group-table-group-header .is-open');
      assert.equal(elIsOpen2.text().trim(), 'false');
    });
});

test('clicking on it fires the appropriate callback on the table parent', function(assert) {
  let whichAction;
  table.setProperties({
    groupClickAction(payload) {
      assert.equal(payload.group, group, 'Expected callback to receive the group data object as an input param');
      whichAction = 'groupClickAction';
    },
    groupCtrlClickAction(payload) {
      assert.equal(payload.group, group, 'Expected callback to receive the group data object as an input param');
      whichAction = 'groupCtrlClickAction';
    },
    groupShiftClickAction(payload) {
      assert.equal(payload.group, group, 'Expected callback to receive the group data object as an input param');
      whichAction = 'groupShiftClickAction';
    }
  });

  this.setProperties({ group, index, table });

  this.render(hbs`{{rsa-group-table/group-header
    group=group
    index=index
    table=table
  }}`);

  return wait()
    .then(() => {
      const row = this.$('.rsa-group-table-group-header');
      row.trigger('click');
      assert.equal(whichAction, 'groupClickAction', 'Expected click handler to be invoked');

      // eslint-disable-next-line new-cap
      const shiftClick = $.Event('click');
      shiftClick.shiftKey = true;
      row.trigger(shiftClick);
      assert.equal(whichAction, 'groupShiftClickAction', 'Expected SHIFT click handler to be invoked');

      // eslint-disable-next-line new-cap
      const ctrlClick = $.Event('click');
      ctrlClick.ctrlKey = true;
      row.trigger(ctrlClick);
      assert.equal(whichAction, 'groupCtrlClickAction', 'Expected CTRL click handler to be invoked');
    });
});
