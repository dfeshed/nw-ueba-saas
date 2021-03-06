import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, find, findAll, render, settled } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const info = {
  format: 'UInt8',
  metaName: 'medium',
  flags: -2147482541,
  displayName: 'Medium',
  formattedName: 'medium (Medium)',
  isOpen: true
};
const groupKey = {
  name: 'medium',
  isOpen: true
};
const aliases = {
  medium: {
    32: 'Logs',
    33: 'Correlation'
  }
};
const values = {
  data: [
    {
      value: 'foo',
      count: 9821
    },
    {
      value: 'bar',
      count: 9638
    }
  ],
  status: 'complete',
  complete: true
};

const info2 = {
  format: 'UInt8',
  metaName: 'email',
  flags: -2147483631,
  displayName: 'Email',
  formattedName: 'email (Email)',
  isOpen: true
};
const groupKey2 = {
  name: 'email',
  isOpen: true,
  disabled: true
};
const aliases2 = {
  email: {
    32: 'Logs',
    33: 'Correlation'
  }
};
const values2 = {
  data: [
    {
      value: 'aaa',
      count: 123
    },
    {
      value: 'bbb',
      count: 456
    }
  ],
  status: 'complete',
  complete: true
};

module('Integration | Component | Key Values', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it renders key-values', async function(assert) {
    await render(hbs`{{
      meta-view/key-values
    }}`);
    assert.ok(find('.rsa-investigate-meta-key-values'));
  });

  test('it renders open and fires and action when toggled', async function(assert) {
    assert.expect(3);

    const info = { isOpen: true };
    const toggleAction = () => {
      assert.ok(true, 'Expected toggleAction to be invoked');
    };

    this.setProperties({
      info,
      toggleAction
    });
    await render(hbs`{{meta-view/key-values info=info toggleAction=(action toggleAction)}}`);
    assert.ok(find('.is-open'), 'Expected meta value body DOM to reflect open state');
    assert.ok(find('.js-toggle-open'), 'Expected to find DOM that will trigger the toggle action');
    await click('.js-toggle-open');

  });

  test('it renders closed and fires and action when toggled', async function(assert) {
    assert.expect(3);

    const info = { isOpen: false };
    const toggleAction = () => {
      assert.ok(true, 'Expected toggleAction to be invoked');
    };

    this.setProperties({
      info,
      toggleAction
    });
    await render(hbs`{{meta-view/key-values info=info toggleAction=(action toggleAction)}}`);
    assert.notOk(find('.is-open'), 'Expected hidden meta value body DOM to reflect closed state');
    assert.ok(find('.js-toggle-open'), 'Expected to find DOM that will trigger the toggle action');
    await click('.js-toggle-open');

  });

  test('it renders values as expected', async function(assert) {
    assert.expect(6);
    this.setProperties({
      groupKey,
      aliases,
      info
    });
    await render(hbs`{{
      meta-view/key-values
        groupKey=groupKey
        values=values
        aliases=aliases
        info=info
    }}`);

    // set the values after rendering the component, otherwise it won't find .js-content
    // class to draw the D3
    this.set('values', values);

    return settled().then(() => {
      assert.ok(find('.rsa-investigate-meta-key-values__values.js-content'), 'Expected to find the class that is drawn by D3');
      const values = findAll('.rsa-investigate-meta-key-values__value');
      const counts = findAll('.rsa-investigate-meta-key-values__value-metric');
      assert.equal(values.length, 2, 'Meta key should have 2 values');
      assert.equal(values[0].getAttribute('title').trim(), 'foo', 'The first expected value');
      assert.equal(counts[0].textContent, 9821, 'Expected count for value foo');
      assert.equal(values[1].getAttribute('title').trim(), 'bar', 'The second expected value');
      assert.equal(counts[1].textContent, 9638, 'Expected count for value bar');
    });
  });

  test('it renders disabled values as expected', async function(assert) {
    assert.expect(6);
    this.setProperties({
      groupKey: groupKey2,
      aliases: aliases2,
      info: info2
    });
    await render(hbs`{{
      meta-view/key-values
        groupKey=groupKey
        values=values
        aliases=aliases
        info=info
    }}`);

    // set the values after rendering the component, otherwise it won't find .js-content
    // class to draw the D3
    this.set('values', values2);

    return settled().then(() => {
      assert.ok(find('.rsa-investigate-meta-key-values__values.js-content'), 'Expected to find the class that is drawn by D3');
      const values = findAll('.rsa-investigate-meta-key-values__value');
      const counts = findAll('.rsa-investigate-meta-key-values__value-metric');
      assert.equal(values.length, 2, 'Meta key shall have two values');
      assert.equal(values[0].getAttribute('title').trim(), 'aaa', 'The first expected value');
      assert.equal(counts[0].textContent, 123, 'Expected count for value aaa');
      assert.equal(values[1].getAttribute('title').trim(), 'bbb', 'The second expected value');
      assert.equal(counts[1].textContent, 456, 'Expected count for value bbb');
    });
  });

  test('it correctly sets the class for disabled values', async function(assert) {
    assert.expect(6);
    this.setProperties({
      groupKey: groupKey2,
      aliases: aliases2,
      info: info2
    });
    await render(hbs`{{
      meta-view/key-values
        groupKey=groupKey
        values=values
        aliases=aliases
        info=info
    }}`);

    // set the values after rendering the component, otherwise it won't find .js-content
    // class to draw the D3
    this.set('values', values2);

    return settled().then(() => {
      assert.ok(find('.rsa-investigate-meta-key-values__values.js-content'), 'Expected to find the class that is drawn by D3');
      const disabledValues = findAll('.rsa-investigate-meta-key-values__value.disabled');
      const counts = findAll('.rsa-investigate-meta-key-values__value-metric');
      assert.equal(disabledValues.length, 2, 'The disabled meta key shall have two values');
      assert.equal(disabledValues[0].getAttribute('title').trim(), 'aaa', 'The first expected value');
      assert.equal(counts[0].textContent, 123, 'Expected count for value aaa');
      assert.equal(disabledValues[1].getAttribute('title').trim(), 'bbb', 'The second expected value');
      assert.equal(counts[1].textContent, 456, 'Expected count for value bbb');
    });
  });

  test('it triggers action when a value is clicked', async function(assert) {
    assert.expect(3);
    const clickValueAction = (meta, value) => {
      assert.ok(true, 'Expected clickValueAction to be invoked');
      assert.equal(meta, 'medium', 'Expected meta');
      assert.equal(value, 'foo', 'Expected value');
    };
    this.setProperties({
      groupKey,
      aliases,
      info,
      clickValueAction
    });

    await render(hbs`{{
      meta-view/key-values
        groupKey=groupKey
        values=values
        aliases=aliases
        info=info
        clickValueAction=(action clickValueAction)
    }}`);

    // set the values after rendering the component, otherwise it won't find .js-content
    // class to draw the D3
    this.set('values', values);

    return settled().then(async() => {
      const values = findAll('.rsa-investigate-meta-key-values__value');
      await click(values[0]);
    });
  });

  test('it does not trigger action when a disabled value is clicked', async function(assert) {
    assert.expect(0);
    const clickValueAction = () => {
      assert.notOk(true, 'clickValueAction shall not be invoked');
    };
    this.setProperties({
      groupKey: groupKey2,
      aliases: aliases2,
      info: info2,
      clickValueAction
    });

    await render(hbs`{{
      meta-view/key-values
        groupKey=groupKey
        values=values
        aliases=aliases
        info=info
        clickValueAction=(action clickValueAction)
    }}`);

    // set the values after rendering the component, otherwise it won't find .js-content
    // class to draw the D3
    this.set('values', values2);

    return settled().then(async() => {
      const values = findAll('.rsa-investigate-meta-key-values__value.disabled');
      await click(values[0]);
    });
  });
});
