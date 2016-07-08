# FAQ: Ember Data

## What is Ember Data?

[Ember Data](https://github.com/emberjs/data) is an Ember addon which attempts to organize & simplify the reading & writing of server-side data.

The idea behind Ember Data is to get away from spaghetti code that makes AJAX calls from various places and then receives responses as plain old JavaScript objects ('POJOs').  Instead, Ember Data attempts to centralize all the AJAX code in one layer ('adapters'), and asks developers to deal with instantiated data objects ('models') rather than POJOs.

A typical (simplified) workflow for fetching data via Ember Data looks something like this:

1. A route wants to fetch a list of Incidents from the web server.

2. Rather than calling $.ajax() directly, the Route uses the Ember Data 'store' API to request models of type 'incident'. The Route can provide optional filter criteria (e.g., IDs or a time range) to the store API.

3. Ember Data looks up the Adapter for the 'incident' model. The Adapter should implement a method that is responsible for the actual server communication (e.g., an AJAX GET/POST, a WebSocket message, whatever). Note that the Adapter might be model-specific, or it could be a default adapter shared across multiple types of models.

4. When the records return from the webserver, Ember Data wraps each record in an instance of the 'incident' model, which can be implemented to expose a friendly API that encapsulates any required business logic.

5. Ember Data also provides client-side caching of records once they are fetched from the server.

## What is a Model?

A [Model](http://guides.emberjs.com/v2.2.0/models/) is a class that represents a data record. Instances of the class are used to wrap data records once they are fetched from the server.

Models are a key concept in Ember Data. In Ember Data, server-side data is categorized into Model types. Each Model type has a name, such as 'incident', 'alert', 'event', etc.  When developers want to fetch data from the server, they talk to Ember Data's store and specify the name of the model type that they are interested in.

The Model class is responsible for defining metadata about the properties ('attributes') of the data records, such as data types and default values.  

For example, an Incident model's class can specify that each Incident instance has an 'isBreach' property of type boolean, and an 'alerts' property which is an array of Alert model instances, as illustrated below:

```js
// file: app/incident/model.js:

export default DS.Model.extend({
  isBreach: DS.attr('boolean', {defaultValue: false}),
  alerts: DS.hasMany('alert') // expects to find app/alert/model.js
})

// file: app/alert/model.js:

export default DS.Model.extend({
  incident: DS.belongsTo('incident') // expects to find app/incident/model.js
})

```

Note that Models do not define how to load the data from server.  That is done by Adapters.

## What is an Adapter?

An [Adapter](http://guides.emberjs.com/v2.2.0/models/customizing-adapters/) tells Ember how to read & write a Model's data from server.  Each Adapter is responsible for implementing Create/Read/Update/Destroy (CRUD) methods for its corresponding Model.

To define a custom Adapter from scratch, the developer should extend the abstract base class `DS.Adapter` and implement a minimum of 6 methods, as illustrated below.  These methods would typically each return a Promise:

```js
// file: app/incident/adapter.js:
import DS from 'ember-data';

export default DS.Adapter.extend({
  findRecord(..){ .. }, // lookup a single incident record by id
  createRecord(..){ .. }, // adds a single incident record
  updateRecord(..){ .. }, // edits an existing incident record
  deleteRecord(..){ .. }, // removes an existing incident record
  findAll(..){ .. },  // returns all incident records
  query(..){ .. } // returns any incident records that match a given filter
});
```

Note that Ember supports a default Adapter for the entire app. If we design a consistent communication protocol for our models to follow, then we can leverage a default Adapter rather than implementing a large number of model-specific Adapters.

## Does Ember support Promises?

Yes, Promises are used throughout Ember and Ember Data.

In fact, Ember has its own Promise implementation: [`Ember.RSVP.Promise`](http://emberjs.com/api/classes/RSVP.Promise.html).  

If you are used to using jQuery Deferreds and Promises, please note: Ember Promises support `.then()` and `.catch()`, not `.done()` and `.fail()`!

Here's an example of how to create a Promise in Ember:

```js
import Ember from 'ember';

// Define a promise that will always resolve with 'yay!' in 1000 milliseconds.
var p = new Ember.RSVP.Promise((resolve, reject) => {
  window.setTimeout(function(){
    resolve('yay!');
  }, 1000);
});
```
And here's an example of how to subscribe to that Promise above:

```js
p.then((response) => {
  // on fulfillment
}, (reason) => {
  // on rejection
});
```

## Should I use Ember Data?

There are pros and cons, depending on the scenario. You can choose to use Ember Data in some scenarios or features, while avoiding Ember Data in others.

### Pros:

* For server requests that are straightforward CRUD (Create/Read/Update/Destroy) operations over AJAX, Ember Data can help you keep your code organized.

* Ember Data will wrap your JSON data records into Model instances. That gives your app nice APIs for inspecting & editing the data.  If your app needs smart models with business logic and sophisticated relationships (1-to-many, many-to-many, etc), Ember Data will be useful.

### Cons:

* If your data is essentially read-only and logic-less, you may not need to instantiate models for it.

* More to the point, wrapping JSON records into models consumes memory and takes time. If you are loading large volumes of JSON records, you may not want to instantiate a model for each and every record.   

* Minor nit-pick: If you're using WebSockets, you'll have to do some extra work, because Ember Data was initially designed with AJAX in mind.

### Our experience so far:

Below, we describe how we are using Ember Data in our Ember UI so far. This description illustrates a hybrid approach that combines websockets, plain old JSON data, and Ember Data Models.

The workflow for our Security Analytics UI has been as follows:

1. The UI loads potentially large volumes of data from the backend (e.g., lists of incidents, alerts and/or events) for viewing.
2. If the user wants to edit data, the user selects some piece of data (e.g., a specific incident) to edit.
3. The user makes some edits and persists them to the backend.

Let's look at how data is moved and manipulated in each of these steps:

1. When we loading the data, we prefer the user experience of streaming the data in, rather than paging through the data.  Therefore we use a websocket stream for the data load.  We like the way Ember Data uses Adapters to keep AJAX code for fetching data in a centralized place.  Therefore, we have customized our application's Adapter to support fetching various types of data records via websocket streams.

  Note that Ember Data supports an application-level Adapter, as well as individual model-type-specific Adapters. In other words, we could implement one Adapter for Incident Models, another Adapter for Alert Models, etc.  Each of those Adapters would then be responsible for fetching records of its specific Model type.  However, we have not found a need for that yet.  Instead, we have adopted a standard convention for fetching any type of record via websocket stream.  The protocol is essentially the same for all record types; the only real differences across the different types are the socket URLs & destinations.  Therefore, we have implemented an application-level Adapter that uses our websocket protocol to load any type of record.  The Adapter simply needs to be told what type of record is being requested; it then looks up the socket URLs & destinations appropriate for that record type in the app's configuration file (`config/environtment.js`).

  Since the initial data volume may be large, it would be both time consuming & memory consuming to wrap all of the loaded data records into Ember Data Models.  Therefore, when we load the data records we initially store them as Plain Old JavaScript Objects (POJOs). Our Adapter does not automatically wrap the individual records as Models.

2. If the user were to edit a record (i.e., an Incident), we would like to leverage the Ember Data Model paradigm for tracking edits, enforcing validations, and persisting changes to the backend. That means that we need to instantiate a Model for the record to be edited.  Therefore, we do this in an on-demand or 'lazy' fashion.  Specifically, if the user wants to edit a record (i.e., an Incident) they must perform some UI action first (e.g., a click).  When this occurs, the UI will instantiate a Model for that record.  This Model is then fed to the editor UI for user interaction.  

  Note that Ember Models are not responsible for communicating with the backend. They delegate to Adapters for that.  What's more, an Ember Model will automatically talk to the application-level Adapter if it does not find a Model-specific Adapter. Therefore, we can (and did) implement an Incident Model without needing to implement an Incident Adapter.

3. Once the user is done editing a record, they must perform some action (e.g., a click) to save their changes. At that point, the editor UI asks the Model to save its changes. Internally the Ember Model will talk to an Ember Adapter, which is responsible for making the appropriate backend call(s).  We have customized our application's Adapter to support persisting a record edit via a websocket call. (Note: In this case, an AJAX call would suffice as well. We've simply chosen to use a websocket here for now since we already have that infrastructure implemented.)
