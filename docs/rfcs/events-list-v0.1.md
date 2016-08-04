# Overview

This document describes the client-side architecture for the first implementation of an Investigate route for the Netwitness 11 web UI client.

The Investigate UI may be compromised of several pieces. For this initial implementation, it will be composed of two screens:

* a landing page ("springboard"), and

* a results page.  

The purpose of the landing page is to help the analyst choose or create an appropriate query. The purpose of the results page is to show the event records found that match the query.

## Landing Page

The focus of this first implementation is on the results page, not the landing page. The design of the landing page will continue to evolve in parallel while the results page is being implemented, and should not be considered a blocker.  

As a temporary placeholder for the landing page, a simple query UI will be displayed:

* A list of Core services that are available to be queried (e.g., Concentrators, Brokers, etc).

* A list of time ranges that the analyst can query (e.g., Last 1 Hour, Last 24 Hours, etc).  Custom time ranges will not be supported in this first implementation.

* A textbox in which the user can type a filter condition.  In this first implementation, the textbox will be disabled.

* A button to start the query.

Once the analyst chooses a Core service and a time range, they push the button to start the query, which will take them to the Results page.

## Results Page

The results page shows the Core data which match a given query. Ultimately it will present the data in various ways, including:

* a breadcrumb header that describes the query,

* a side panel (on left) of meta values that are aggregated from the query's results;

* a main content area that displays the events matching the query; these events may be displayed in 1 or more of the following visualizations:

    - data table: a listing of each event record individually;

    - timeline: a timeline that plots the volume of events along a time axis;

    - geo: a geographical map that plots the events based on their latitude & longitude (if the geo data is available);

    - nodal: an entity-relationship graph that plots the users, IPs, hosts & domains mentioned in the events.

These various pieces above will be implemented in phases and added to the UI progressively.  The first piece that will be implemented is the data table of events.  The other pieces (breadcrumb, meta side panel, timeline, geo & nodal) will not be included in this initial implementation.

Additionally, in this first implementation, the data table will not support any interactions. Subsequent implementations will enhance the table with interactions, such as:

* the analyst may click on an indexed meta value in the data table order to perform a drill;

* the analyst may click a button to load the next batch of records (if any);

* the analyst may change the set of columns shown in the data table.

# Implementation: Investigate Route

## Feature Flag

A new feature flag named `show-investigate-route` will be defined for the entire route in the client file `config/environment.js`.  This flag will determine whether or not the route is included in the app's router map, and it will show/hide a link to the route in the app's main navigation header.  When shown, clicking that link will navigate the analyst to the Investigate Landing Page.

Initially, this feature flag will be turned off in production builds until the route's UI & backend are implemented.

## URL Structure

The investigate route will be defined in Ember as `protected.investigate`, which will be mapped to the URL `/investigate` at run-time.  The sub-routes `/investigate/index` and `/investigate/search` will correspond to the landing page & the query results page, respectively.

## Model

This implementation of the Investigate route will generally follow Ember's "data down actions up" (DDAU) philosophy.  The high-level state of the Investigate route UI will be "owned" by the `protected.investigate` route, which will also implement actions to change ("mutate") that state.  Generally speaking, components within in the route (and sub-routes) should not mutate the state directly; rather, they should trigger the route actions.  

The paradigm we will follow is somewhat similar to the [Flux pattern](https://facebook.github.io/flux/docs/overview.html), but with some Ember-y differences in the details:

  * **App state & store**: Like Flux, our DDAU pattern will favor a unidirectional data flow.  However, Flux also includes the concept of a global "store" that holds the app state. This could be implemented in Ember as a service across the app.  But for our first implementation, we will reduce the scope to just the Investigate route.  The "state" of the route will simply be a tree of nested `Ember.Object`s, which will be exposed by the route's `model()` hook.  This model is essentially an observable representation of the route's state.  

  Since the state is exposed by the route's `model()` hook, the sub-routes & templates can access it too.  Sub-routes can access it via `this.modelFor('protected.investigate')`. The sub-route can expose it to their templates in their corresponding `model()` hooks as well:

  ```js
  // file: protected/investigate/index/route.js
  model() {
    return this.modelFor('protected.investigate');
  }
  ```

  Templates can then access the state as simply `model`:

  ```hbs
  {{!file: protected/investigate/index/template.hbs}}
  {{rsa-form-select options=model.foo ..}}
  ```

  * **Dispatcher & actions**: As in Flux, components will generally avoid mutating the app state directly. Instead they will fire actions. However, rather than introducting a new global "dispatcher" for actions, we will simply leverage Ember's existing `send()` Action API.  The actions will be implemented as standard Ember actions on the `protected.investigate` route.  To help us keep the actions code organized, we will borrow a Redux idea: we will create a subfolder `protected/investigate/actions/` with an `index.js` file.  This file will simply import all the other `*.js` files in this subfolder, which will each implement actions.  This structure will help us separate our growing number of actions into meaningful groups.

# Implementation: Landing Page

## URL Structure

The landing page's URL will not consist of any dynamic segments or query parameters. It will simply be '/investigate/index'.

## Template

The template will simply display two pulldowns (service & time range), a disabled textbox and a button to start the query. Both a service selection & a time range selection are required to start the query.

## Model

The list of time ranges in the UI will be hard-coded, but the list of services must be fetched from the server. The services list can be loaded into the `protected.investigate` route's state by invoking a route action (e.g., `servicesGet`). This route action will be responsible for fetching the data from the server and storing it in `state.coreServices`.  If the landing page's `model()` hook returns the model for its parent route, then the landing page's template can access the services via `model.coreServices`.

Note that the list of services does not necessarily need to be reloaded every time the analyst navigates to the Landing page. We could either automatically refresh the list after a fixed interval, and/or offer the analyst a button in the UI for manually refreshing it.  In this first implementation, we will only load the list once for the duration of the user's session.

## Actions

The button that starts the query should fire an action on the Landing Page's route. This action is responsible for navigating to the results page's route, passing into that route the selected service's id and the start & end times for the query.  

The Landing Page's action is responsible for computing specific start & end times from the selected time range (e.g., "Last 24 Hours"). In this implementation, the times will be computed relative to the current time.  In subsequent implementations, the times will be computed relative to the last capture time of the selected service.  In other words, if the service has not been capturing data since 7 days, then "Last 24 Hours" refers to a period of time starting 8 days ago. That logic will require an additional call to the server.

# Implementation: Results Page

## URL Structure

The results page's URL will be comprised of a dynamic segment (`filter`), followed by optional query parameters:

```text
/investigate/query/<filter>?<param1>=<value1>&<param2>=<value2>&...
```

Above, the dynamic segment `<filter>` specifies the dataset to be fetched from the server. The optional parameters (after the `?`) provide additional information about the presentation of the data. More details below.

## URL Filter Syntax

The `filter` segment must specify the Core service and the time range to be queried. It can also include an (optional) meta filter. These pieces will be concatenated, in that order, by forward slashes (`/`):

```text
/* Without a meta filter: */
/investigate/query/<service_id>/<start_time>/<end_time>

/* With a meta filter: */
/investigate/query/<service_id>/<start_time>/<end_time>/<meta_filter>
```

The `service_id` is an alphanumeric string (e.g., "555d9a6fe4b0d37c827d402d"). The `start_time` & `end_time` are integers.  The times are specified as UTC time (in seconds).  For example:

```text
/investigate/555d9a6fe4b0d37c827d402d/1468860661/1468863661
```

Note that both `start_time` & `end_time` must be provided. If either is unbounded, a `0` should be provided:

```text
/* No lower bound on time; use the earliest capture time for start time: */
/investigate/555d9a6fe4b0d37c827d402d/0/1466595536653

/* No upper bound on time; use the latest capture time for end time: */
/investigate/555d9a6fe4b0d37c827d402d/1466591936653/0

/* All data from the service, no time bound: */
/investigate/555d9a6fe4b0d37c827d402d/0/0
```

Also note that `start_time` can be relative to `end_time`. This is specified by a negative number, which is assumed to be a count of seconds:

```text
/* No upper bound on time; lower bound is 60 seconds before latest captured data: */
/investigate/555d9a6fe4b0d37c827d402d/-60/0
```

The optional `meta_filter` is a string of one or more conditions for filtering the data.  In this initial implementation, any `meta_filter` will be ignored.  Subsequent implementations will add support for `meta_filter`.

## URL Query Parameters

The URL query parameters specify optional presentation settings.  In this first implementation, any URL query parameters will be ignored.  Subsequent implementations will progressively add support for various query parameters, such as:

  * the list of visualizations to be shown (e.g., data table, meta, timeline, geo map, etc);

  * the column group to be displayed in the data table; etc.

## Model

In Ember, typically a route's `model()` hook is responsible for transforming the URL's dynamic segment into a corresponding model object.  In our implementation, we want the incoming URL to be parsed into a query that we can execute. The Results page can then show the query results. However, the Results page will also need to show other things besides the query results, for example:

  * the list of Core services shown in the landing page;

  * a running list of all the queries the analyst has executed in this session; etc.

This information is all part of the Investigate route's state, which is exposed as the `model` for the parent route `protected.investigate`.  Therefore, the Result page's `model()` hook will return the parent route's model, which the Result page's template can then access.  But before returning that model, the `model()` hook will indeed parse the URL's dynamic segment into a query.  This query needs to be added to the app state.  According to the DDAU pattern, the `model()` hook shouldn't directly change the app state; so instead, it will send actions to its parent route, which will apply the state changes.

  Note: This implies that whenever we transition to the Results route via `transitionTo()`, we should always pass in either a dynamic segment string, or the model from the `protected.investigate` route.  We should not pass in some other model.  Doing that would cause the Results route's `model()` hook to be skipped and prevent it's template from accessing the Investigate route's state.


# Server API Calls

The following calls will be needed by the client to retrieve data from the web server. All of these calls will be made over a web socket:

  * List of available Core services: This list should include an ID, type (e.g., Broker, Concentrator, etc) and display name for each service.

  * Query: This is a wrapper for the Core SDK `query` call, which should stream back events as they are found. For optimum performance, no server-side sorting will be requested.  The UI will provide a hard-coded limit for the maximum number of records that should be sent back to the UI (e.g., 10,000) in order to avoid overwhelming the browser's memory usage.

  * Count: Similar to `query`, this call will return a count of the total number of event ids (`sessionid`) which match a given query criteria.  This call is useful in cases where the `query` call reaches the record limit (e.g., 10,000) and is therefore unable to return all the records which match the query.
