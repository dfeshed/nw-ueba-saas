import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, findAll, find, click, settled } from '@ember/test-helpers';
import EmberObject, { set } from '@ember/object';

module('Integration | Component | rsa group table group header', function(hooks) {
  setupRenderingTest(hooks, {
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

  test('it renders the group value by default when no block is given', async function(assert) {

    set(group, 'isOpen', true);
    this.setProperties({ group, index, table });

    await render(hbs`{{rsa-group-table/group-header
    group=group
    index=index
    table=table
     }}`);


    const cell = findAll('.rsa-group-table-group-header');
    assert.ok(cell.length, 'Expected to find root DOM node');
    assert.equal(cell[0].textContent.trim(), group.title);
    assert.ok(cell[0].classList.contains('is-open'), 'Expected open group to have is-open css class');

    set(group, 'isOpen', false);

    await settled().then(() => {
      const cellModified = find('.rsa-group-table-group-header');
      assert.ok(cellModified.classList.contains('is-not-open'), 'Expected closed group to have is-not-open css class');
    });


  });

  test('it yields the group & index when a block is given', async function(assert) {

    set(group, 'isOpen', true);
    this.setProperties({ group, index, table });

    await render(hbs`{{#rsa-group-table/group-header
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

    const el = find('.rsa-group-table-group-header');

    assert.equal(el.querySelector('.index').textContent.trim(), index);
    assert.equal(el.querySelector('.group-title').textContent.trim(), group.title);

    const elIsOpen = el.querySelectorAll('.is-open');
    assert.ok(elIsOpen.length);
    assert.equal(elIsOpen[0].textContent.trim(), 'true');

    const elToggle = el.querySelectorAll('.toggle-action');
    assert.ok(elToggle.length);

    await click('.toggle-action');

    const elIsOpen2 = find('.rsa-group-table-group-header .is-open');
    assert.equal(elIsOpen2.textContent.trim(), 'false');

  });

  test('clicking on it fires the appropriate callback on the table parent', async function(assert) {
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

    await render(hbs`{{rsa-group-table/group-header
    group=group
    index=index
    table=table
    }}`);

    await click('.rsa-group-table-group-header');
    assert.equal(whichAction, 'groupClickAction', 'Expected click handler to be invoked');

    await click('.rsa-group-table-group-header', { shiftKey: true });
    assert.equal(whichAction, 'groupShiftClickAction', 'Expected SHIFT click handler to be invoked');

    // eslint-disable-next-line new-cap
    await click('.rsa-group-table-group-header', { ctrlKey: true });
    assert.equal(whichAction, 'groupCtrlClickAction', 'Expected CTRL click handler to be invoked');

  });
});