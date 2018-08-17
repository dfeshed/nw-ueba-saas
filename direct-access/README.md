# direct-access

## TODO
- [ ] Add `{{dashboard-visual}}` components to `{{dashboard-view}}` for modules other than the decoder.
  - Files affected: [`app/components/dashboard-view/component.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/direct-access/app/components/dashboard-view/component.js), [`app/components/dashboard-view/template.hbs`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/direct-access/app/components/dashboard-view/template.hbs), [`/app/reducers/selectors/index.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/direct-access/app/reducers/selectors/index.js)
  1. Create a new selector for `isConcentrator`, `isBroker`, ...
  2. Add graphs or gauges for the desired stats to `app/components/dashboard-view/template.hbs`
  3. Put the new `{{dashboard-visual}}` components inside conditional blocks to only render when the appropriate device is currently running

## Introduction
`direct-access` is an Ember app which serves as a web interface for the core devices.

### Setting up the environment
1. Run `../scripts/welcome.sh`.
2. Open [`app/services/transport.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/direct-access/app/services/transport.js) and edit the *development* WebSocket URL to point to your core device.
3. In the `direct-access` directory, run `ember serve`.
4. Make your changes. The webpage will automatically reload.

### Testing
`direct-access` uses its own simpler version of `mock-server` that lives in `direct-access/da-mock-server`. To run tests locally, first navigate into that folder and run `node index.js`. Then, in a seperate terminal, navigate back to the `direct-access` root folder and run `ember exam`.

### Architecture
`direct-access` is built using many of the same tools as the other ember apps/engines in `sa-ui`. State is tracked in Redux, but there are a couple exceptions. (There are possible other exceptions, but these are the two biggest)
- The `{{dashboard-visual}}` component monitors stats passed to it, but does not update state when it receives updates, it just updates itself and passes the data through bindings to its child component. (This was not a design decision, but rather a consequence of a bug)
- `{{tree-view-operation-parameter}}` passes data up through actions to its parent.

There is one important service, [`app/services/transport.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/direct-access/app/services/transport.js) which controls all the WebSocket communication. It has two main methods, `send()` and `stream()`. To see usage examples, look at the action creators in [`app/actions/actions.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/direct-access/app/actions/actions.js)