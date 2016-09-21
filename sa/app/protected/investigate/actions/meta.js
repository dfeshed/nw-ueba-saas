/**
 * @file Investigate Route Meta Actions
 * Route actions related to fetching/manipulating the meta data for the current Core query.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';
import MetaKeyState from '../state/meta-key';
import { buildMetaValueStreamInputs, executeMetaValuesRequest } from './helpers/query-utils';

const { get, set, Mixin } = Ember;

const STREAM_LIMIT = 1000;
const STREAM_BATCH = 19;

// Maximum number of parallel threads that fetch meta values.
// Eventually this will be a configurable Admin setting in the UI. For now, use default from Classic UI.
const MAX_JOBS_QUEUE_SIZE = 2;

export default Mixin.create({
  actions: {
    metaGet(queryNode, forceReload = false) {
      // Ensure we have one state object per each possible meta key's request for values.
      let metaKeyStates = queryNode.get('value.results.metaKeyStates');
      if (!metaKeyStates.length) {
        let states = queryNode.get('value.language.data').map((info) => {
          return MetaKeyState.create({
            info
          });
        });
        metaKeyStates.pushObjects(states);
      }

      // Kick off the fetching of meta values for the currently selected meta group.
      this.send(
        'metaGroupValuesGet',
        queryNode.get('value.defaultMetaGroup'),
        queryNode,
        metaKeyStates,
        forceReload
      );
    },

    /**
     * Submits "jobs" to retrieve values for open meta keys in a given meta group.
     * Only the currently open keys in the group will have their values fetched.
     * Note that a job queue limit is enforced, in order to avoid executing too many jobs in parallel.
     * This method is invoked whenever a meta group is selected, or a meta group key is opened, or whenever a job
     * finishes up (in order to kick off the next job now that the queue has freed up some room).
     * @param {object} group The meta group whose open keys' values are to be fetched.
     * @param {object} queryNode The query for which meta values are to be fetched.
     * @param {object[]} metaKeyStates Array of state objects; each represents the state of a request for the
     * values of a specific key for the given `queryDefinition`.
     * @param {boolean} [forceReload=false]
     * @public
     */
    metaGroupValuesGet(group, queryNode, metaKeyStates, forceReload = false) {
      const queryDefinition = queryNode && queryNode.get('value.definition');

      // Abort any pending jobs for past queries that are no longer the current query.
      this._metaJobsRemoveObsolete(queryDefinition);

      if (!group || !queryDefinition || !metaKeyStates) {
        return;
      }

      let freeJobCount = MAX_JOBS_QUEUE_SIZE - this.get('state.meta.jobs.length');
      if (freeJobCount) {
        let candidates = group.keys
          .filterBy('isOpen', true)   // only fetch open keys
          .map((groupItem) => metaKeyStates.findBy('info.metaName', groupItem.name))  // map key name to key info
          .compact();   // discard group keys that don't match up to anything in the given language

        // @todo: Only fetch indexed meta keys
        // candidates = candidates.filterBy('info.isIndexed');

        if (!forceReload) {
          // Only fetch keys that haven't started yet
          candidates = candidates.filterBy('values.status', 'idle');
        }

        // Limit the size of parallel requests
        candidates = candidates.slice(0, freeJobCount);

        this._metaJobsAdd(queryNode, candidates);
      }
    },

    /**
     * Updates state to mark the given meta group (if any) as the currently selected group.
     * If a queryNode and group are both specified, then this method will trigger the fetching of the
     * meta values for that query, as the currently selected meta keys may have changed and thus might need to have
     * their data values fetched.
     * @param {object} [queryNode] The current query node, if any.
     * @param {object} [group] The newly selected group, if any.
     * @public
     */
    metaGroupSet(queryNode, group) {
      this.set('state.meta.group', group);
      if (queryNode && group) {
        this.send('metaGet', queryNode, false);
      }
    },

    /**
     * Toggles the `isOpen` state of a given meta group's key object.  If the key's new `isOpen` is truthy, and if a
     * queryNode is also given, kicks off the retrieval of meta values for that query.
     * @param {object} queryNode Represents the query for which meta values may be retrieved.
     * @param {object} key A member of a meta group object's `keys` array whose `isOpen` will be toggled.
     * @public
     */
    metaGroupKeyToggle(queryNode, key) {
      if (key) {
        let isOpen = !get(key, 'isOpen');
        set(key, 'isOpen', isOpen);
        if (queryNode && isOpen) {
          this.send('metaGet', queryNode, false);
        }
      }
    },

    /**
     * Updates the meta panel size state to a given value, then triggers the retrieval of data if needed.
     * @param {string} size Either 'min', 'max' or 'default'.
     * @public
     */
    metaPanelSize(size) {
      const wasSize = this.get('state.meta.panelSize');
      const sizeChanged = wasSize !== size;
      if (!sizeChanged) {
        return;
      }
      // When expanding meta panel from its minimized state, ensure recon panel is closed.
      if (wasSize === 'min') {
        this.send('reconClose', false);
      }

      this.set('state.meta.panelSize', size);

      // When opening meta panel or shrinking it from maximized, ensure results are fetched for all visible views.
      if (wasSize === 'min' || wasSize === 'max') {
        this.send('resultsGet', this.get('state.queryNode'));
      }
    }
  },

  // Kicks off stream requests for the values of a given set of keys for a given query definition.
  // These are recorded in the meta jobs queue array for future reference.
  _metaJobsAdd(queryNode, metaKeyStates) {
    const queryDefinition = queryNode && queryNode.get('value.definition');
    if (!queryDefinition || !metaKeyStates) {
      return;
    }

    // Kick off the stream requests.
    metaKeyStates.forEach((metaKeyState) => {
      this._metaKeyValuesGet(queryNode, metaKeyState);
    });

    // Register each request in "jobs" queue for future reference.
    this.get('state.meta.jobs').pushObjects(
      metaKeyStates.map((metaKeyState) => {
        return {
          queryDefinition,
          metaKeyState
        };
      })
    );
  },

  // Removes entries from the meta jobs queue array.
  // Exiting entries are cancelled if they are still streaming.
  _metaJobsRemove(jobs) {
    if (jobs) {
      jobs.forEach((job) => {
        let values = job.metaKeyState.get('values');
        if (values.get('status') === 'streaming') {
          values.get('stopStreaming')();
        }
      });
    }
    this.get('state.meta.jobs').removeObjects(jobs);
  },

  // Removes entries from the meta jobs queue array if they don't match a given query definition or aren't streaming anymore.
  _metaJobsRemoveObsolete(queryDefinition) {
    if (!queryDefinition) {
      return;
    }
    let obsolete = this.get('state.meta.jobs')
      .filter((job) => !queryDefinition.isEqual(job.queryDefinition) || (job.metaKeyState.get('values.status') !== 'streaming'));
    this._metaJobsRemove(obsolete);
  },

  _metaKeyValuesGet(queryNode, metaKeyState) {
    const queryDefinition = queryNode.get('value.definition');
    const { values, options } = metaKeyState.getProperties('values', 'options');

    // Prepare values state object for a new request.
    values.setProperties({
      data: [],
      anchor: 0,
      goal: options.get('size')
    });

    const inputs = buildMetaValueStreamInputs(
      metaKeyState.get('info.metaName'),
      queryDefinition,
      queryNode.get('value.language.data'),
      metaKeyState.get('options'),
      STREAM_LIMIT,
      STREAM_BATCH
    );

    executeMetaValuesRequest(this.request, inputs, values)
      .finally(() => {
        // If the meta panel is still open AND the given node is still the current node, continue fetching next meta key.
        // But panel might not be open, and/or node might not be the current node anymore; user may have drilled or
        // navigated away or closed the meta panel during last fetch.
        if (
          (this.get('state.meta.panelSize') !== 'min') &&
          (this.get('state.queryNode') === queryNode)
        ) {
          this.send('metaGet', queryNode, false);
        }
      });
  }
});
