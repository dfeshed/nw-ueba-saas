# rsa-context-menu

An ember addon to add any right-click-menu to your components.

## Installing

Need to update your package json with add on and ember-fetch dependency:
```bash
  "ember-addon": {
    "configPath": "tests/dummy/config",
    "paths": [
      "../rsa-context-menu"
    ]`
  }
  "dependencies": {
    "ember-fetch": "^3.4.4"
  }
```

In your application.hbs add the following:

```hbs
{{context-menu}}
```

WARNING: You need to add this to make the context-menu work, and should add it just once in your application.

## Usage

```hbs
{{#rsa-context-menu
  contextItems=contextItems
  contextDetails=scope
  contextSelection="Right clicked item"}}
  Right click here
{{/rsa-context-menu}}
```

### Using the mixin

This mixin is designed to add a context-menu to any component. Add it to your component like this:

```js
import rsaMenuMixin from 'rsa-context-menu';

export default Ember.Component.extend(rsaMenuMixin, {
  // your component properties
  
  _contextMenu(e) {
    // do anything before triggering the context-menu
  }
});

```

### Context items

Your component needs at least an array of contextItems, which should have a label and an action.

```js
export default Ember.Component.extend(rsaMenuMixin, {
  contextItems: [
    {
      label: 'do something',
      action(selection, details, event) { /* do something */ }
    }
  ]
});
```

### Label icons

:no_entry_sign: (Temporary removed from 0.2.0, back in 0.3.2)

You can optionally set an icon to show in front of the label. Just give the name of the icon.

```js
  contextItems: [
    {
      label: 'do something',
      icon: 'search',
      action() { /* do something */ }
    }
  ]
```

The icons that you can use are the one from font-awesome.
See [http://fontawesome.io/icons/](http://fontawesome.io/icons/) for the icons

### Sub actions

You can add as many sub-actions as you like, but keep in mind it could blow out of your screen ;-)

```js
  contextItems: [
    {
      label: 'multiple actions',
      subActions: [
        {
          label: 'sub action 1',
          action() { /* do something */ }
        }
      ]
    }
  ]
```

### Selection

This context-menu can even be used in case you have to pass an item to your action.
You should add it as the contextSelection. This could be one or multiple items.

```js
  contextItems: [
    {
      label: 'do something',
      action(selection) { /* do something with the selection */ }
    }
  ],
  
  contextSelection: { foo: 'bar' }
```

When it's an array of multiple items, the context-menu will show the amount of items you pass to the action.

### Details

If you want to pass some more details to your action, you can set is as the contextDetails.
It will be passed to the action as the second argument.

```js
  contextItems: [
    {
      label: 'do something',
      action(selection, details) { /* do something */ }
    }
  ],
  
  contextDetails: { foo: 'bar' }
```

### Disabled actions

When your item has no action and no sub-actions, it will be disabled by default.
Also you could disable it by yourself to add the disabled property.
This could be either a boolean or a function which gets the selection.

```js
  contextItems: [
    {
      label: 'foo',
      disabled: true
      action() { /* do nothing */ }
    },
    {
      label: 'bar',
      disabled(selection) {
        /* return disabled depending on selection */
      },
      action() { /* do something */ }
    }
  ]
```

## Configuration based action

Rsa-context-menu supports context actions from classic SA configuration. Any analyst can create new action with defined scope from context menu configuration screen. Please check details in classic SA guide for more details about how to create new action.

### Usages

Need not to pass context menu item in that case. As items will be taken from SA configuration.
```hbs
{{#rsa-context-menu
  contextDetails=scope
  contextSelection=contextSelection}}
  Mata Value
{{/rsa-context-menu}}
```
Need to add module name in Context Selection. 

```js
contextSelection: { moduleName: 'EventGrid', metaName: 'ip.src', metaValue: '10.10.10.10' }
```

Right click menu items will appaer based on moduleName and meta name. For same module different meta will show diffferent context actions.

### Limitations for configuration based context action

### Non supported actions

Some actions defined in classic SA cannot be supported for nw-ui. 

Current non support action id list:


```js 
File name:: /sa-ui/rsa-context-menu/addon/utils/non-url-actions-handler.js

export const nonSupportedActionList = [
  'contextServiceDefaultAction',
  'addToList',
  'malwareScanAction',
  'change-meta-view-ACTION_OPEN',
  'change-meta-view-ACTION_CLOSE',
  'change-meta-view-ACTION_AUTO',
  'change-meta-view-ACTION_HIDDEN',
  'reconstructionAction',
  'reconAnalysisAction'
]
```
Add action id in same json to avoid the same in nw-ui.

### Non supported action formats

Currently user can create url based or java script based actions from context menu action configuration. But nw-ui can support only url based actions. To support non url based OOTB action. One to one mapping need to be done in nw-ui.

Existing OOTB non url actions are handled. To support new non url action from nw-ui need to add new handler function.

To support new non url function just add function id and handler function in nonUrlBasedActions.

```js
File name:: /sa-ui/rsa-context-menu/addon/utils/non-url-actions-handler.js

export const nonUrlBasedActions = {
  drillDownNewTabEquals: ([selection], contextDetails) => {
    windowProxy.openInNewTab(buildInvestigateUrl(selection, '=', contextDetails));
  }
}
```

---