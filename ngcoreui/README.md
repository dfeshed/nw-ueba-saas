# ngcoreui

## TODO
- [x] Add `{{dashboard-visual}}` components to `{{dashboard-view}}` for modules other than the decoder.
  - Files affected: [`app/components/dashboard-view/component.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/ngcoreui/app/components/dashboard-view/component.js), [`app/components/dashboard-view/template.hbs`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/ngcoreui/app/components/dashboard-view/template.hbs), [`/app/reducers/selectors/index.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/ngcoreui/app/reducers/selectors/index.js)
  1. ~~Create a new selector for `isConcentrator`, `isBroker`, ...~~
  2. Add graphs or gauges for the desired stats to `app/components/dashboard-view/template.hbs`
  3. Put the new `{{dashboard-visual}}` components inside conditional blocks to only render when the appropriate device is currently running
- [x] Tree operations cancel button
  - change button to `Clear` to clear (or reset to defaults) parameter controls
  - add `Cancel` button to results pane to stop a request that is in progress
- [ ] Transport needs to be updated to encapsulate error responses and not pass them to the message callback

  This should probably include close handler as well

- [ ] CRITICAL: Back/Forward/Refresh support

  This should probably include dropping into the tree view from any path in the tree based on the URL

- [ ] Add historical fetch to `card-devices` to populate graph
- [ ] Update tree view to be a console
- [ ] Add support for local user settings persistence
- [ ] Add build number in ngcoreui.sh to archive for production builds
- [x] Enhance graphs to use fixed y-axis range where appropriate (e.g cpu % -> 0-100)
- [ ] use rsa-content-datetime template for time value
- [ ] Internationalization, add compatibility with SA-UI i18n service
- [ ] Update existing cards to use the same graph population mechanisms as `card-devices`

## BUGS
- [ ] CRITICAL: page becomes unresponsive after time
- [ ] HIGH: offline awareness

  when the websocket connection is lost, the app still accepts input

- [ ] time values should be local to browser, not UTC
- Logs View
  - [x] it be broke
  - [x] show latest only applies filter to initial pull
  - match/regex
    - [ ] filter does not apply until control loses focus
    - [ ] filter not applied to new logs when `Show Latest` selected
  - [ ] change message wrap so that it does not wrap to begining of next line (wrap toggle?)
- [x] tree operation results panel only displays last message received
- [x] The popout menu on the left should have a “Shutdown” option

  This should probably be on the dashboard page and not the popout menu...

- [ ] the middle divider in the tree view should be adjustable
- [x] “Select Operations” drop down should have sorted options
- [ ] When filling in values for an operation, clicking “Enable” should be assumed if clicking on the control (ie. the text box that you “Enable”)
- [x] When utilizing an operation there should be a way to specify the entire string-param’d string, as in the classic REST interface
- Dashboard
  - [ ] cards should be fixed width (maybe?)
  - [ ] card property names/values wrap and flow into property immediately below, add clipping & '...'
  - [ ] monitor mixin pulls 10 minutes of data when anything over 6 *should* be sufficient
  - [ ] allow gaps in graphs where no values exist (e.g. capture not running)
  - [ ] show errors for action failures using notifications.js (standard errors SAStyle example not working)
  - [ ] `devices-card` last time needs to be formatted as a time value

## Introduction
`ngcoreui` is an Ember app which serves as a web interface for the core devices.

The artifact for ng-core can be manually built from the `sa-ui/ngcoreui` directory as follows:

    VERSION=0.1
    BUILDNO=0
    ARCHIVE_NAME=ngcoreui-${VERSION}.nw.${BUILDNO}.any.tar.bz2
    NODE_ENV=production ember build -e production
    tar -cyvf ${ARCHIVE_NAME} dist/*
    md5 -q ${ARCHIVE_NAME} | tr -d '\n' > ${ARCHIVE_NAME}.md5

(Replace version and build numbers as appropriate.)

### Setting up the environment
1. Run `../scripts/welcome.sh`.
2. Open [`app/services/transport.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/ngcoreui/app/services/transport.js) and edit the *development* WebSocket URL to point to your core device.
3. In the `ngcoreui` directory, run `ember serve`.
4. Make your changes. The webpage will automatically reload.

### Testing
`ngcoreui` uses its own simpler version of `mock-server` that lives in `ngcoreui/ngcoreui-mock-server`. To run tests locally, first navigate into that folder and run `node index.js`. Then, in a separate terminal, navigate back to the `ngcoreui` root folder and run `ember exam`.

### Architecture
`ngcoreui` is built using many of the same tools as the other ember apps/engines in `sa-ui`. State is tracked in Redux, but there are a couple exceptions. (There are possible other exceptions, but these are the two biggest)
- The `{{dashboard-visual}}` component monitors stats passed to it, but does not update state when it receives updates, it just updates itself and passes the data through bindings to its child component. (This was not a design decision, but rather a consequence of a bug)
- `{{tree-view-operation-parameter}}` passes data up through actions to its parent.

There is one important service, [`app/services/transport.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/ngcoreui/app/services/transport.js) which controls all the WebSocket communication. It has two main methods, `send()` and `stream()`. To see usage examples, look at the action creators in [`app/actions/actions.js`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/ngcoreui/app/actions/actions.js) or the docs in [`app/services/transport/README.md`](https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/ngcoreui/app/services/transport/README.md)
