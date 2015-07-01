# Adding a component to the dashboard addon

For now, addons do not support POD structure, so the files must be placed 
in separate directories.

## Create a template

Place the component's template in the ``dashboard/app/templates/components`` 
directory, with a filename that matches the name of your component: 
``your-component.hbs``

```html
<button class="rsa-test" {{action 'incrementCount'}}>RSA Test - {{count}}</button>
```

## Create a component
 
Create a file ``dashboard/addon/your-component/component.js``.  This file 
will contain the logic for the Ember component.

```javascript
import Ember from 'ember';

/**
 * A sample component to use a guide for creating new components.
 */
export default Ember.Component.extend({
    count: 0,

    actions: {
        incrementCount: function() {
            this.incrementProperty('count');
        }
    }
});
```

## Export the component

To make the component available in the downstream application (the 
application that requires the dashboard addon), another file must be
created: ``dashboard/app/rsa-test/component.js``.  This file simply
exports the component defined in the addon and allows the downstream
application to override/extend it.

```javascript
import RsaTest from 'dashboard/rsa-test/component';

/**
 * Exporting the addon component into the app provides the downstream app the
 * ability to extend it.
 */
export default RsaTest;
```

## Create a test

Unit tests for components should be placed in ``dashboard/tests/unit/components/your-component-test.js``

```javascript
import { moduleForComponent, test } from 'ember-qunit';

moduleForComponent('rsa-test', 'Unit | Component | rsa-test', {
    // Specify the other units that are required for this test
    // needs: ['component:foo', 'helper:bar'],
    unit: true
});

test('it renders', function (assert) {
    assert.expect(2);

    // Creates the component instance
    var component = this.subject();
    assert.equal(component._state, 'preRender');

    // Renders the component to the page
    this.render();
    assert.equal(component._state, 'inDOM');
});

test('clicking the button increments the count', function (assert) {
    assert.expect(2);

    var component = this.subject();
    this.render();

    assert.equal(component.get('count'), 0);

    this.$().find('button').click();

    assert.equal(component.get('count'), 1);
});
```

