# FAQ: Ember Components

## What is an Ember Component?

In Ember, a Component is essentially a view + controller, packaged together as a reusable standalone object.  The view defines the DOM, while the controller defines the logic.

Unlike Ember Controllers (which are singletons), Ember Components can be instantiated multiple times. Each instance of the component renders its own separate DOM (which can be referenced by the component instance's `element` property).

## How do I create a Component ?

You need to create a couple of files for your component.  By convention, Ember expects the following Component files to be at `app/components/<component-name>`:
* `component.js`: This is javascript file exports a class that defines the logic for each component instance (essentially, the "controller" of the component).
* `template.hbs`: The HTML template for the component (essentially, the "view" for the component).

You shouldn't create these files manually. Instead, ask Ember CLI create them for you in the command line, like this:

```
ember generate component <component-name>
```

The advantages of using Ember CLI are that:

1. It will put the files in the property subfolder according to Ember's "pod structure" paradigm.

2. It will also add a little bit of boilerplate to the files to help you get started.

3. It will also generate some boilerplate files for automated tests for your component (in the `/tests` subdir of your Ember project).

Note that Ember Component names [must have at least one dash](https://guides.emberjs.com/v2.3.0/components/defining-a-component/).  Ember requires this in order to avoid collisions with future HTML element names.

## How do I use a Component in my Ember app?

Once you have implemented a component, you can use it in any of your HTML templates like a custom HTML tag. For example, if you implement a component call "rsa-tree-diagram" then you could include it in any HTML template like this:

```hbs
<h1>Welcome</h1>
<p>Here is the diagram:</p>
{{rsa-tree-diagram prop1=val1 prop2=val2 .. }}
```

A few things to note in the snippet above:

* You can use double-curly-braces to denote a component. Starting in Ember 2.1, you can also use angle brackets (`<rsa-tree-diagram ..>`). However, keep in mind that using angle brackets will change your component's bindings to be 1-way (rather than 2-way) by default; and it will require you to put your component's public attributes in an `attrs` hash. For more details, see [this post](http://emberjs.com/blog/2015/05/10/run-up-to-two-oh.html).

* You can assign property values to your component, just like setting HTML attributes. In fact, Ember frequently refers to such properties as your component's "attributes".  Your `component.js` file is responsible for defining what exactly these attributes do.

## Do I need to import a Component to use it?

No. To use the component, simply include it in a `template.hbs` (for example, in the `template.hbs` for a Route).  Ember will detect the component's name in your template and automatically load it from its expected location (`app/components/<component-name>/`).

## How do I dynamically instantiate a Component in JavaScript?

That's not how Components are used in Ember apps.  That is an "imperative" approach.  Ember prefers a "declarative" approach, where Components are "layed out" in template files.

## Can I choose which Component to use at run-time?

Yes, Ember provides mechanisms for doing this in your templates.

Typically your templates will include components statically, like this:

```hbs
<h1>Your Data</h1>
{{my-bar-chart}}
```
But in some cases, your app may want to choose a component at run-time depending on some variable.  In simple cases this can be accomplished with {{if}} conditions, like this:

```hbs
<h1>Your Data</h1>
{{#if isBarChart model=myData}}
  {{my-bar-chart}}
{{else if isLineChart model=myData}}
  {{my-line-chart}}
{{else if isAreaChart model=myData}}
  {{my-area-chart}}
{{/if}}
```

For more sophisticated logic, Ember provides a `{{component}}` helper. This helper will take as input a variable or function which must resolve to the name of a component at run-time.  For example, suppose the container for your Component (i.e., a parent Component or a Route) has a function `whichTypeOfChart` which returns a Component name at run-time. Then your container's `template.hbs` can use the `{{component}}` helper like this:

```hbs
<h1>Your Data</h1>
{{component whichTypeOfChart model=myData}}
```

If we wanted to, we could even pass in arguments into `whichTypeOfChart` by using parenthesis for nesting, like this:

```hbs
<h1>Your Data</h1>
{{component (whichTypeOfChart myData myPreferences) model=myData}}
```

See the [Ember docs](http://emberjs.com/api/classes/Ember.Templates.helpers.html#method_component) for more details.

## How do I define the root DOM node for my Component?

The root DOM node is not in the Component's `template.hbs`.  Rather, all the DOM in `template.hbs` will be wrapped into a single root DOM node, which will be a `<div>` by default.  However, you can overwrite this default in your `component.js` by setting the Component's `tagName` property (e.g., `tagName: "section"`).

You can also specify the root DOM node's CSS class and HTML attributes in your `component.js` file.  See the [EmberJS Guides](http://guides.emberjs.com/v2.2.0/components/customizing-a-components-element/) for details.

## Can my Component just be a single DOM node without any child nodes?

Yes. Your Component's `template.hbs` file will be empty in this case.  The DOM node's tag name, CSS classes and HTML attributes will be specified in your `component.js` file.

## Can I use jQuery to do DOM manipulation in my Component?

You can, but typically that is not necessary.

In Ember, your Component DOM typically changes because some Component attribute changed. To set that up, you would use [binding](http://guides.emberjs.com/v2.2.0/templates/handlebars-basics/) in the Component's `template.hbs`.  This is a "declarative" approach that keeps your DOM (`template.hbs`) somewhat separate from your Component's logic (`component.js`).

With that said, there are scenarios in which you may want finer control over the DOM. In those cases, you might leave some/all of your `template.hbs` empty and use JavaScript to manipulate DOM.  A good example of this might be a [d3 visualization](http://d3js.org/) with specific animation requirements.  But even visualizations can be implemented with a `template.hbs` in simple cases that don't require sophisticated animation.

## How do I write code that will execute after my Component is rendered?

Use the `didInsertElement` hook, like this:

```js
// file: app/components/my-component.js:
import Ember from 'ember';

export default Ember.Component.extend({

  didInsertElement(){

      // stuff to do after the initial render..

  }

});
```
Note that if a Component has child (nested) components, the parent's `didInsertElement` hook is called after all the children's `didInsertElement` hooks are called.

You can read more about the Ember Component lifecycle hooks in the [Guides](https://guides.emberjs.com/v2.3.0/components/the-component-lifecycle/).

## How do I write code that will cleanup when my Component is about to be destroyed?

Use the `willDestroyElement` hook, like this:

```js
// file: app/components/my-component.js:
import Ember from 'ember';

export default Ember.Component.extend({

  willDestroyElement(){

      // clean up before your component DOM is destroyed..

  }

});
```

Typically you don't need to handle cleanup explicitly in your code. If you use Ember's computed properties and data binding, Ember will handle the cleanup for those things automatically for you. But if your code does things outside of the standard Ember techniques, then you are responsible for the cleanup.  For example, if your code uses jQuery to attach DOM listeners, then you are responsible for detaching those listeners during the cleanup stage.

## How do I write code that will execute when an attribute of my Component is changed?

Use the `.observes()` extension, like this:

```js
// file: app/components/my-component.js:
import Ember from 'ember';

export default Ember.Component.extend({

  foo: false,

  fooDidChange: function(){

      // stuff to do when 'foo' changes
      if (this.get('foo')) {
        ...
      }
      else {
        ...
      }

  }.observes('foo')

});
```

# How do I make my Component clickable? How do I handle DOM events?

Ember offers some mechanisms to enable this.

First of all, if you want your Component to respond to certain DOM events (e.g., 'click'), you can simply assign the Component a property using that event name, like this:

```js
// file: app/component/my-component.js:

import Ember from 'ember';

export default Ember.Component.extend({
    click(e){
      // stuff to do when user clicks on your component
    },
    doubleClick(e){
      // stuff to do when user double clicks on your component
    },
    mouseEnter(e){
      // stuff to do when user brings the mouse over your component
    },
    ...
});
```

Note that the event names used above are not the standard DOM event names; they are Ember reserved keywords. For example, the Ember name 'doubleClick' is used instead of the DOM name 'dblclick'. [Read here](http://guides.emberjs.com/v2.2.0/components/handling-events/) to learn more.

# What are Component actions?

In Ember, actions are named behaviors. Your Component can invoke an action from it's `template.hbs` by using the `{{action}}` helper.

As shown in the previous sample above, you are not required to define actions for interactivity.  The above example works in simple cases, such as when your entire Component responds to all clicks with the same handler, regardless of where exactly the Component's DOM was clicked.  A simple button is a good example of such a case.

But in other cases, you may want your Component to take different actions depending upon which specific part of its DOM was targeted.  For such scenarios, you can use the `{{action}}` helper in your Component's template.

You can place the `{{action ..}}` helper on any DOM node that should trigger an action.  The helper will then invoke the action that you specify in the helper.

For example, the snippet below will invoke one of the Component's actions, either 'ok' or 'cancel':

```hbs
<p>Do you wish to continue?</p>
<button {{action "ok"}}>OK</button>
<button {{action "cancel"}}>Cancel</button>
```

When the buttons above are clicked, Ember will look for either an 'ok' or 'cancel' action in your `component.js`.  These are defined inside an 'actions' hash, like this:

```js
// file: app/components/my-component.js:
import Ember from 'ember';

export default Ember.Component.extend({
  actions: {
    ok() { .. },
    cancel() { .. }
  }
});
```

The `{{action}}` macro can even pass input arguments into the action functions.  See [Ember docs](http://guides.emberjs.com/v2.2.0/templates/actions/) for more details.

# Can I set my Component's action dynamically, rather than hard-code it?

Yes, you can make your actions be configurable.

For example, suppose we have want a generic "Confirmation Dialog" Component that has "OK" and "Cancel" buttons. We want to use this Component in many places in our app, but the result of clicking OK will be different in each place.  Therefore, we don't want to hard-code the actions logic in the `component.js`. Instead, we want the Component to be generic, and we want the container of the Component to specify what those actions should do.

We can implement the above as follows. We change the "ok" and "cancel" actions in the `component.js` file so that they lookup the Component's "onOk" and "onCancel" attributes, respectively:

```js
// file: app/components/confirmation-dialog.js:
import Ember from 'ember';

export default Ember.Component.extend({
  onOK: null,
  onCancel: null,
  actions: {
    ok() {
      var fn = this.get('onOk');
      fn && fn();
    },
    cancel() {
      var fn = this.get('onCancel');
      fn && fn();
    }
  }
});
```

Then, in the `template.hbs` of the Component's container, we assign the Component instance some "onOK" and "onCancel" attributes. To illustrate, let's pretend that:
* If the user clicks OK, we want to call the container's save method with some data.
* Otherwise if the user clicks Cancel, we want to reset the container's "isSaving" attribute to false.

```hbs
{{confirmation-dialog
  onOk=(action "save" myData target=this)
  onCancel=(action "set" "isSaving" false target=this)
  }}
```

The syntax above can look a little confusing, because there are two things going on:

1. It uses parenthesis to nest expressions. This is necessary here because there are blank spaces in the values for onOk and onCancel.

2. It uses a flavor of the `action` helper that is different from the more typical `{{action}}` flavor.  The `(action ..)` flavor is useful for specifying a ([curried](http://www.sitepoint.com/currying-in-functional-javascript/)) function.  

  * In our `onOk` example above, it is specifying the function named `"save"` on the target `this` (i.e., the owner of this template), and it is passing in the argument `myData`.  

  * In our `onCancel` example above, it is specifying the function named `"set"` on the target `this`, and passing in the arguments `"isSaving"` and `false`.  

This is an example of an advanced usage of actions, which is a recent enhancement to Ember.  For more details read [this RFC](https://github.com/emberjs/rfcs/blob/master/text/0050-improved-actions.md).

# How do I define the root DOM element of my Component?

Ember supports various component attributes for this, such as `tagName`, `classNames`, `classNameBindings` and `attributeBindings`.  To learn more about them, read [this Guide](https://guides.emberjs.com/v2.3.0/components/customizing-a-components-element/).

# Where can I find a collection of RSA Components to use in my app?

There is another Ember project called `component-lib` which houses a library of reusable components.  It currently lives in the directory `sa-ui/client/component-lib`.  You can browse the source code in the project's `addon/components` directory, or better yet, browse the library using the `sa-ui/client/style-guide` Ember app.
