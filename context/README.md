# Introduction

This Ember addon enables the use of the NetWitness Context Hub in Ember apps. It provides the consuming app with the following:

* an **Ember Component** (`context-panel`) that fetches and displays detailed Context data from server for a given entity;

* an **Ember Mixin** (`highlights-entities`) that enables a Component to highlight any entities in its DOM and wires them up to tooltips; and

* an **Ember Component** (`context-tooltip`) that leverages the `context` Service to display summary-level data about a given entity in a tooltip UI;

* an **Ember Service** (`context`) that fetches Context-related data from server.

The sections below discuss each of these topics above in more detail.

*Note:* To include any code from this addon in your app/engine/addon, you must first define this addon as a dependency for your app/engine/addon as usual.  For `sa-ui`, those changes were introduced in [this PR](https://github.rsa.lab.emc.com/asoc/sa-ui/pull/1146).


# The Context Panel Component

_A standalone Ember Component that fetches and displays detailed context data for a given entity._

## How to use

In order to include the Context Panel in your Ember UI,  insert the `{{context-panel}}` component in the `template.hbs` file of your Route (or Component), and specify the `entityId` & `entityType` attributes as strings:

```hbs
{{context-panel entityId=entityId entityType=entityType}}
```

In the `sa` app, the `{{context-panel}}` is included in the `protected` route's template. In the `respond` engine's `dummy` app, the `{{context-panel}}` is included in the `application` route's template. These changes were introduced in [this PR](https://github.rsa.lab.emc.com/asoc/sa-ui/pull/1154).

## Outstanding questions/issues

* It seems `context-panel` may throw errors if either `entityId` or `entityType` are empty/null/undefined. Therefore it is recommended to wrap it in an `{{#if}}` block as shown in [this PR](https://github.rsa.lab.emc.com/asoc/sa-ui/pull/1162).

* Need a way to specify which tab of the Context Panel to display.



# The Highlights Entities Mixin

_Enables a Component to highlight the entities mentioned in its DOM, and wire them to the Context Tooltip Component._

## Overview

This Mixin equips a Component with a method for finding the entities (if any) mentioned in its DOM, decorating those DOM nodes with CSS classes, and then wiring up those found DOM elements to the `{{context-tooltip}}` Component.

It asssumes any entities mentioned in the DOM should match a configurable querySelector (`entitySelector`). Any DOM elements that don't match the selector will be ignored.  For example, if `entitySelector` is set to `.entity`, then it would match:

```html
<span class="entity" ..>10.20.30.40</span>
```

It also assumes each matching DOM element must have an HTML string attribute `data-entity-id` to identify the value of the entity (e.g., IP address, username, domain name, hostname, etc). Additionally, to identify the type of the entity (e.g., "IP", "USER", "DOMAIN", "HOST", etc), the DOM element must have either:

  - (a) a `data-entity-type` HTML string attribute, such as:

  ```html
  <span class="entity" data-entity-id="10.20.30.40" data-entity-type="IP">10.20.30.40</span>
  ```

  - or (b) a `data-meta-key` HTML string attribute which identifies the meta key that the data value corresponds to, such as:

  ```html
  <span class="entity" data-entity-id="10.20.30.40" data-meta-key="ip.src">10.20.30.40</span>
  ```

In the latter scenario (b), the Component's `entityEndpointId` attr must be set to the id of the endpoint (concentrator/broker) from which the meta value was fetched.  Both the meta key & the endpoint id are required to validate the entity in this case.

Note that any DOM elements found by this mixin and determined to be enabled for context-lookups will be checked for an `id` attribute.  Those elements which don't have an `id` will be assigned an automatically generated id like `rsa-entity-<#>` where `<#>` is an auto-generated number.  

## Highlighting with CSS

Although this Mixin is called "HighlightsEntities", it doesn't actually highlight anything strictly speaking.  It merely applies CSS classes with certain specific names. However it does not define the styling rules for those CSS classes; those are to be defined by the consuming app.

For each DOM node that is found to match the `entitySelector` attr (see above), the following CSS class names may be applied:

* `entity-has-been-validated`: Indicates that the Mixin has compared this DOM node to the list of valid enabled entity types.
* `is-context-enabled` or `is-not-context-enabled`: Indicates whether or not this DOM node corresponds to a valid enabled entity type.
* `has-context-data`: Indicates that context summary-level data has been successfully received from server for this DOM node.  This is applied asynchronously as data is streamed back to the client.

## How to use

To use the Mixin in your Components, you must:

1. Apply the Mixin to your Component;

2. Mark your Component's entities DOM;

3. Configure some Component attrs.

The process is explained in more detail below.  You can also see examples of how this process was applied to Components in the `respond` UI in the PR's for [Storyline](https://github.rsa.lab.emc.com/asoc/sa-ui/pull/1159), [Entities nodal diagram](https://github.rsa.lab.emc.com/asoc/sa-ui/pull/1161), [Events table](https://github.rsa.lab.emc.com/asoc/sa-ui/pull/1163) and [Event Details](https://github.rsa.lab.emc.com/asoc/sa-ui/pull/1164).

(1) *Apply the Mixin to your Component:*

  In your `component.js` file, import the Mixin from `context/mixins/highlights-entities` and apply it to your Component class, like so:

  ```js
  // file: my-component/component.js
  import HighlightsEntities from 'context/mixins/highlights-entities';
  export default Component.extend(HighlightsEntities, {
    ..
  });
  ```

(2) *Mark your Component's entities DOM:*

  For every DOM node in your Component that might be a context-enabled entity, apply an `entity` CSS class.  If you choose to use a different CSS class name, just set the Component's `entitySelector` attr to match your custom CSS class name (see next step below).

  Additionally, for each entity DOM node, define an `data-entity-id` HTML attribute, plus either a `data-entity-type` or `data-meta-key` attribute.

(3) *Configure some Component attrs:*

  Most importantly, you will typically want to set the Component's `autoHighlightEntities` attr to `true`.  This attr is set to `false` by default.  Setting it to `true` will trigger the Mixin's functionality after the Component's `didInsertElement`.  This is useful for simple Components that do not typically change their DOM after `didInsertElement`.  If you don't set `autoHighlightEntities` to `true`, you will need to explicitly call the Mixin's `highlightEntities()` method from your Component's code whenever you want the Mixin to do its business.  This is useful for Components who change their DOM after some custom logic (e.g., after a data model is updated).

  If you chose to use a CSS class name other than `entity` for your entities DOM in the previous step, set your Component's `entitySelector` attr to match that CSS class. Additionally, if you chose to use `data-meta-key`, set your Component's `entityEndpointId` to specify the ID of the Concentrator/Broker from which the meta originated.

  Additionally, you can customize the configuration of the context tooltip using the optional attrs `entityTooltipTriggerEvent`, `entityTooltipDisplayDelay`, `entityTooltipHideDelay` and `entityTooltipPanelId`.

  ```js
  // file: my-component/component.js
  import HighlightsEntities from 'context/mixins/highlights-entities';
  export default Component.extend(HighlightsEntities, {

    // automatically invoke Mixin after didInsertElement
    autoHighlightEntities: true,

    // selector for entity DOMs; default = '.entity'
    entitySelector: '.foo',

    // only used for `data-meta-key` HTML attrs
    entityEndpointId: 'CONCENTRATOR-1',

    // event name: 'click', 'contextmenu' or 'hover' (default)
    entityTooltipTriggerEvent: 'hover',

    // pause in millisec; only used for 'hover'; default = 1000
    entityTooltipDisplayDelay: 0,

    // pause in millisec; only used for 'hover'; default = 1000
    entityTooltipHideDelay: 0,

    // `panelId` of your custom rsa-content-tethered-panel; default = 'context-tooltip-1'
    entityTooltipPanelId: 'my-custom-tooltip-1',

    ..
  });
  ```


# The Context Tooltip Component
_A tooltip Ember Component for displaying summary-level data for a given entity, and for launching the Context Panel._

## How to use

To add the tooltip to your Ember project, insert the `{{context-tooltip}}` Component in a template of the Component or Route that will contain entities DOM.  For maximum efficiency and to avoid duplication, it is desirable to put the tooltip as high up in its Route hierarchy as practical. Avoid having multiple declarations of `{{context-tooltip}}` in your Route.  If multiple declarations are unavoidable, then provide each `{{context-tooltip}}` with a unique `panelId` attr.

The `context-tooltip` Component supports an optional configurable `clickDataAction` attr. This attr can be set to an action which will be invoked whenever the user clicks on the data in the tooltip's UI.  The action (if any) will receive two arguments:

 - `entity`: an object with `type` and `id` properties; and

 - `record`: the data record which was clicked.

 In the `sa` app and in the `respond` engine's `dummy` app, we use `clickDataAction` to fire an action that reveals the Context Panel Component.

The `context-tooltip` Component is typically used in conjunction with the `highlights-entities` Mixin described in the earlier section above.  The tooltip is not typically called directly in the consuming app's Component code; rather, the Mixin is applied to those Components and it handles the summoning of the tooltip.


# The Context Service

_An Ember Service for fetching Context-related data from server._

## Overview

The Context Service (named `context`) is a client-side [Ember Service](http://emberjs.com/api/classes/Ember.Service.html) (not to be confused with a web-server-side Java service). That is, it is an Ember object that lives for the duration of the application and can be made available to many different parts of the application.

The Context service exposes an API for retrieving context-related information from server, such as:

- the list of known entity types (e.g., "IP", "DOMAIN", etc.);
- mapping of meta keys to entity types (e.g., "ip.src" -> "IP");
- summary-level context data about a given entity (e.g., the count of Incidents, Alerts & Feeds in which the entity was included as of some time frame);
- detailed context data about a given entity (e.g., the actual lists of those Incidents, Alerts & Feeds).

This information can then be leveraged by the UI in a variety of ways, such as:

- the UI can highlight names of entities as "context enabled";
- the UI can present summary-level context data in a tooltip (or custom UI);
- the UI can present detailed context data in the Context panel (or custom UI).

## How to use

Typically the consuming app will not need to use the `context` Service directly; rather it will use the `highlights-entities` Mixin and `context-tooltip` Component, which in turn leverage the `context` Service.  However, if needed, the Service can be injected the same way any Ember Service can, by using the `service()` method from `ember-service/inject`, as illustrated below:

```js
// file: my-component.js
import service from `ember-service/inject`;

export default Component.extend({
  // Inject the context service:
  context: service(),
  ..
});
```

The `context` Service exposes the following API methods:

### `types()`: `Promise`

Requests the list of known entity types.  

Use this async call to fetch the Array of context-enabled entity types. The Context Hub maintains a configurable list of "entity types"; that is, types of data objects for which the Context Hub can query data.  A typical list may look as follows:

```js
[ 'IP', 'HOST', 'USER', 'DOMAIN', 'FILE', .. ]
```

The `types()` method returns a `Promise` which will resolve with an Array of entity types when the request completes successfully:

```js
// file: my-component.js
this.get('context').types()
  .then((types) => {
    // do stuff here with the types array
    console.log(types);
    ...
  });
```

Note that the `types()` method caches its results (if successful), so it can be called repeatedly without requiring multiple round trips to server.  It can cache because it assumes that the `types` configuration is not likely to change frequently within a user's session of Netwitness UI. If the configuration were to change in mid-session, the Netwitness UI would need to be refreshed in order for the change to take effect.


### `metas(endpointId)`: `Promise`

Requests a mapping of meta keys to entity types for a given NetWitness Core endpoint device (i.e., Concentrator or Broker).

Use this async call in order to convert a meta key name to an entity type.  The mapping is configurable per endpoint, so this call takes the endpoint's ID as an input param. The call fetches a mapping from the server with a structure similar to the following example:

```js
{
  code: 0,
  data: [
    {
      type: 'IP',
      enabled: true,
      keys: ['ip.src', 'ip.dst', 'ipv6.src', 'ipv6.dst', .. ]
    }, {
      type: 'HOST',
      enabled: true,
      keys: ['hostname.alias', .. ]
    },
    ..
  ]
}
```

After fetching the JSON structure above from server, the `metas()` method transforms the JSON into a simple flat hash map for ease of use, such as the example below:

```js
{
  'ip.src': 'IP',
  'ip.dst': 'IP',
  'ipv6.src': 'IP',
  'ipv6.dst': 'IP',
  ..,
  'hostname.alias': 'HOST',
  ..
}
```

Note that the hash map would only include hash keys for meta key names that are mapped to enabled entity types.  

The `metas()` method returns a `Promise` which will resolve with a hash map like the one above when the request completes successfully.  With that hash map, it is straightforward to lookup the entity type for a given meta key, as follows:

```js
// file: my-component.js
const metaKeys = [ .. ];

this.get('context').metas(myEndpointId)
  .then((metasMap) => {

    // do stuff here with the hashmap
    metaKeys.forEach((metaKey) => {
      const entityType = metasMap[metaKey];
      console.log(`${metaKey} maps to ${entityType}`);
      ...

    });
  });
```

As with the `types()` method, the `metas()` method caches its results so it can be called multiple times with the same `endpointId` without requiring multiple server round-trips. This caching is done because we assume that the configuration will not change very frequently during a user's Netwitness UI session.

This method is useful for UIs such as Investigate or Recon, which render meta values from a Netwitness Core device.  Using the mapping from this method, those UIs can determine which meta values should enable Context lookups.  


### `summary(entities, callback)`: `Function`

Requests a stream of summary-level context data records  for a given list of entities.
Returns a stop function to cancel the request.

Use this async call to fetch summary-level context data for one or more entities. Each entity is specified as an object with both `type` and `id` properties. The `type` must be set to one of the valid entity types (see `types()` section above). The `id` must be set to an entity identifier (an IP address, user name, host name, domain, etc). Below is an example to illustrate:

```js
// file: my-component.js

// Define a callback to receive the context data records as they stream to client.
const callback = (type, id, records) => {
  console.log(type, id, records);
  ..;
};

// Kick off the stream, and cache a reference to the stream's stop function.
this.stop = this.get('context').summary([
  { type: 'ip', id: '10.20.30.40' },
  { type: 'ip', id: '11.21.31.41' },
  { type: 'user', id: 'user1' },
  { type: 'domain', id: 'g00gle.com' }  
], callback);
```

The summary-level data is an Array of Objects; each Object has `name`, `count`,
`severity` & `lastUpdated` properties; for example:

```js
// Sample summary-level data from context service for a given entity:
[
  // Count of incidents related to the given entity
  { name: 'incidents', count: 1, severity: null, lastUpdated: 1479998598247 },
  // Count of alerts related to the given entity
  { name: 'alerts', count: 2, severity: null, lastUpdated: 1479998598247 },
  // Um, not sure what this is... some ECAT data?
  { name: 'machines', count: null, severity: 'Critical', lastUpdated: 1479998598247 },
  ...
]
```

The data records can come from several asynchronous data sources in the backend, therefore they are streamed to the UI, meaning that they may arrive in multiple batches rather than all at once.

Each time one or more record arrives, the given `callback` (if any) is invoked with 3 input parameters:
- `type` (String): the entity type,
- `id` (String): the entity id, and
- `records` (Object[]): the Array of all the entity's summary data records that have arrived so far.

Since the summary-level data may come from multiple sources, its retrieval is inherently stream-like. On the server tier, the Context Hub service may query these sources in parallel and stream records to the browser client as they are found.  The structure of these server responses may resemble batches like this example below:

```js
// Sample JSON response from server:
{
  code: 0,
  data: {
    "10.20.30.40" : { // entity id
      "type" : "IP",  // entity type
      "data" : [  // summary-level data records
        {
          "name" : "incidents",
          "count" : 1,
          "severity" : null,
          "lastUpdated" : 1479998598247
        },
        ..
      }]
    },
    ..    // next entity
  }
}
```

However, on the client tier the Context Ember Service will assemble these records into a cache, grouping the records by entity type & id, and then notify the appropriate `callback`.
The `callback` will be notified each time a batch of data arrives for the requested entities.  To return to the earlier example of 3 records, the `callback` may be invoked up to 3 separate times, with a growing list of `records` each time:

```js
// First time callback is invoked, `records` is:
[
  { name: 'incidents', count: 1, severity: null, lastUpdated: 1479998598247 },
]

// Second time callback is invoked, `records` is:
[
  { name: 'incidents', count: 1, severity: null, lastUpdated: 1479998598247 },
  { name: 'alerts', count: 2, severity: null, lastUpdated: 1479998598247 },
]

// Third time callback is invoked, `records` is:
[
  { name: 'incidents', count: 1, severity: null, lastUpdated: 1479998598247 },
  { name: 'alerts', count: 2, severity: null, lastUpdated: 1479998598247 },
  { name: 'machines', count: null, severity: 'Critical', lastUpdated: 1479998598247 },
]
```

The `summary` method returns a function which can be used to stop any further invocations to `callback`. The stop function does not require any arguments.  To illustrate, the example below shows how the stop function can be used if the UI is only interested in the incidents count:

```js
// Define a callback that will call stop once it receives the incidents count.
const callback = (type, id, records = []) => {
  if (records.findBy('name', 'incidents')) {
    this.stop();
  }
};

// Start streaming data.
this.stop = this.get('context').summary('ip', '10.20.30.40', callback);
```

This stop function is particularly useful when tearing down or resetting a UI. For example, if a Component calls `summary` for a specific entity, then that Component may want to call the stop function when either (a) the Component's model is reset to a different entity, or (b) the Component is destroyed.

Note that `summary` may be called without a `callback`.  In that case, `summary` will still fetch the requested data and cache in the client Ember Service.  This is useful for background loading or "pre-fetching" data before it is needed by the UI:

```js
// Start fetching data in background:
this.stop = this.get('context').summary('ip', '10.20.30.40');
```

## Outstanding questions

* How does the client know when the `summary()` stream is completed?
* How is the time frame for the `summary()` call determined?
